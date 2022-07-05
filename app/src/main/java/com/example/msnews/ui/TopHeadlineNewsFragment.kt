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
import androidx.navigation.fragment.findNavController
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

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = sharedViewModel
        binding.topHeadlineNews = this@TopHeadlineNewsFragment

        // binding the recyclerview to the adapter
        binding.newsRecyclerView.adapter = ArticlesAdapter(
            ArticleListener { article ->
                sharedViewModel.onArticleClicked(article)
                Log.e("News List", "Article clicked")
            }
        )

        binding.noInternetLayout.root.isVisible = false

        // Inflate the layout for this fragment
        return binding.root
    }

    fun navigateToSearchScreen() {
        Log.d("News List", "Onclick worked")
        Toast.makeText(context, "Nimeclickiwa", Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_topHeadlineNewsFragment_to_searchNewsFragment)
    }
}
