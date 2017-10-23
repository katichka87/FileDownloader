package com.example.filedownloader.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*

class MainActivity : AppCompatActivity() {

    val presenter = Presenter()
    val adapter = ImageAdapter(ArrayList(), presenter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.isLoading.subscribe(RxView.visibility(loading_progress))

        val layoutManager = LinearLayoutManager(this)
        img_list.layoutManager = layoutManager
        img_list.adapter = adapter

        callLoadNext()
        val scrollListener = EndlessRecyclerViewScrollListener(layoutManager)
        scrollListener.loadMoreSubject.subscribe { callLoadNext() }
        img_list.addOnScrollListener(scrollListener)
    }

    fun callLoadNext(clear : Boolean = false) {
        presenter.loadNextPage().doOnError {
            it.printStackTrace()
            Toast.makeText(this, it.message ?: "Error getting data from network", Toast.LENGTH_SHORT).show()
        }
                ?.subscribe(adapter.addItems())
    }

    class ImageAdapter(images : ArrayList<String>, presenter : Presenter) : RecyclerView.Adapter<ImageAdapter.ItemViewHolder>() {
        private val images = images
        private val presenter = presenter

        fun addItems() : Consumer<List<String>> {
            return Consumer {
                val currentLen = images.size
                images.addAll(it)
                notifyItemRangeInserted(Math.max(0, currentLen - 1), images.size - 1)
            }
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            presenter.downloadImage(holder.image, images[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(parent?.context)
            val v = inflater.inflate(R.layout.list_item, parent, false)
            return ItemViewHolder(v)
        }

        override fun getItemCount(): Int {
            return images.size
        }

        inner class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val image = v.img
        }
    }
}

