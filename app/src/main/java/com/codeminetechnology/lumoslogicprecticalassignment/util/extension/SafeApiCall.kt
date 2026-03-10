package com.codeminetechnology.lumoslogicprecticalassignment.util.extension

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.safeApiCall(
    onSuccess: (T?) -> Unit,
    onError: (Throwable) -> Unit
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                onSuccess(response.body())
            } else {
                onError(Exception("Error: ${response.code()}"))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onError(t)
        }
    })
}
