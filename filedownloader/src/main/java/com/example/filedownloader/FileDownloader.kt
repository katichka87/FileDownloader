package com.example.filedownloader

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by kati4ka on 10/21/17.
 */
class FileDownloader(private val cache: Cache = Cache.newInstance((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8),
                     private val fileDownloads: FileDownloads = FileDownloads()) {

    fun getFile(fileUrl: String): Observable<ByteArray> {
        val downloaded = cache.getBitmapFromMemCache(fileUrl)
        if (downloaded !== null) {
            return Observable.just(downloaded)
        }

        val inProgress = fileDownloads[fileUrl]
        if (inProgress !== null) {
            return inProgress
        }

        return cacheFile(java.net.URL(fileUrl))

    }

    private fun cacheFile(fileUrl: java.net.URL): PublishSubject<ByteArray> {
        val fileSubject = PublishSubject.create<ByteArray>()
        fileDownloads.put(fileUrl.toString(), fileSubject)
        Observable.create<ByteArray> { subscriber ->
            try {
                val outputStream = UrlToByteArrayOutputStream(fileUrl)
                subscriber.onNext(outputStream.toByteArray())
                subscriber.onComplete()
            } catch (e: Throwable) {
                e.printStackTrace()
                subscriber.onError(e)
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    fileDownloads.remove(fileUrl.toString())
                    cache.addBitmapToMemoryCache(fileUrl.toString(), it)
                    fileSubject.onNext(it)
                }, {
                    fileDownloads.remove(fileUrl.toString())
                    fileSubject.onError(it)
                }, {
                    fileDownloads.remove(fileUrl.toString())
                    fileSubject.onComplete()
                })
        return fileSubject
    }

    private fun UrlToByteArrayOutputStream(fileUrl: java.net.URL): java.io.ByteArrayOutputStream {
        val connection = fileUrl.openConnection()
        connection.connect()
        val input = java.io.BufferedInputStream(fileUrl.openStream())
        val outputStream = java.io.ByteArrayOutputStream()
        val data = ByteArray(1024)
        val fileLength = connection.contentLength
        var total: Long = 0
        var count: Int
        var percent = 0
        do {
            count = input.read(data)
            if (count != -1) {
                outputStream.write(data, 0, count)
                total += count.toLong()
                val newPercent = (total * 100 / fileLength).toInt()
                if (fileLength > 0 && newPercent - percent > 5) {
                    percent = newPercent
                }
            }
        } while (count != -1)
        input.close()
        outputStream.flush()
        outputStream.close()
        return outputStream
    }
}