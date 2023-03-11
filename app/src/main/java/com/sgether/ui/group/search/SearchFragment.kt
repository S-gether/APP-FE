package com.sgether.ui.group.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sgether.adapter.GroupAdapter
import com.sgether.adapter.SearchLogAdapter
import com.sgether.databinding.FragmentSearchBinding
import com.sgether.model.GroupSearchLog
import com.sgether.util.Constants

class SearchFragment : Fragment(), SearchLogAdapter.OnClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()

    private val groupAdapter by lazy { GroupAdapter(lifecycleScope, findNavController(), javaClass.simpleName, activity?.intent?.getStringExtra(
        Constants.KEY_TOKEN)?:"NULL") }
    private val searchLogAdapter by lazy { SearchLogAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewListeners()
        initViewModelListeners()
    }

    private fun initViews() {
        binding.rvGroup.adapter = groupAdapter
        binding.rvGroupSearchLog.adapter = searchLogAdapter

        showSearchLogList()
    }

    private fun initViewListeners() {
        // 검색 리스너
        binding.inputKeyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            // 변경되는 즉시 호출되므로 입력 도중 호출됨.
            override fun onTextChanged(
                keyword: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (keyword.isNullOrBlank()) {
                    showSearchLogList()
                } else {
                    viewModel.filterKeywords(keyword.toString())
                    showGroupList()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.inputKeyword.setOnEditorActionListener { textView, actionId, keyEvent ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    viewModel.insertGroupSearchLog(GroupSearchLog(keyword = binding.inputKeyword.text.toString()))
                    true
                }
                else -> false
            }
        }
    }

    private fun initViewModelListeners() {
        viewModel.groupLiveData.observe(viewLifecycleOwner) {
            groupAdapter.list = it
        }

        viewModel.groupSearchLogListLiveData.observe(viewLifecycleOwner) { it ->
            searchLogAdapter.list = it
        }
    }

    private fun showGroupList() {
        binding.rvGroup.visibility = View.VISIBLE
        binding.rvGroupSearchLog.visibility = View.GONE
        binding.textType.text = "검색 결과"
    }

    private fun showSearchLogList() {
        binding.rvGroup.visibility = View.GONE
        binding.rvGroupSearchLog.visibility = View.VISIBLE
        binding.textType.text = "최근 검색"
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

    // SearchLogAdapter
    override fun onClose(groupSearchLog: GroupSearchLog) {
        viewModel.deleteGroupSearchLog(groupSearchLog)
    }
}