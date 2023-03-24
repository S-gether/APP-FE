package com.sgether.api.response.group

import com.sgether.model.User

data class ReadGroupUserResponse(
    var message: String,
    var usersInfo: List<User>
)