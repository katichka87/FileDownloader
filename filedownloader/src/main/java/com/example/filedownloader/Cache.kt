package com.example.filedownloader


/**
 * Created by kati4ka on 10/21/17.
 */

open class Cache(cacheSize : Int) {

    companion object {
        fun newInstance(cacheSize : Int): com.example.filedownloader.Cache {
            return com.example.filedownloader.Cache(cacheSize)
        }
    }

    private val memoryCache = object : android.util.LruCache<String, ByteArray>(cacheSize) {
        override fun sizeOf(key: String, data: ByteArray): Int {
            return data.size / 1024
        }
    }

    open fun addBitmapToMemoryCache(key: String, data: ByteArray) {
        if (getBitmapFromMemCache(key) === null) {
            memoryCache.put(key, data)
        }
    }

    open fun getBitmapFromMemCache(key: String): ByteArray? {
        return memoryCache.get(key)
    }
}