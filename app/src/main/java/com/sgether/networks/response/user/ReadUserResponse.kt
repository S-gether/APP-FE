package com.sgether.networks.response.user

data class ReadUserResponse(
    var message: String?,
    var userSelectReseult: List<User>?,
)

data class User(
    var id: String?,
    var user_id: String?,
    var email: String?,
    var introduce: String?,
    var created_at: String?,
    var updated_at: String?,
)
