package com.sgether.ui.group

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sgether.R
import com.sgether.databinding.ActivityAddGroupBinding
import com.sgether.networks.RetrofitHelper
import com.sgether.networks.request.group.CreateAndEditGroupBody
import com.sgether.utils.toastOnMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddGroupActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddGroupBinding.inflate(layoutInflater) }
    private var groupImageUri: Uri? = null
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                groupImageUri = uri
                Glide.with(this)
                    .asBitmap()
                    .load(uri)
                    .centerCrop()
                    .circleCrop()
                    .into(binding.imageGroupProfile)


                binding.textUpload.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewListeners()

        /*
        lifecycleScope.launch(Dispatchers.IO) {
            val res = RetrofitHelper.groupService.createGroup(
                CreateAndEditGroupBody(
                    "내방2",
                    5,
                    "1q2w3e4r"
                )
            )
            if (res.isSuccessful) {
                val message = res.body()?.message
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddGroupActivity, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("TAG", "onCreate: ${res.errorBody()?.string()}")
            }
        }*/
    }

    private fun initViewListeners() {
        binding.imageGroupProfile.setOnClickListener {
            imageLauncher.launch("image/*")
        }

        binding.checkPassword.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.inputPassword.run {
                    visibility = View.VISIBLE
                    text.clear()
                }
            } else {
                binding.inputPassword.run {
                    visibility = View.GONE
                    text.clear()
                }
            }
        }

        binding.btnOk.setOnClickListener {
            if (checkInput()) {
                lifecycleScope.launch {
                    val groupId = createGroup(
                        binding.inputGroupName.text.toString(),
                        5,
                        if (binding.checkPassword.isChecked) binding.inputPassword.text.toString() else null
                    )
                    groupImageUri?.let {
                        // 이미지 업로드 시작
                        uploadImage(groupId!!)
                    }
                }

            } else {
                Toast.makeText(this, "모든 정보를 입력하여 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkInput(): Boolean {
        if (binding.checkPassword.isChecked) {
            binding.inputGroupName.text.isNotBlank() && binding.inputPassword.text.isNotBlank()
        }
        return binding.inputGroupName.text.isNotBlank()
    }

    private suspend fun createGroup(roomName: String, capacity: Int, pwd: String? = null) =
        withContext(Dispatchers.IO) {
            val res = RetrofitHelper.groupService.createGroup(
                CreateAndEditGroupBody(roomName, capacity, pwd)
            )
            if (res.isSuccessful) {
                val body = res.body()
                toastOnMain(body?.message)
                body?.roomId
            } else {
                val errorBody = RetrofitHelper.parseErrorBody(res.errorBody())
                toastOnMain(errorBody?.message)
                null
            }
        }

    private suspend fun uploadImage(groupId: String) = withContext(Dispatchers.IO) {
        try {
            val inputStream = contentResolver.openInputStream(groupImageUri!!)
            val file = File(externalCacheDir, "groupImage.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("upload", file.name, requestFile)

            val res = RetrofitHelper.uploadService.uploadGroupProfile(groupId, body)
            if(res.isSuccessful) {
                toastOnMain(res.message())
            } else {
                toastOnMain("이미지 업로드 실패")
            }
        } catch(e: IOException) {
            toastOnMain(e.toString())
        }

    }
}