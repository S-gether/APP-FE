package com.sgether.util

import android.content.Context
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREF_AUTH)

fun loadGroupProfile(groupId: String, token: String, view: ImageView) {

    val url = GlideUrl(
        "${Constants.serverUrl}upload/group/${groupId}", LazyHeaders.Builder()
            .addHeader(Constants.KEY_AUTHORIZATION, "Bearer $token")
            .build()
    )

    Glide.with(view)
        .load(url)
        .circleCrop()
        .into(view)
}