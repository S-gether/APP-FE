package com.sgether.networks

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sgether.networks.service.*
import com.sgether.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitHelper {

    // 조금 더 자세한 로그를 위한 OkHttp 인터셉터
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // 토큰 자동 삽입을 위한 OkHttp 인터셉터
    private val interceptorChain: (Interceptor.Chain) -> Response = { chain: Interceptor.Chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header(
                "Authorization",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InRocmlmNjAiLCJuYW1lIjoi7LGE7ZmN66y0IiwiYXV0aG9yaXR5Ijoic3R1ZGVudCIsImlhdCI6MTY3NjM3MDgxOCwiaXNzIjoiYXBpLXNlcnZlciJ9.k1ESg7Qxz5SLKJ7nVXXFCc9VQWr9BItEDCe-otUvxvw"
            )
            .method(original.method, original.body)
            .build()

        chain.proceed(request)
    }
    
    // 인터셉터 연결을 위한 OkHttp 클라이언트
    private val client = OkHttpClient.Builder()
        .addInterceptor (interceptorChain)
        .addInterceptor(loggingInterceptor)
        .build()

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.serverUrl)
        .addConverterFactory(GsonConverterFactory.create(gson)) // JSON
        .addConverterFactory(ScalarsConverterFactory.create()) // Scalar
        .client(client)
        .build()
    
    val authService: AuthService = retrofit.create(AuthService::class.java)
    val groupService: GroupService = retrofit.create(GroupService::class.java)
    val joinGroupService: JoinGroupService = retrofit.create(JoinGroupService::class.java)
    val noticeService: NoticeService = retrofit.create(NoticeService::class.java)
    val userService: UserService = retrofit.create(UserService::class.java)
}