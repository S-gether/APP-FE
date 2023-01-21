package com.sgether.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sgether.R
import com.sgether.databinding.ActivityLoginBinding
import com.sgether.ui.MainActivity
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // super.onCreate() 이전에 위치해야 함
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Splash Screen
        var isReady = false
        Timer("CheckLogin").schedule(1000){ // 1000ms 후 실행
            isReady = true
        }
        
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
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
            if(isLogin){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}