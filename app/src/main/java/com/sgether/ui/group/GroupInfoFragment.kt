package com.sgether.ui.group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sgether.R
import com.sgether.databinding.FragmentGroupInfoBinding
import com.sgether.ui.room.StudyRoomActivity

class GroupInfoFragment : Fragment() {
    private var _binding: FragmentGroupInfoBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewListeners()
    }

    private fun initViewListeners() {
        binding.btnStudyRoom.setOnClickListener {
            startActivity(Intent(requireContext(), StudyRoomActivity::class.java))
        }

        binding.btnNotice.setOnClickListener {
            findNavController().navigate(R.id.action_groupInfoFragment_to_noticeFragment)
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