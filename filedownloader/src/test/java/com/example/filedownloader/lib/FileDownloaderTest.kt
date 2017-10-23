package com.example.filedownloader.lib

import com.example.filedownloader.Cache
import com.example.filedownloader.FileDownloader
import com.example.filedownloader.FileDownloads
import com.example.filedownloader.RxSchedulersOverrideRule
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Matchers
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as given


/**
 * Created by kati4ka on 10/23/17.
 */
class FileDownloaderTest {

    @get:Rule
    val rxRule = RxSchedulersOverrideRule()

    lateinit var fileDownloader: FileDownloader
    lateinit var mockCache: Cache
    lateinit var mockFileDownloads: FileDownloads
    @Before
    fun setUp() {
        mockCache = mock(Cache::class.java)
        mockFileDownloads = mock(FileDownloads::class.java)
        fileDownloader = FileDownloader(mockCache, mockFileDownloads)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun ShouldReturnFileFromCacheIfExists() {
        given(mockCache.getBitmapFromMemCache("testUrl")).thenReturn("cachedFile".toByteArray())
        val res = fileDownloader.getFile("testUrl").blockingFirst()
        assertThat(res, `is`("cachedFile".toByteArray()))
    }

    @Test
    fun ShouldAddUrlToFileDownloadsHashTableBeforeStartingDownload() {
        fileDownloader.getFile("http://test.me/file")
        verify(mockFileDownloads, times(1)).put(eq("http://test.me/file"), Matchers.anyObject())
    }

}