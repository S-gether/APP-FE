package com.sgether.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sgether.databinding.FragmentMyPageBinding
import com.sgether.networks.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            val res = RetrofitHelper.userService.readUser()
            if(res.isSuccessful) {
                val first = res.body()?.userSelectReseult?.get(0)
                withContext(Dispatchers.Main) {
                    binding.textUserName.text = first?.user_id
                    binding.textUserEmail.text = first?.email
                }
            } else {

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
}