package com.sgether.networks.response

data class AuthResponse(
    var error: String?,
    var message: String?,
    var token: String?,
    var issue: String?,
    var id: String?,
)
