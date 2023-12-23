package com.example.breakingnews.repository

import com.example.breakingnews.db.ArticleDatabase
import com.example.breakingnews.models.Article
import com.example.breakingnews.retrofit.RetrofitInstance

class NewsRepository(val db : ArticleDatabase) {

    suspend fun getHeadlines(countryCode : String, pageNumber : Int) =
        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    suspend fun searchHeadlines(searchQuery : String, pageNumber : Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDAO().upsert(article)

    fun getFavouriteNews() = db.getArticleDAO().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDAO().deleteArticle(article)

}