package com.example.breakingnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.breakingnews.R
import com.example.breakingnews.databinding.FragmentArticlesBinding
import com.example.breakingnews.ui.NewsActivity
import com.example.breakingnews.ui.NewsViewModel

class ArticlesFragment : Fragment(R.layout.fragment_articles) {


    private lateinit var newsViewModel: NewsViewModel
    private val args: ArticlesFragmentArgs by navArgs<ArticlesFragmentArgs>()
    private lateinit var binding: FragmentArticlesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticlesBinding.bind(view)


        newsViewModel = (activity as NewsActivity).newsViewModel
        val article  = args.article

            binding.webView.apply {
                webViewClient = WebViewClient()
                loadUrl(article.url)
            }


        binding.fab.setOnClickListener {
                newsViewModel.addToFavourites(article)
            Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
            }
    }
}