package com.sgether.utils

import android.os.Parcelable
import android.util.Base64
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

object  JWTHelper {
    private fun String.base64Decode(): ByteArray = Base64.decode(this, Base64.URL_SAFE)

    fun parseJwtToken(token: String): JwtToken {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format")
        }

        val headerJson = parts[0].base64Decode().decodeToString()
        val payloadJson = parts[1].base64Decode().decodeToString()
        val signature = parts[2]

        val header = Gson().fromJson(headerJson, JwtHeader::class.java)
        val payload = Gson().fromJson(payloadJson, JwtPayload::class.java)

        return JwtToken(header, payload, signature)
    }

    data class JwtToken(
        val header: JwtHeader,
        val payload: JwtPayload,
        val signature: String
    )

    data class JwtHeader(
        val alg: String,
        val type: String
    )

    @Parcelize
    data class JwtPayload(
        val iss: String,
        val id: String,
        val name: String,
        val authority: String,
        val iat: Long
    ) : Parcelable
}