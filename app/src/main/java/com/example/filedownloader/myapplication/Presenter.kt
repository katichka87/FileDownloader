package com.example.filedownloader.myapplication

import android.graphics.BitmapFactory
import android.widget.ImageView
import com.example.filedownloader.FileDownloader
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.json.JSONArray

/**
 * Created by kati4ka on 10/22/17.
 */
class Presenter {
    companion object {
        const val DOWNLOAD_URL = "http://pastebin.com/raw/wgkJgazE"
    }

    val downloader = FileDownloader()
    val isLoading : PublishSubject<Boolean> = PublishSubject.create()

    fun loadNextPage() : Observable<List<String>> {
        return downloader.getFile(DOWNLOAD_URL)
                .doOnSubscribe { isLoading.onNext(true) }
                .map { JSONArray(String(it)) }
                .doOnNext {
                    if (it.length() == 0) throw EmptyException()
                    isLoading.onNext(false)
                }
                .map { jsonArray ->
                    (0..jsonArray.length() - 1)
                            .map { jsonArray.getJSONObject(it) }
                            .map { it.getJSONObject("user").getJSONObject("profile_image").getString("large") }
                }
    }

    fun downloadImage(view : ImageView, url : String) {
        if (view.tag !== null) {
            (view.tag as Disposable).dispose()
        }
        view.setImageBitmap(null)
        val d = downloader.getFile(url).subscribe({
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bitmap)
        }, {
            it.printStackTrace()
        })
        view.tag = d
    }

    class EmptyException : Exception()
}