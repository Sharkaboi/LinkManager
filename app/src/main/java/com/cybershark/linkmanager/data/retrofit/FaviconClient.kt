package com.cybershark.linkmanager.data.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Url

interface FaviconClient {

    @POST
    suspend fun getBodyFromUrl(@Url url: String): Call<ResponseBody>
}
