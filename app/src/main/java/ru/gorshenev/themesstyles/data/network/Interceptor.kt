package ru.gorshenev.themesstyles.data.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

object Interceptor : Interceptor {

    private const val EMAIL = "gorshmeister@gmail.com"
    private const val API_KEY = "Q449RzIFTSEeCOMGFclHxoiC8AtPRJft"

    override fun intercept(chain: Interceptor.Chain): Response {
       val request: Request = chain.request()
       val authRequest: Request = request.newBuilder()
           .header("Authorization",
               Credentials.basic(EMAIL, API_KEY)
           ).build()

       return chain.proceed(authRequest)
   }
}