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
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.example.msnews.R
import com.example.msnews.data.model.Article
import com.example.msnews.databinding.FragmentSearchNewsBinding
import com.example.msnews.ui.adapter.ArticleListener
import com.example.msnews.ui.adapter.ArticleLoadStateAdapter
import com.example.msnews.ui.adapter.PagingArticlesAdapter
import com.example.msnews.viewmodels.NewsViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
                    if (it.isNotBlank())
                        searchNews(it)
                }
                return true
            }
        })

        // binding the recyclerview to the adapter
        /*binding.newsRecyclerView.adapter = ArticlesAdapter(
            ArticleListener { article ->
                sharedViewModel.onArticleClicked(article)
            }
        ) */
       /* binding.newsRecyclerView.adapter = PagingArticlesAdapter(
            ArticleListener { article ->
                sharedViewModel.onArticleClicked(article)
            }
        )*/

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

        binding.noInternetLayout.root.isVisible = false

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun searchNews(searchQuery: String) {
        // sharedViewModel.getSearchedNews(searchQuery, "en")
        sharedViewModel.getPagedSearchedNews(searchQuery, "en")

        /*sharedViewModel.listOfPagedSearchedArticles.observe(viewLifecycleOwner, Observer {
            setUpRecyclerView(it)
        })*/

        Toast.makeText(context, "Searched", Toast.LENGTH_LONG).show()
    }

    private fun setUpRecyclerView(pagingListData: PagingData<Article>) {
        articlesAdapter = PagingArticlesAdapter(
            ArticleListener { article ->
                sharedViewModel.onArticleClicked(article)
            }
        )
        binding.newsRecyclerView.adapter = articlesAdapter
        lifecycleScope.launch {
            // Since the paging list is LiveData, we use the non-suspend submitData
            articlesAdapter.submitData(lifecycle, pagingListData)
        }
    }
}
