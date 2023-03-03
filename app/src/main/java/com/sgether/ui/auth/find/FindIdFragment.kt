package com.sgether.ui.auth.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sgether.databinding.FragmentFindIdBinding
import com.sgether.api.ApiClient
import com.sgether.api.request.auth.IdFoundBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class FindIdFragment : Fragment() {

    private var _binding: FragmentFindIdBinding? = null
    private val binding get() = _binding!!

    private val resultDialog by lazy { buildResultDialog() }
    private fun buildResultDialog(): AlertDialog.Builder {
        return AlertDialog.Builder(requireContext())
            .setNegativeButton("닫기") { _, _ -> }
            .setTitle("결과")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            if (checkInput()) {
                findId(binding.inputUserName.text.toString(), getResidentNum())
            }
        }
    }

    private fun checkInput(): Boolean {
        return true // TODO: 입력 공백 확인 및 생년월일 확인
    }

    private fun getResidentNum(): String {
        val start = binding.inputResidentNumStart.text.toString()
        val end = binding.inputResidentNumEnd.text.toString()
        return "${start}-${end}000000"
    }

    private fun findId(userName: String, residentNum: String) =
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = ApiClient.authService.findId(IdFoundBody(userName, residentNum))
                when (result.code()) {
                    200 -> {
                        val body = result.body()
                        showResultDialog("결과", body?.id ?: "NULL")
                    }
                    401 -> {
                        showResultDialog("결과", "존재하지 않는 회원입니다.")
                    }
                    406 -> {
                        showResultDialog("결과", "존재하지 않는 회원입니다.")
                    }

                }

            } catch (e: IOException) {
                showResultDialog("오류", "${e}")
            }
        }

    private suspend fun showResultDialog(title: String, message: String) =
        withContext(Dispatchers.Main) {
            resultDialog.setTitle(title)
            resultDialog.setMessage(message)
            resultDialog.show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}