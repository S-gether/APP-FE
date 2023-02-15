package com.sgether.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.window.SplashScreen
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
import com.sgether.ui.MainActivity
import com.sgether.ui.auth.find.FindActivity
import com.sgether.utils.Constants
import com.sgether.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.schedule

// 싱글톤으로 만들기 위해 파일 최상단에 위치해야 함
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREF_AUTH)

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    // 내부저장소에 토큰을 읽고 쓰기 위해 DataStore Preference 확장 프로퍼티 선언

    // 스플래시 화면을 종료하기 위한 변수
    private var isReady = false

    // 권한을 요청하기 위한 런처
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (!result.containsValue(false)) {
                //isReady = true
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
            )
        ).run { // 스코프 함수 사용
            permissionLauncher.launch(this)
        }

        // (현재 미사용) 스플래시 화면을 길게 지속시키기 위한 함수
        Timer("CheckLogin").schedule(1000) { // 1000ms 후 실행
            isReady = true
        }

        // 화면이 그려지기 전 호출
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean { // 반복하여 호출됨
                return if (isReady) { // 준비과정이 끝난 경우 스플래시 화면 종료
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (checkToken() != null && isReady) { // 토큰을 가지고 있는지 확인하여 MainActivity 로 이동
                            withContext(Dispatchers.Main) {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        }
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
        /*lifecycleScope.launch(Dispatchers.IO) {
            try {
                val res = RetrofitHelper.authService.signIn(SignInBody(id, password))
                if(res.isSuccessful) {
                    val body = res.body() // TODO: 로직 추가
                    updateToken(body?.token)
                    Log.d("text", "startLogin: ${body?.token}")
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val body = res.errorBody() // TODO: 로직 추가
                        //startActivity(Intent(applicationContext, MainActivity::class.java))
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
            }
        }*/
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