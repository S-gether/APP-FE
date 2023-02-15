package com.sgether.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sgether.databinding.FragmentMyPageBinding
import com.sgether.networks.RetrofitHelper
import com.sgether.ui.auth.LoginActivity
import com.sgether.ui.auth.dataStore
import com.sgether.utils.Constants
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