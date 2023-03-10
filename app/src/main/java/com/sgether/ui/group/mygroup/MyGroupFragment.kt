package com.sgether.ui.group.mygroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sgether.adapter.GroupAdapter
import com.sgether.databinding.FragmentMyGroupBinding
import com.sgether.api.ApiClient
import com.sgether.ui.group.add.AddGroupActivity
import com.sgether.util.Constants
import com.sgether.util.toastOnMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MyGroupFragment : Fragment() {
    private var _binding: FragmentMyGroupBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<MyGroupViewModel>()

    private val groupAdapter by lazy { GroupAdapter(lifecycleScope, findNavController(), javaClass.simpleName, activity?.intent?.getStringExtra(Constants.KEY_TOKEN)?:"NULL") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.btnGroupInfo.setOnClickListener {
        //    findNavController().navigate(R.id.action_myGroupFragment_to_groupInfoFragment)
        //}

        initGroupRecyclerView()
        initViewModelObservers()

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(context, AddGroupActivity::class.java))
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val res = ApiClient.groupService.readGroup()
                val body = res.body()
                if(res.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, body?.message, Toast.LENGTH_SHORT).show()
                    }
                    viewModel.setGroupList(body?.groupsSelectReseult!!)
                    Log.d("TAG", "onViewCreated: ${body?.groupsSelectReseult!!.joinToString(" ")}")
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, res.errorBody()?.string(), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch(e: IOException) {
                context?.toastOnMain(e.message)
            }
        }
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