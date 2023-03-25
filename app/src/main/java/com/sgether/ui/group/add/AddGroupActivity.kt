package com.sgether.ui.group.add

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sgether.databinding.ActivityAddGroupBinding
import com.sgether.api.ApiClient
import com.sgether.api.request.group.CreateAndEditGroupBody
import com.sgether.util.toastOnMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

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
                lifecycleScope.launch(Dispatchers.IO) {
                    val groupId = createGroup(
                        binding.inputGroupName.text.toString(),
                        5,
                        binding.inputGroupDescription.text.toString(),
                        if (binding.checkPassword.isChecked) binding.inputPassword.text.toString() else null
                    )?.let {groupId ->
                        groupImageUri?.let {
                            // 이미지 업로드 시작
                            val uploadImageTask = async {  uploadImage(groupId) }
                            val joinGroupTask = async {  joinGroup(groupId) }

                            uploadImageTask.await()
                            joinGroupTask.await()
                            finish()
                        }
                    }
                }

            } else {
                Toast.makeText(this, "모든 정보를 입력하여 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkInput(): Boolean {
        if (binding.checkPassword.isChecked) {
            binding.inputGroupName.text.isNotBlank() && binding.inputPassword.text.isNotBlank() && binding.inputGroupDescription.text.isNotBlank()
        }
        return binding.inputGroupName.text.isNotBlank() && binding.inputGroupDescription.text.isNotBlank()
    }

    private suspend fun createGroup(roomName: String, capacity: Int, description: String, pwd: String? = null) =
        withContext(Dispatchers.IO) {
            try {
                val res = ApiClient.groupService.createGroup(
                    CreateAndEditGroupBody(roomName, capacity, description, pwd)
                )
                if (res.isSuccessful) {
                    val body = res.body()
                    toastOnMain(body?.message)
                    body?.roomId
                } else {
                    val errorBody = ApiClient.parseErrorBody(res.errorBody())
                    toastOnMain(errorBody?.message)
                    null
                }
            } catch(e: IOException) {
                toastOnMain(e.message)
                null
            }
        }

    private suspend fun uploadImage(groupId: String): Boolean {
        try {
            val inputStream = contentResolver.openInputStream(groupImageUri!!)
            val file = File(externalCacheDir, "${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("upload", file.name, requestFile)

            val res = ApiClient.uploadService.uploadGroupProfile(groupId, body)
            if(res.isSuccessful) {
                return true
            } else {
                toastOnMain("이미지 업로드 실패")
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    suspend fun joinGroup(groupId: String): Boolean {
        try {
            val res = ApiClient.joinGroupService.joinGroup(groupId)
            if(res.isSuccessful) {
                return true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}