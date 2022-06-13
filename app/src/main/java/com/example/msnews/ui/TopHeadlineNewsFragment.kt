package com.example.msnews.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.msnews.R

/**
 * A simple [Fragment] subclass.
 * Use the [TopHeadlineNewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TopHeadlineNewsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_headline_news, container, false)
    }
}
