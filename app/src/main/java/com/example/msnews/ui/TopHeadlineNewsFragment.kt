package com.example.msnews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.msnews.R
import com.example.msnews.databinding.FragmentTopHeadlineNewsBinding
import com.example.msnews.ui.adapter.ArticleListener
import com.example.msnews.ui.adapter.ArticleLoadStateAdapter
import com.example.msnews.ui.adapter.PagingArticlesAdapter
import com.example.msnews.ui.adapter.bindRecyclerView
import com.example.msnews.viewmodels.NewsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
    private lateinit var articlesAdapter: PagingArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_top_headline_news, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = sharedViewModel
        binding.topHeadlineNews = this@TopHeadlineNewsFragment

        bindRecyclerView()

        binding.noInternetLayout.root.isVisible = false
        binding.shimmerFrameLayout.isVisible = false

        binding.btnInitialRetry.setOnClickListener {
            articlesAdapter.retry()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun bindRecyclerView() {

        sharedViewModel.listOfTopHeadlineArticles.observe(viewLifecycleOwner) { pagingListData ->

            articlesAdapter = PagingArticlesAdapter(
                ArticleListener { article ->
                    sharedViewModel.onArticleClicked(article)
                    findNavController()
                        .navigate(R.id.action_topHeadlineNewsFragment_to_newsDetailsFragment)
                    Log.e("News List", "Article clicked")
                }
            )
            // binding the recyclerview to the adapter
            binding.newsRecyclerView.adapter = articlesAdapter

            binding.newsRecyclerView.adapter = articlesAdapter.withLoadStateHeaderAndFooter(
                header = ArticleLoadStateAdapter { articlesAdapter.retry() },
                footer = ArticleLoadStateAdapter { articlesAdapter.retry() },
            )

            lifecycleScope.launch {
                articlesAdapter.loadStateFlow.collect { loadState ->
                    val isListEmpty =
                        loadState.refresh is LoadState.NotLoading && articlesAdapter.itemCount == 0
                    val isLoading =
                        loadState.source.refresh is LoadState.Loading || loadState.mediator?.refresh is LoadState.Loading
                    // show empty list
                    binding.emptyList.isVisible = isListEmpty
                    // Show shimmer effect during initial load or refresh.
                    binding.shimmerFrameLayout.isVisible = isLoading
                    Log.e("Load State", "$loadState")
                    Log.e("Load State: Loading", "$isLoading")
                    Log.e("Testing 4", "${articlesAdapter.itemCount}")
                    // Only show the list if refresh succeeds.
                    binding.newsRecyclerView.isVisible = !isListEmpty
                    // Show the retry state if initial load or refresh fails.
                    binding.btnInitialRetry.isVisible = loadState.source.refresh is LoadState.Error ||
                        loadState.mediator?.refresh is LoadState.Error && articlesAdapter.itemCount == 0
                }
            }
            lifecycleScope.launch {
                articlesAdapter.submitData(lifecycle, pagingListData)
            }
        }
    }

    fun navigateToSearchScreen() {
        Log.d("News List", "Onclick worked")
        Toast.makeText(context, "Nimeclickiwa", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_topHeadlineNewsFragment_to_searchNewsFragment)
    }
}
