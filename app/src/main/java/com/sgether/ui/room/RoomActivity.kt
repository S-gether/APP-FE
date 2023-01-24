package com.sgether.ui.room

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sgether.adapters.MemberVideoAdapter
import com.sgether.databinding.ActivityRoomBinding

class RoomActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRoomBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<RoomViewModel>()

    private val memberVideoAdapter by lazy { MemberVideoAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initViewModelListeners()
    }

    private fun initViews() {
        binding.rvMemberVideo.adapter = memberVideoAdapter
    }

    private fun initViewModelListeners() {
        viewModel.memberDataListLiveData.observe(this) {
            memberVideoAdapter.list = it
        }
    }
}