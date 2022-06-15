package com.example.msnews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.msnews.R
import com.example.msnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.msnews.ui.adapter.ArticleListener
import com.example.msnews.ui.adapter.ArticlesAdapter
import com.example.msnews.viewmodels.NewsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [TopHeadlineNewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TopHeadlineNewsFragment : Fragment() {

    // lazy delegate property to inject shared ViewModel instance into a property using koin
    private val sharedViewModel: NewsViewModel by sharedViewModel()
    private lateinit var binding: FragmentTopHeadlineNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_top_headline_news, container, false)

        // call the view model method that calls the news api
        sharedViewModel.getTopHeadlinesFromApi("health", "en")

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = sharedViewModel

        // binding the recyclerview to the adapter
        binding.newsRecyclerView.adapter = ArticlesAdapter(
            ArticleListener { article ->
                sharedViewModel.onArticleClicked(article)
                Log.e("News List", "Article clicked")
            }
        )

        // Inflate the layout for this fragment
        return binding.root
    }
}
