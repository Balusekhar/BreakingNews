package com.example.breakingnews.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.breakingnews.models.Article
import com.example.breakingnews.models.NewsResponse
import com.example.breakingnews.repository.NewsRepository
import com.example.breakingnews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import java.io.IOException


class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinedPage = 1
    var headlinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery:String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHedlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                headlinedPage++
                if (headlinesResponse == null) {
                    headlinesResponse = it
                } else {
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlinesResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }


    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = it
                } else {
                    searchNewsPage++
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = it.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    private fun hasInternetConnection(context: Context): Boolean {

        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private suspend fun headlinesInternet(countryCode: String){
       headlines.postValue(Resource.Loading())
       try {
           if(hasInternetConnection(this.getApplication())){
               val response = newsRepository.getHeadlines(countryCode,headlinedPage)
               headlines.postValue(handleHedlinesResponse(response))
           }else{
               headlines.postValue(Resource.Error("No Internet"))
           }
       }catch (t:Throwable){
           when(t){
               is IOException -> headlines.postValue(Resource.Error("Unable to Connect"))
               else-> headlines.postValue(Resource.Error("No signal"))
           }
       }
    }

    private suspend fun searchNewsInternet(searchQuery: String){
        Log.d("mySearch",searchQuery)
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection(this.getApplication())){
                val response = newsRepository.searchHeadlines(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to Connect"))
                else-> searchNews.postValue(Resource.Error("No signal"))
            }
        }
    }

}