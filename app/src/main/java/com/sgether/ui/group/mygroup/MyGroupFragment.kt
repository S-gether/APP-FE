package com.sgether.ui.group.mygroup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sgether.adapter.GroupAdapter
import com.sgether.databinding.FragmentMyGroupBinding
import com.sgether.ui.group.add.AddGroupActivity
import com.sgether.util.Constants

class MyGroupFragment : Fragment() {
    private var _binding: FragmentMyGroupBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<MyGroupViewModel>()

    private val groupAdapter by lazy { GroupAdapter(lifecycleScope, findNavController(), javaClass.simpleName, activity?.intent?.getStringExtra(Constants.KEY_TOKEN)?:"NULL") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupRecyclerView()
        initViewListeners()
        initViewModelObservers()

        viewModel.loadMyGroupList()
    }

    private fun initGroupRecyclerView() {
        binding.rvGroup.adapter = groupAdapter
    }

    private fun initViewListeners() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(context, AddGroupActivity::class.java))
        }
    }

    private fun initViewModelObservers() {
        viewModel.groupLiveData.observe(viewLifecycleOwner){
            groupAdapter.list = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyGroupBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}