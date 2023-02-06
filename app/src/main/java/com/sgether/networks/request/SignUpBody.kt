package com.sgether.networks.request

data class SignUpBody(
    var id: String,
    var pwd: String,
    var name: String,
    var residentNum: String,
    var authority: String,
    var email: String,
    var introduce: String,
)
