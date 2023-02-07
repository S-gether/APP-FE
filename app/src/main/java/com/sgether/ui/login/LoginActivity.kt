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

    private val registerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Toast.makeText(this, "${it.resultCode} 호출", Toast.LENGTH_SHORT).show()
        if(it.resultCode == RESULT_OK) {
            val id = it.data?.getStringExtra("id")
            val password = it.data?.getStringExtra("password")
            Toast.makeText(this, "$id, $password", Toast.LENGTH_SHORT).show()
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
        var isLogin = false
        content.viewTreeObserver.addOnDrawListener {
            if (isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            registerLauncher.launch(Intent(this, RegisterActivity::class.java))
        }

        binding.btnGoogle.setOnClickListener {

        }

        binding.sendGroup.setOnClickListener {

        }
        initViewListeners()
    }

    private fun initViewListeners() {
        binding.textSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun startLogin(id: String, password: String) {
        Toast.makeText(this, "$id, $password", Toast.LENGTH_SHORT).show()
    }
}