package com.sgether.ui.menu.mypage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sgether.databinding.FragmentMyPageBinding
import com.sgether.ui.auth.login.LoginActivity
import com.sgether.util.Constants
import com.sgether.util.JWTHelper
import com.sgether.util.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageFragment : Fragment() {
    // 프래그먼트에서 메모리 누수를 방지하기 위해 바인딩을 아래와 같이 사용
    private var _binding: FragmentMyPageBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 토큰에서 정보를 가져옴
        val payload: JWTHelper.JwtPayload? =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                activity?.intent?.getParcelableExtra(Constants.JWT_PAYLOAD)
            else
                activity?.intent?.getParcelableExtra(Constants.JWT_PAYLOAD, JWTHelper.JwtPayload::class.java)

        payload?.run {
            binding.textUserName.text = name
            binding.textUserEmail.text = id
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

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                removeData(Constants.KEY_TOKEN)
                withContext(Dispatchers.Main) {
                    startActivity(Intent(context, LoginActivity::class.java))
                    activity?.finish()
                }
            }
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