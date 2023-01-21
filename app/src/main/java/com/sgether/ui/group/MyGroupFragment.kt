package com.sgether.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sgether.adapters.GroupAdapter
import com.sgether.databinding.FragmentMyGroupBinding

class MyGroupFragment : Fragment() {
    private var _binding: FragmentMyGroupBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<MyGroupViewModel>()

    private val groupAdapter by lazy { GroupAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.btnGroupInfo.setOnClickListener {
        //    findNavController().navigate(R.id.action_myGroupFragment_to_groupInfoFragment)
        //}

        initGroupRecyclerView()
        initViewModelObservers()
    }

    private fun initGroupRecyclerView() {
        binding.rvGroup.adapter = groupAdapter
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