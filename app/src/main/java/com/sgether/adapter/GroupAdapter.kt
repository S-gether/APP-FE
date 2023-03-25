package com.sgether.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.sgether.R
import com.sgether.api.ApiClient
import com.sgether.databinding.ItemGroupBinding
import com.sgether.model.GroupModel
import com.sgether.ui.group.mygroup.MyGroupFragment
import com.sgether.ui.group.mygroup.MyGroupFragmentDirections
import com.sgether.ui.group.search.SearchFragment
import com.sgether.ui.group.search.SearchFragmentDirections
import com.sgether.util.Constants
import com.sgether.util.loadGroupProfile
import kotlinx.coroutines.CoroutineScope
import java.io.IOException

class GroupAdapter(
    private val scope: CoroutineScope,
    private val navController: NavController,
    var simpleName: String,
    var token: String
) :
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {


    var list: List<GroupModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: GroupModel) {
            binding.textGroupName.text = group.room_name
            binding.textGroupInfo.text = group.explain
            loadGroupProfile(group.id!!, token, binding.imageGroupProfile)

            binding.root.setOnClickListener {
                var action: NavDirections? = null
                when (simpleName) {
                    MyGroupFragment::class.java.simpleName -> {
                        action = MyGroupFragmentDirections.actionMyGroupFragmentToGroupInfoFragment(
                            group
                        )
                    }
                    SearchFragment::class.java.simpleName -> {
                        action = SearchFragmentDirections.actionSearchFragmentToGroupInfoFragment(
                            group
                        )
                    }
                }
                navController.navigate(action!!)
            }
        }

        private suspend fun loadGroupProfile(groupId: String) {
            try {
                val res = ApiClient.uploadService.readGroupProfile(groupId)
                if(res.isSuccessful) {
                    //res.body()?.
                }
            } catch (e: IOException) {

            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}
