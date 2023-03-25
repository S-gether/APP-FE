package com.sgether.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sgether.databinding.FragmentMyPageBinding
import com.sgether.ui.auth.login.LoginActivity
import com.sgether.util.*
import com.sgether.util.PreferenceManager.readStringData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageFragment : Fragment() {
    // 프래그먼트에서 메모리 누수를 방지하기 위해 바인딩을 아래와 같이 사용
    private var _binding: FragmentMyPageBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: MyPageViewModel by viewModels()

    private var userImageUri: Uri? = null
    private val userImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null) {
            userImageUri = it

            Glide.with(this)
                .asBitmap()
                .load(it)
                .centerCrop()
                .circleCrop()
                .into(binding.imageUserProfile)

            viewModel.uploadImage(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            binding.textUserName.text = PreferenceManager.readStringData(requireContext(), Constants.KEY_USER_NAME)
            binding.textUserId.text = PreferenceManager.readStringData(requireContext(), Constants.KEY_USER_ID)
        }
        /* TODO: API 로 가져오는 코드 (설명 추가 요청해야 함)
        lifecycleScope.launch(Dispatchers.IO) {
            val res = RetrofitHelper.userService.readUser() // 유저 정보에 대한 요청을 전송
            if (res.isSuccessful) {
                val first = res.body()?.userSelectReseult?.get(0) // 첫 번째 유저에 대한 정보를 받아옴
                withContext(Dispatchers.Main) {
                    // View 와 연결
                    binding.textUserName.text = first?.user_id
                    binding.textUserEmail.text = first?.email
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, res.errorBody()?.string(), Toast.LENGTH_SHORT).show()
                }
            }
        }
        */
        binding.imageUserProfile.setOnClickListener {
            userImageLauncher.launch("image/*")
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                removeData(Constants.KEY_TOKEN)
                withContext(Dispatchers.Main) {
                    startActivity(Intent(context, LoginActivity::class.java))
                    activity?.finish()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            loadUserProfile(withContext(Dispatchers.IO) {PreferenceManager.readStringData(requireContext(), Constants.KEY_USER_ID)?:""}, withContext(Dispatchers.IO) {PreferenceManager.readStringData(requireContext(), Constants.KEY_TOKEN)?:"" }, binding.imageUserProfile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun removeData(key: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context?.dataStore?.edit { settings ->
            settings.remove(dataStoreKey)
        }
    }
}