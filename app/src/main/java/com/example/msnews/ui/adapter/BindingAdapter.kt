package com.example.msnews.ui.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.msnews.R
import com.example.msnews.data.model.Article

/**
 *The first method parameter is the type of the target View and
 *  the second is the value being set to the attribute.
 *
 *  @BindingAdapter annotation tells data binding to execute this binding adapter
 *  when a View item has the imageUrl attribute.
 * */
@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        // convert the URL string to a Uri object using the toUri() method.
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        imageView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

/**
 * Use a BindingAdapter to initialize the PhotoGridAdapter with the list of MarsPhoto objects.
 * Using a BindingAdapter to set the RecyclerView data causes data binding to automatically
 * observe the LiveData for the list of MarsPhoto objects. Then the binding adapter is called
 * automatically when the MarsPhoto list changes.
 * */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Article>?) {
    val adapter = recyclerView.adapter as ArticlesAdapter
    adapter.submitList(data) // tells the RecyclerView when a new list is available.
}
