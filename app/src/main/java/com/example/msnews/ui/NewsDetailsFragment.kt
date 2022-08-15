package com.example.msnews.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.msnews.R
import com.example.msnews.databinding.FragmentNewsDetailsBinding
import com.example.msnews.viewmodels.NewsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewsDetailsFragment : Fragment() {

    private lateinit var binding: FragmentNewsDetailsBinding
    private val sharedViewModel: NewsViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_news_details, container, false)
        binding.viewModel = sharedViewModel

        // Inflate the layout for this fragment
        return binding.root
    }
}
