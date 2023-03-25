package com.sgether.ui.group.info

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
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
import com.sgether.ui.MainActivity
import com.sgether.ui.group.room.RoomActivity
import com.sgether.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class GroupInfoFragment : Fragment() {
    private var _binding: FragmentGroupInfoBinding? = null
    private val binding
    get() = _binding!!

    var token: String? = null

    private val viewModel: GroupInfoViewModel by viewModels()
    private val args: GroupInfoFragmentArgs by navArgs()
    private lateinit var memberRankingAdapter: MemberRankingAdapter


    private var userId: String? = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            token = PreferenceManager.readStringData(requireContext(), Constants.KEY_TOKEN)
            userId = PreferenceManager.readStringData(requireContext(), Constants.KEY_USER_ID)
            Log.d(null, "onViewCreated: $userId $token")
            memberRankingAdapter = MemberRankingAdapter(token!!, userId!!)
            withContext(Dispatchers.Main) {
                initViews()
                initViewListeners()
                initViewModelListeners()
                viewModel.loadGroupMemberStudyTime(args.groupModel.id!!)
            }
        }
    }

    private fun initViews() {
        binding.rvMemberRanking.adapter = memberRankingAdapter

        args.groupModel.run {
            binding.textGroupName.text = room_name
            binding.textGroupDescription.text = explain
            binding.btnDelete.visibility = if(master_id == userId) View.VISIBLE else View.GONE
            lifecycleScope.launch {
                loadGroupProfile(
                    groupId = id!!,
                    token = withContext(Dispatchers.IO) { PreferenceManager.readStringData(requireContext(), Constants.KEY_TOKEN)?:"" },
                    view = binding.imageGroupProfile
                )
            }
        }
        viewModel.groupModel = args.groupModel
    }

    private fun initViewListeners() {
        binding.btnStartConference.setOnClickListener {
            startActivity(Intent(requireContext(), RoomActivity::class.java).apply {
                val payload: JWTHelper.JwtPayload? =
                    activity?.intent?.getParcelableExtra(Constants.JWT_PAYLOAD)
                putExtra(Constants.KEY_GROUP_MODEL, args.groupModel)
                putExtra(Constants.JWT_PAYLOAD, payload)
            })
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNotice.setOnClickListener {
            // 공지사항 이동
            findNavController().navigate(R.id.action_groupInfoFragment_to_noticeFragment)
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteGroup(args.groupModel.id?:"")
        }

        binding.btnJoin.setOnClickListener {
            if (binding.btnJoin.text == "스터디 룸 가입하기") {
                // 그룹에 가입하기
                viewModel.joinGroup(args.groupModel.id!!)
            } else {
                // 그룹에 탈퇴하기
                viewModel.dropGroup(args.groupModel.id!!)
            }
        }
    }

    // ViewModel의 LiveData의 값 변화를 관찰
    private fun initViewModelListeners() {
        viewModel.memberRankingListLiveData.observe(viewLifecycleOwner) { memberRankingList ->
            if(memberRankingList != null) {
                val IsUserJoinedGroup = memberRankingList.map { it.user_id }.contains(userId!!)

                binding.btnStartConference.isEnabled = IsUserJoinedGroup
                binding.btnNotice.isEnabled = IsUserJoinedGroup

                if(IsUserJoinedGroup){
                    binding.btnJoin.text = "스터디 룸 탈퇴하기"
                } else {
                    binding.btnJoin.text = "스터디 룸 가입하기"

                }
                memberRankingAdapter.list = memberRankingList
            }

        }

        viewModel.deleteGroupResult.observe(viewLifecycleOwner) {
            if(it.isSuccessful) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.joinGroupResult.observe(viewLifecycleOwner) {
            if(it.isSuccessful) {

            } else {

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