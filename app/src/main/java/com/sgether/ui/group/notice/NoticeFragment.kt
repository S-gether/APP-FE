package com.sgether.ui.group.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sgether.adapter.CommentAdapter
import com.sgether.databinding.FragmentNoticeBinding
import com.sgether.model.Comment

class NoticeFragment : Fragment() {
    private var _binding: FragmentNoticeBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: NoticeViewModel by viewModels()

    private val commentAdapter by lazy { CommentAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initViewListeners()
        initViewModelListeners()
    }

    private fun initViews() {
        binding.rvComment.adapter = commentAdapter
    }

    private fun initViewListeners() {
        binding.btnSend.setOnClickListener {
            viewModel.addComment(Comment("", "sdfds", binding.inputComment.text.toString()))
        }
    }

    private fun initViewModelListeners() {
        viewModel.commentListLiveData.observe(viewLifecycleOwner) {
            commentAdapter.list = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}