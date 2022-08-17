package com.example.msnews.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.msnews.databinding.ItemLoadingStateBinding
import com.facebook.shimmer.ShimmerFrameLayout

/**
 *  LoadStateAdapter is a special list adapter that has the loading state of the PagingSource.
 *  You can use it with a RecyclerView to present the loading state on the screen.
 * */

class ArticleLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ArticleLoadStateAdapter.LoadingStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val view = LayoutInflater.from(parent.context)
        return LoadingStateViewHolder(
            ItemLoadingStateBinding.inflate(view, parent, false), retry
        )
    }

    class LoadingStateViewHolder(binding: ItemLoadingStateBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        private val shimmerLoadingEffect: ShimmerFrameLayout = binding.shimmerFrameLayout
        private val errorFeedbackTxt: TextView = binding.feedbackError
        private val btnRetry: Button = binding.btnRetry

        init {
            btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bindState(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                errorFeedbackTxt.text = loadState.error.localizedMessage
            }
            shimmerLoadingEffect.isVisible = loadState is LoadState.Loading
            errorFeedbackTxt.isVisible = loadState is LoadState.Error
            btnRetry.isVisible = loadState is LoadState.Error
        }
    }
}
