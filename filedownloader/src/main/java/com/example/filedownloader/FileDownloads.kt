package com.example.filedownloader

import io.reactivex.subjects.PublishSubject

/**
 * Created by kati4ka on 10/21/17.
 */
open class FileDownloads : HashMap<String, PublishSubject<ByteArray>>() {

}