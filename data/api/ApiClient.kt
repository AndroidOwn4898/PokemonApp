package data.api

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ApiClient {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    fun getClient(): OkHttpClient {
        return client
    }
}