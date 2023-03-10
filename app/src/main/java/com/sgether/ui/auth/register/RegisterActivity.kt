package com.sgether.ui.auth.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sgether.R
import com.sgether.databinding.ActivityRegisterBinding
import com.sgether.api.ApiClient
import com.sgether.api.request.auth.SignUpBody
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
                        ApiClient.disableToken()
                        val id = binding.inputUserId.text.toString()
                        val password = binding.inputPassword.text.toString()
                        val result = ApiClient.authService.signUp(
                            SignUpBody(
                                id,
                                password,
                                binding.inputUserName.text.toString(),
                                getResidentNum(),
                                getAuthority(),
                                binding.inputEmail.text.toString(),
                                binding.inputIntroduction.text.toString()
                            )
                        )
                        if (result.isSuccessful) {
                            toastOnMain("????????? ??????")
                            hideProgressBar()
                            startLogin(id, password)
                        } else {
                            toastOnMain("${result.errorBody()?.string()}")
                            hideProgressBar()
                        }
                    }  catch (e: IOException) {
                        toastOnMain("???????????? ????????? ?????????????????????. $e")
                        hideProgressBar()
                    }

                }
            } else {
                Toast.makeText(this, "??????????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "??? ?????? ?????? ?????? ?????????", Toast.LENGTH_SHORT).show()
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
        // TODO : ResidentNum ?????? ???????????? ???
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