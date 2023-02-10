package com.sgether.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sgether.R
import com.sgether.databinding.ActivityRegisterBinding
import com.sgether.networks.RetrofitHelper
import com.sgether.networks.request.auth.SignUpBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnOk.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val password1 = binding.inputPassword.text.toString()
        val password2 = binding.inputPassword2.text.toString()
        if (checkInputIsNotBlank()){
            if (checkPassword(password1, password2)) {
                binding.progressBar.root.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val result = RetrofitHelper.authService.signUp(
                            SignUpBody(
                                binding.inputUserId.text.toString(),
                                binding.inputPassword.text.toString(),
                                binding.inputUserName.text.toString(),
                                getResidentNum(),
                                getAuthority(),
                                binding.inputEmail.text.toString(),
                                binding.inputIntroduction.text.toString()
                            )
                        )
                        if (result.isSuccessful) {
                            toastOnMain("로그인 성공")
                            hideProgressBar()
                            finish()
                        } else {
                            toastOnMain("${result.errorBody()}")
                            hideProgressBar()
                        }
                    }  catch (e: IOException) {
                        toastOnMain("서버와의 연결에 실패하였습니다. ${e.toString()}")
                        hideProgressBar()
                        startLogin("테스트 아이디", "테스트 비밀번호") // TODO: 성공 시 하는 것으로 바꿔야 함
                    }

                }
            }
        } else {
            Toast.makeText(this, "빈 칸을 모두 채워 주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLogin(id: String, password: String) {
        setResult(RESULT_OK, intent.apply {
            putExtra("id", id)
            putExtra("password", password)
        })
        finish()
    }

    private fun checkInputIsNotBlank(): Boolean {
        // TODO : ResidentNum 크기 체크해야 함
        val list = listOf(
            binding.inputUserName,
            binding.inputResidentNumStart,
            binding.inputResidentNumEnd,
            binding.inputEmail,
            binding.inputUserId,
            binding.inputPassword,
            binding.inputPassword2,
            binding.inputIntroduction,
        )

        list.forEach {
            if (it.text.toString().isBlank()) {
                return false
            }
        }
        return true
    }

    private fun checkPassword(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    private fun getAuthority(): String {
        return when (binding.radioGroupAuthority.checkedRadioButtonId) {
            R.id.btnAdmin -> "admin"
            else -> "student"
        }
    }

    private fun getResidentNum(): String {
        val start = binding.inputResidentNumStart.text.toString()
        val end = binding.inputResidentNumEnd.text.toString()
        return "$start-${end}000000"
    }

    private suspend fun toastOnMain(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    private suspend fun hideProgressBar() = withContext(Dispatchers.Main) {
        binding.progressBar.root.visibility = View.GONE
    }


}