package com.sgether.ui.mypage

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgether.api.ApiClient
import com.sgether.util.toastOnMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MyPageViewModel(application: Application) : AndroidViewModel(application) {

    var applicationContext = application.applicationContext

    fun uploadImage(imageUri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val inputStream = applicationContext.contentResolver.openInputStream(imageUri)
            val file = File(applicationContext.externalCacheDir, "groupImage.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("upload", file.name, requestFile)

            val res = ApiClient.uploadService.uploadUserProfile(body)
            if(res.isSuccessful) {
                applicationContext.toastOnMain(res.message())
            } else {
                applicationContext.toastOnMain("이미지 업로드 실패")
            }
        } catch(e: IOException) {
            applicationContext.toastOnMain(e.toString())
        }

    }
}