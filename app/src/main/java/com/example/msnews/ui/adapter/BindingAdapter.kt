package com.example.msnews.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.msnews.R
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import com.example.msnews.data.utils.ExtensionFunctions.toFormattedDateAndTime
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
 * Use a BindingAdapter to initialize the ArticlesAdapter with the list of article objects.
 * Using a BindingAdapter to set the RecyclerView data causes data binding to automatically
 * observe the LiveData for the list of article objects. Then the binding adapter is called
 * automatically when the articles list changes.
 * */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Article>?) {
    val adapter = recyclerView.adapter as ArticlesAdapter
    adapter.submitList(data) // tells the RecyclerView when a new list is available.
}

@BindingAdapter(value = ["pagingListData", "scope", "lifecycle"], requireAll = true)
fun bindRecyclerView(recyclerView: RecyclerView, pagingListData: PagingData<Article>?, scope: CoroutineScope, lifecycle: Lifecycle) {
    val adapter = recyclerView.adapter as PagingArticlesAdapter
    scope.launch {
        // Since the paging list is LiveData, we use the non-suspend submitData
        pagingListData?.let {
            adapter.submitData(lifecycle, it)
        }
    }
}

@BindingAdapter("newsStatus")
fun bindStatusWithShimmer(shimmerFrameLayout: ShimmerFrameLayout, status: Resource<List<Article>>) {
    when (status) {
        is Resource.Success -> {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
        }
        is Resource.Loading -> {
            shimmerFrameLayout.visibility = View.VISIBLE
        }
        is Resource.Error -> {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
        }
    }
}

@BindingAdapter("networkStatus")
fun bindNetworkWithLayout(relativeLayout: RelativeLayout, networkStatus: Boolean) {
    when (networkStatus) {
        true -> relativeLayout.visibility = View.GONE
        false -> relativeLayout.visibility = View.VISIBLE
    }
}

@BindingAdapter("formattedText")
fun formatPublishedAtToDateAndTime(textView: TextView, time: String) {
    /* Assuming the function was not an extension function, this worked
    textView.text = time.let {
         ExtensionFunctions.toFormattedDateAndTime(it)
    }*/

    // textView.text = ExtensionFunctions.toFormattedDateAndTime(time)

    textView.text = time.toFormattedDateAndTime(time)
}
