package com.example.breakingnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.breakingnews.R
import com.example.breakingnews.databinding.ActivityNewsBinding
import com.example.breakingnews.db.ArticleDatabase
import com.example.breakingnews.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel:NewsViewModel
    private lateinit var binding: ActivityNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = NewsRepository(ArticleDatabase.createDatabase(this))
        val viewModelProviderFactory = NewsViewModelFactory(application,
            newsRepository)
        newsViewModel = ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)  as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}