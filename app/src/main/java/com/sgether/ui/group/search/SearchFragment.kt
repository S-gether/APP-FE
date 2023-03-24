package com.sgether.ui.group.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sgether.adapter.GroupAdapter
import com.sgether.adapter.SearchLogAdapter
import com.sgether.databinding.FragmentSearchBinding
import com.sgether.model.GroupSearchLog
import com.sgether.util.Constants

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()

    private val groupAdapter by lazy { GroupAdapter(lifecycleScope, findNavController(), javaClass.simpleName, activity?.intent?.getStringExtra(
        Constants.KEY_TOKEN)?:"NULL") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewListeners()
        initViewModelListeners()
    }

    private fun initViews() {
        binding.rvGroup.adapter = groupAdapter
    }

    private fun initViewListeners() {
        // 검색 리스너
        binding.inputKeyword.addTextChangedListener {
            if (it.toString().isNotBlank()) {
                viewModel.filterKeywords(it.toString())
            } else {
                viewModel.removeFilter()
            }
        }
    }

    private fun initViewModelListeners() {
        viewModel.groupLiveData.observe(viewLifecycleOwner) {
            groupAdapter.list = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}