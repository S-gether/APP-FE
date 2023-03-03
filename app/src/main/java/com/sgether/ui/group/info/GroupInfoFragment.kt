package com.sgether.ui.group.info

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.sgether.R
import com.sgether.adapter.MemberRankingAdapter
import com.sgether.databinding.FragmentGroupInfoBinding
import com.sgether.api.ApiClient
import com.sgether.ui.group.room.RoomActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupInfoFragment : Fragment() {
    private var _binding: FragmentGroupInfoBinding? = null
    private val binding
        get() = _binding!!

    private val args: GroupInfoFragmentArgs by navArgs()

    private val viewModel: GroupInfoViewModel by viewModels()

    private val memberRankingAdapter by lazy { MemberRankingAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initViewListeners()
        initViewModelListeners()
    }

    private fun initViews() {
        binding.rvMemberRanking.adapter = memberRankingAdapter
        args.groupModel.run {
            binding.textGroupName.text = room_name
            binding.textGroupDescription.text = this.created_at
            loadGroupProfile(groupId = id!!)
        }

    }

    private fun initViewListeners() {
        binding.btnStudyRoom.setOnClickListener {
            startActivity(Intent(requireContext(), RoomActivity::class.java))
        }

        binding.btnNotice.setOnClickListener {
            findNavController().navigate(R.id.action_groupInfoFragment_to_noticeFragment)
        }

        binding.btnStudyRoom.setOnClickListener {
            // 그룹에 참여
            viewModel.joinGroup()
        }
    }

    private fun initViewModelListeners() {
        viewModel.memberRankingListLiveData.observe(viewLifecycleOwner) {
            memberRankingAdapter.list = it
        }
    }


    private fun loadGroupProfile(groupId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val res = ApiClient.uploadService.readGroupProfile(groupId)
            if(res.isSuccessful) {
                withContext(Dispatchers.Main) {
                    Glide.with(binding.root)
                        .load(res.body()?.bytes())
                        .circleCrop()
                        .into(binding.imageGroupProfile)
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}