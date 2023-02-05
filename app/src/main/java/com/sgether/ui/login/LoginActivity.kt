package com.sgether.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.sgether.databinding.ActivityLoginBinding
import com.sgether.networks.RetrofitHelper
import com.sgether.ui.MainActivity
import com.sgether.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (!result.containsValue(false)) {
                Toast.makeText(this, "권한 허용", Toast.LENGTH_SHORT).show()
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
        var isLogin = false
        content.viewTreeObserver.addOnDrawListener {
            if (isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            //startActivity(Intent(this, MainActivity::class.java))
            //finish()
            /*lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result = RetrofitHelper.myService.signUp(JsonObject().apply {
                        addProperty("id", "hong12")
                        addProperty("pwd", "1q2w3e4r!!!")
                        addProperty("name", "hong1243")
                        addProperty("residentNum", "hong12143")
                        addProperty("authority", "student")
                        addProperty("email", "hongmucahe@gmail.com")
                        addProperty("introduce", "소개 메시지")
                    })
                    Log.d(".LoginActivity", "body: ${result.body()}")
                    Log.d(".LoginActivity", "errorbody: ${result.errorBody()?.string()}")
                } catch(e: Exception) {
                    Log.d(".LoginActivity", "catch: $e")
                }

            }*/
        }

        binding.btnGoogle.setOnClickListener {
            /*lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result = RetrofitHelper.myService.signIn(JsonObject().apply {
                        addProperty("id", "hong12")
                        addProperty("pwd", "1q2w3e4r!!!")
                    })
                    Log.d(".LoginActivity", "onCreate: ${result.body()}")
                    Log.d(".LoginActivity", "onCreate: ${result.errorBody()?.string()}")
                } catch(e: Exception) {
                    Log.d(".LoginActivity", "onCreate: $e")
                }

            }*/
        }

        binding.sendGroup.setOnClickListener {
            /*lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result = RetrofitHelper.myService.getGroupList("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImhvbmcxMiIsImlhdCI6MTY3NTQ5MTQ5MSwiaXNzIjoiYXBpLXNlcnZlciJ9.5IWGD0C1noJMSmpHT4lbujsmG4kFyZILryrTZuWXAS4")
                    Log.d(".LoginActivity", "onCreate: ${result.body()}")
                    Log.d(".LoginActivity", "onCreate: ${result.errorBody()?.string()}")
                } catch(e: Exception) {
                    Log.d(".LoginActivity", "onCreate: $e")
                }

            }*/
        }
        initViewListeners()
    }

    private fun initViewListeners() {
        binding.textSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}