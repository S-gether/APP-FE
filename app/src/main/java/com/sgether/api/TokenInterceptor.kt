package com.sgether.api

import com.sgether.util.Constants
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    var isInterceptorEnabled = true // 인터셉터 활성화/비활성화 플래그 변수
    lateinit var token: String

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInterceptorEnabled) {
            // 인터셉터가 비활성화되었을 때는 이곳에서 처리하지 않고
            // 다음 인터셉터로 연결합니다.
            return chain.proceed(chain.request())
        }
        // 인터셉터를 활성화할 때는 원래 동작을 수행합니다.
        val original = chain.request()
        val request = original.newBuilder()
            .header(
                Constants.KEY_AUTHORIZATION,
                token //"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InRocmlmNjAiLCJuYW1lIjoi7LGE7ZmN66y0IiwiYXV0aG9yaXR5Ijoic3R1ZGVudCIsImlhdCI6MTY3NjM3MDgxOCwiaXNzIjoiYXBpLXNlcnZlciJ9.k1ESg7Qxz5SLKJ7nVXXFCc9VQWr9BItEDCe-otUvxvw"
            )
            .method(original.method, original.body)
            .build()

        return chain.proceed(request)
    }
}