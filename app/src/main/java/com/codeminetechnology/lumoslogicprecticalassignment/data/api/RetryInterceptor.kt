package com.codeminetechnology.lumoslogicprecticalassignment.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(private val maxRetries: Int) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var lastException: IOException? = null

        for (i in 0 until maxRetries) {
            try {
                val response = chain.proceed(chain.request())
                // If response is successful, return it
                if (response.isSuccessful) {
                    return response
                }
                // Close unsuccessful response before retrying to avoid resource leaks
                response.close()
            } catch (e: IOException) {
                lastException = e // Keep track of the last exception
            }
        }

        // If we reach here, retries are exhausted. Throw the last exception.
        throw lastException ?: IOException("Failed to get a successful response.")
    }
}
