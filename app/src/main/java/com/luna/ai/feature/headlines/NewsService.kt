package com.luna.ai.feature.headlines

import retrofit2.http.GET

interface NewsService {
    // এটি একটি ফ্রি নিউজ এপিআই, যা কোনো Key ছাড়াই কাজ করে
    @GET("top-headlines/category/general/in.json")
    suspend fun getTopHeadlines(): NewsResponse
}
