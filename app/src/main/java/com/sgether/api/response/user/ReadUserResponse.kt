package com.sgether.api.response.user

import com.sgether.model.User

data class ReadUserResponse(
    var message: String?,
    var userSelectReseult: List<User>?,
)
