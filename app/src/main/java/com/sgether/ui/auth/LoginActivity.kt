package com.sgether.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.sgether.databinding.ActivityLoginBinding
import com.sgether.networks.RetrofitHelper
import com.sgether.networks.request.auth.SignInBody
import com.sgether.ui.MainActivity
import com.sgether.ui.auth.find.FindActivity
import com.sgether.utils.Constants
import com.sgether.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREF_AUTH)

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (!result.containsValue(false)) {
                Toast.makeText(this, "권한 허용", Toast.LENGTH_SHORT).show()
            }
        }

    private val registerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            val id = it.data?.getStringExtra("id")
            val password = it.data?.getStringExtra("password")
            if(id != null && password != null) {
                startLogin(id, password)
            }
        }
    }

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // super.onCreate() 이전에 위치해야 함
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        PermissionHelper.getDeniedPermissions(
            this, listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
            )
        ).run {
            permissionLauncher.launch(this)
        }

        // Splash Screen
        var isReady = false
        Timer("CheckLogin").schedule(1000) { // 1000ms 후 실행
            isReady = true
        }

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean { // 지속적으로 호출하여 확인
                return if (isReady) { // 준비과정이 끝난 경우 스플래시 화면 종료
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        })

        // 스플래시 화면 종료 후 로그인 상태일 경우 메인으로 이동
        content.viewTreeObserver.addOnDrawListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (checkToken() != null) {
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }

        initViewListeners()

        lifecycleScope.launch(Dispatchers.IO) {
            if(checkToken() != null ) {
                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }
        }
    }

    private fun initViewListeners() {
        binding.textSignUp.setOnClickListener {
            registerLauncher.launch(Intent(this, RegisterActivity::class.java))
        }

        binding.textForgetPassword.setOnClickListener {
            startActivity(Intent(this, FindActivity::class.java))
        }

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            val id = binding.inputId.text.toString()
            val password = binding.inputPassword.text.toString()
            if(checkInput()) {
                startLogin(id, password)
            }
        }

        binding.btnGoogle.setOnClickListener {

        }
    }

    private suspend fun checkToken(): String? { // TODO: 토큰이 유효한지도 확인해야 함
        return readStringData(Constants.KEY_TOKEN)
    }

    private fun checkInput(): Boolean {
        val id = binding.inputId.text.toString()
        val password = binding.inputPassword.text.toString()
        return id.isNotBlank() && password.isNotBlank()
    }

    private fun startLogin(id: String, password: String) {
        Toast.makeText(this, "$id, $password", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val res = RetrofitHelper.authService.signIn(SignInBody(id, password))
                if(res.isSuccessful) {
                    val body = res.body() // TODO: 로직 추가
                } else {
                    withContext(Dispatchers.Main) {
                        val body = res.errorBody() // TODO: 로직 추가
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
            }
        }
    }

    private fun updateToken(token: String) = lifecycleScope.launch(Dispatchers.IO) {
        writeStringData(Constants.KEY_TOKEN, token)
    }

    private suspend fun readStringData(key: String): String?{
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    private suspend fun writeStringData(key: String, value: String){
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }
}