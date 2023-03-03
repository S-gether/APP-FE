package com.sgether.util

import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREF_AUTH)
suspend fun Context.toastOnMain(msg: String?) = withContext(Dispatchers.Main) {
    Toast.makeText(this@toastOnMain, msg, Toast.LENGTH_SHORT).show()
}


// 키값과 같은 상수를 저장하는 객체
object Constants {
    const val serverUrl = "http://3.35.166.15:8080/"
    const val testServerUrl = "https://reqres.in/api/"

    const val PREF_AUTH = "pref_auth"
    const val KEY_TOKEN = "token"

    const val TYPE_JOIN = "type_join"
    const val TYPE_OFFER = "type_offer"

    const val KEY_AUTHORIZATION = "Authorization"

    const val JWT_PAYLOAD = "jwt_payload"
}