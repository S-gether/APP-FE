package com.sgether.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sgether.R
import com.sgether.databinding.ActivityMainBinding
import com.sgether.api.ApiClient
import com.sgether.util.Constants

// Navigation Component 를 통해 Fragment 를 화면에 표시하기 위한 Activity
// XML 의 FragmentContainerView 를 보면 @navigation/nav_main 와 연결됨을 확인 가능
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ApiClient.enableToken(intent.getStringExtra(Constants.KEY_TOKEN))
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        // BottomNavigationView 와 NavController 를 연결하여 메뉴 아이템 클릭시 해당 프래그먼트로 이동할 수 있도록 함
        binding.bottomNavigationView.setupWithNavController(navController)


        val learningTime = 60 // 학습 시간 (단위: 분)
        val aiCount = 3 // 지적 횟수



    }
}