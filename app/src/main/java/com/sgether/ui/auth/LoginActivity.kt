package com.sgether.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.sgether.databinding.ActivityLoginBinding
import com.sgether.networks.RetrofitHelper
import com.sgether.networks.request.auth.SignInBody
import com.sgether.ui.MainActivity
import com.sgether.ui.auth.find.FindActivity
import com.sgether.utils.Constants
import com.sgether.utils.JWTHelper
import com.sgether.utils.PermissionHelper
import com.sgether.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule

// 싱글톤으로 만들기 위해 파일 최상단에 위치해야 함

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    // 내부저장소에 토큰을 읽고 쓰기 위해 DataStore Preference 확장 프로퍼티 선언

    // 스플래시 화면을 종료하기 위한 변수
    private var isReady = false
    private var permissionReady = false
    private var loadTokenReady = false
    private var token: String? = null

    // 권한을 요청하기 위한 런처
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (!result.containsValue(false)) {
                permissionReady = true
            } else {
                Toast.makeText(this, "사용자 권한을 허용하지 않으면 정상적인 이용이 불가합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 회원가입 액티비티에서 돌아와 자동으로 로그인 하기 위한 런처
    private val registerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val id = it.data?.getStringExtra("id")
                val password = it.data?.getStringExtra("password")
                if (id != null && password != null) {
                    startLogin(id, password)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 스플래시 스크린 시작: 반드시 부모 생성자 앞에 위치해야 함
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 승인되지 않은 권한을 불러와 사용자에게 요청한다.
        PermissionHelper.getDeniedPermissions(
            this, listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        ).run { // 스코프 함수 사용
            permissionLauncher.launch(this)
        }

        // 스플래시 화면을 보여주는 최소 시간
        Timer("CheckLogin").schedule(500) { // 1000ms 후 실행
            isReady = true
        }

        lifecycleScope.launch(Dispatchers.IO) {
            token = checkToken()
            loadTokenReady = true
        }

        // 화면이 그려지기 전 호출
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean { // 반복하여 호출됨
                return if (permissionReady && loadTokenReady && isReady) { // 준비과정이 끝난 경우 스플래시 화면 종료
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    if (token != null) { // 토큰을 가지고 있는지 확인하여 MainActivity 로 이동
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra(Constants.KEY_TOKEN, token)
                            putExtra(Constants.JWT_PAYLOAD, JWTHelper.parseJwtToken(token!!).payload)
                        })
                        finish()
                    }
                    true
                } else {
                    false
                }
            }
        })
        initViewListeners()
    }

    // 뷰와 관련된 이벤트 리스너 초기화
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
            if (checkInput()) {
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
        Toast.makeText(this@LoginActivity, "${id} ${password}", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                RetrofitHelper.disableToken()
                val res = RetrofitHelper.authService.signIn(SignInBody(id, password))
                if(res.isSuccessful) {
                    val body = res.body() // TODO: 로직 추가
                    updateToken(body?.token)
                    Log.d("text", "startLogin: ${body?.token}")
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(applicationContext, MainActivity::class.java).apply {
                            putExtra(Constants.KEY_TOKEN, body?.token)
                        })
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val body = res.errorBody() // TODO: 로직 추가
                        Toast.makeText(this@LoginActivity, body?.string(), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun updateToken(token: String?) {
        writeStringData(Constants.KEY_TOKEN, token ?: "")
    }

    private suspend fun readStringData(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    private suspend fun writeStringData(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

}