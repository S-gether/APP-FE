package com.sgether.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sgether.adapters.GroupAdapter
import com.sgether.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()

    private val groupAdapter by lazy { GroupAdapter() }

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
        binding.inputKeyword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            // 변경되는 즉시 호출되므로 입력 도중 호출됨.
            override fun onTextChanged(keyword: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterKeywords(keyword.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
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