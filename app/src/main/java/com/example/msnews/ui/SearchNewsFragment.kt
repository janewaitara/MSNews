package com.example.msnews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.msnews.R
import com.example.msnews.data.model.Article
import com.example.msnews.databinding.FragmentSearchNewsBinding
import com.example.msnews.ui.adapter.ArticleListener
import com.example.msnews.ui.adapter.ArticleLoadStateAdapter
import com.example.msnews.ui.adapter.PagingArticlesAdapter
import com.example.msnews.viewmodels.NewsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [SearchNewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchNewsFragment : Fragment() {

    // lazy delegate property to inject shared ViewModel instance into a property using koin
    private val sharedViewModel: NewsViewModel by sharedViewModel()
    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var articlesAdapter: PagingArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_news, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = sharedViewModel

        binding.coroutineScope = viewLifecycleOwner.lifecycleScope
        binding.lifecycle = lifecycle

        binding.searchInputField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank())
                        searchNews(it)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) {
                        searchNews(it)
                    } else {
                        binding.newsRecyclerView.isVisible = false
                    }
                }
                return true
            }
        })

        // setUpRecyclerViewWithDataBinding()

        binding.noInternetLayout.root.isVisible = false
        binding.btnInitialRetry.isVisible = false
        binding.shimmerFrameLayout.isVisible = false
        binding.emptyList.isVisible = false

        // Back button
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setUpRecyclerViewWithDataBinding() {
        articlesAdapter = PagingArticlesAdapter(
            ArticleListener { article ->
                sharedViewModel.onArticleClicked(article)
            }
        )

        /**
         * The binding below is not working
         * Note:  With the free Api, I can only fetch 100 responses, meaning,on reaching the 100
         * responses I get an error.
         * With my current implementation of LoadStateAdapter, I assume whenever I get an error,
         * the error button shows but instead, the app crashes on reaching the 100.
         *
         * It says: {"status":"error","code":"maximumResultsReached","message":"You have requested
         * too many results. Developer accounts are limited to a max of 100 results.You are trying
         * to request results 100 to 120. Please upgrade to a paid plan if you need more results."}
         *
         * By default, the API returns 100 responses per page. I have changed the number of requests
         * made from the API from 100 to 30 to help me see whether the shimmer loading effect
         * will show but it doesn't.
         * Here I'm not sure whether it's my paging source with an issue or it's the footer
         * that is not working.
         *
         * I have tried this while not using dataBinding to submit data but it's still not working.
         * */
        // Bind the footer adapter with the list
        binding.newsRecyclerView.adapter = articlesAdapter.withLoadStateHeaderAndFooter(
            header = ArticleLoadStateAdapter { articlesAdapter.retry() },
            footer = ArticleLoadStateAdapter { articlesAdapter.retry() },
        )

        binding.newsRecyclerView.adapter = articlesAdapter
        Log.d("Recycler Items", "${articlesAdapter.itemCount}")

        /**
         * The problem here:
         * The expectations are:
         * 1. when the loadState is Loading, the shimmerFrame is visible but in this case it is not
         * 2. whenever I make too many requests to the Api, beyond the limitation, the retry btn
         * becomes visible. Instead, it crashes with the error: {"status":"error",
         * "code":"rateLimited","message":"You have made too many requests recently.
         * Developer accounts are limited to 100 requests over a 24 hour period (50 requests
         * available every 12 hours). Please upgrade to a paid plan if you need more requests."}
         *
         * solved the crashing by adding the elvis operator on line 57 of the pagingSource but
         * this makes the pagingListData become empty meaning the adapter itemCount is 0
         * satisfying the isListEmpty condition below which in my opinion should not be an
         * empty List state(No results) but an error. How best can I handle this?
         *
         * The emptyList showing works which is confusing because why are the others not working.
         * */
        // To know when the list was loaded we will use the PagingDataAdapter.loadStateFlow property.
        // This Flow emits every time there's a change in the load state via a CombinedLoadStates object.
        lifecycleScope.launch {
            articlesAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && articlesAdapter.itemCount == 0
                // show empty list
                binding.emptyList.isVisible = isListEmpty
                // Show shimmer effect during initial load or refresh.
                binding.shimmerFrameLayout.isVisible = loadState.source.refresh is LoadState.Loading

                Log.e("Load State", "$loadState")
                Log.d("Recycler Items", "${articlesAdapter.itemCount}")

                // Only show the list if refresh succeeds.
                binding.newsRecyclerView.isVisible = !isListEmpty
                // Show the retry state if initial load or refresh fails.
                binding.btnInitialRetry.isVisible = loadState.source.refresh is LoadState.Error
            }
        }
    }

    private fun searchNews(searchQuery: String) {

        sharedViewModel.getPagedSearchedNews(searchQuery, "en")

        /*sharedViewModel.listOfPagedSearchedArticles.observe(viewLifecycleOwner, Observer {
            setUpRecyclerView(it)
        })*/
        observePagingData()

        Toast.makeText(context, "Searched", Toast.LENGTH_LONG).show()
    }

    private fun observePagingData() {
        sharedViewModel.listOfPagedSearchedArticles.observe(
            viewLifecycleOwner,
            Observer {
                setUpRecyclerView(it)
            }
        )
    }

    private fun setUpRecyclerView(pagingListData: PagingData<Article>) {
        articlesAdapter = PagingArticlesAdapter(
            ArticleListener { article ->
                sharedViewModel.onArticleClicked(article)
                findNavController()
                    .navigate(R.id.action_searchNewsFragment_to_newsDetailsFragment)
            }
        )
        binding.newsRecyclerView.adapter = articlesAdapter

        binding.newsRecyclerView.adapter = articlesAdapter.withLoadStateHeaderAndFooter(
            header = ArticleLoadStateAdapter { articlesAdapter.retry() },
            footer = ArticleLoadStateAdapter { articlesAdapter.retry() },
        )

        lifecycleScope.launch {
            articlesAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && articlesAdapter.itemCount == 0
                val isLoading = loadState.source.refresh is LoadState.Loading
                // show empty list
                binding.emptyList.isVisible = isListEmpty
                // Show shimmer effect during initial load or refresh.
                binding.shimmerFrameLayout.isVisible = isLoading
                // Log.e("Load State", "$loadState")
                Log.e("Load State: Loading", "$isLoading")
                Log.d("Recycler Items", "${articlesAdapter.itemCount}")

                // Only show the list if refresh succeeds.
                binding.newsRecyclerView.isVisible = !isListEmpty
                // Show the retry state if initial load or refresh fails.
                binding.btnInitialRetry.isVisible = loadState.source.refresh is LoadState.Error
            }
        }

        lifecycleScope.launch {
            // Since the paging list is LiveData, we use the non-suspend submitData
            articlesAdapter.submitData(lifecycle, pagingListData)
        }
    }
}
