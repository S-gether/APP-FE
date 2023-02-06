package com.sgether.ui.login

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sgether.R
import com.sgether.databinding.ActivityRegisterBinding
import com.sgether.networks.RetrofitHelper
import com.sgether.networks.request.SignUpBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ConnectException

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
        if (checkInput(
                listOf(
                    binding.inputUserName,
                    binding.inputEmail,
                    binding.inputUserId,
                    binding.inputPassword,
                    binding.inputPassword2,
                    binding.inputIntroduction
                )
            )
        ) {
            if (checkPassword(password1, password2)) {
                binding.progressBar.root.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val result = RetrofitHelper.myService.signUp(
                            SignUpBody(
                                binding.inputUserId.text.toString(),
                                binding.inputPassword.text.toString(),
                                binding.inputUserName.text.toString(),
                                "",
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
                    } catch (e: ConnectException) {
                        toastOnMain("서버와의 연결에 실패하였습니다.")
                        hideProgressBar()
                    } catch (e: IOException) {
                        toastOnMain(e.toString())
                        hideProgressBar()
                    }

                }
            }
        } else {
            Toast.makeText(this, "빈 칸을 모두 채워 주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkInput(list: List<EditText>): Boolean {
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

    private suspend fun toastOnMain(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    private suspend fun hideProgressBar() = withContext(Dispatchers.Main) {
        binding.progressBar.root.visibility = View.GONE
    }
}