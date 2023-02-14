package com.sgether.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.sgether.R
import com.sgether.databinding.ItemGroupBinding
import com.sgether.networks.response.group.Room
import com.sgether.ui.group.MyGroupFragmentDirections

class GroupAdapter(var navController: NavController) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    var list: List<Room> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class GroupViewHolder(val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Room) {
            binding.textGroupName.text = group.room_name
            binding.textGroupInfo.text = group.created_at

            binding.root.setOnClickListener {
                val action = MyGroupFragmentDirections.actionMyGroupFragmentToGroupInfoFragment(group.room_name?:"NULL")
                navController.navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}