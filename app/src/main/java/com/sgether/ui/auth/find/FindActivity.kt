package com.sgether.ui.auth.find

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sgether.R
import com.sgether.databinding.ActivityFindBinding

class FindActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFindBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.viewPager.adapter = FindAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "아이디 찾기"
                1 -> tab.text = "비밀번호 찾기"
            }
        }.attach()


    }
}