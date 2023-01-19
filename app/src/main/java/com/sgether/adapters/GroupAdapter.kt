package com.sgether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemGroupBinding
import com.sgether.models.Group

class GroupAdapter : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    var list: List<Group> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class GroupViewHolder(val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.textGroupName.text = group.name
            binding.textGroupInfo.text = group.description
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