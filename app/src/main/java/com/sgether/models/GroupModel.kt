package com.sgether.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupModel(
    val id: String?,
    val master_id: String?,
    val room_name: String?,
    val accommodation: Int?,
    val room_pwd: String?,
    val pwd_flag: Int?,
    val created_at: String?,
    val updated_at: String?,
) : Parcelable