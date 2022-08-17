package com.example.msnews.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.msnews.data.model.Article
import com.example.msnews.databinding.NewsViewItemBinding

class ArticlesAdapter(val clickListener: ArticleListener) :
    PagingDataAdapter<Article, ArticlesAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NewsViewHolder(
            NewsViewItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)
        article?.let { holder.bind(clickListener, it) }
    }

    class NewsViewHolder(var binding: NewsViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ArticleListener, article: Article) {
            binding.article = article
            binding.clickListener = clickListener
            binding.executePendingBindings() // causes the update to execute immediately.
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<Article>() {
        // decide whether two objects represent the same Item.
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        // check whether two items have the same data
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}

class ArticleListener(val clickListener: (article: Article) -> Unit) {
    fun onClick(article: Article) = clickListener(article)
}
