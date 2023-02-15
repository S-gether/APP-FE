package com.sgether.adapters

import android.content.ClipData
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sgether.R
import com.sgether.databinding.FragmentMyGroupBinding
import com.sgether.databinding.ItemGroupBinding
import com.sgether.networks.response.group.Room
import com.sgether.ui.group.MyGroupFragmentDirections

class GroupAdapter(private val navController: NavController) :
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    var list: List<Room> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: Room) {
            binding.textGroupName.text = group.room_name
            binding.textGroupInfo.text = group.created_at

            binding.root.setOnClickListener {
                val action =
                    MyGroupFragmentDirections.actionMyGroupFragmentToGroupInfoFragment(
                        group.room_name
                            ?: "NULL"
                    )
                navController.navigate(action)
            }
        }
    }

    inner class SwipeToDeleteCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(0, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val item = list[position]

            // delete the item from the adapter
            list = list.toMutableList().apply { removeAt(position) }

            // notify the adapter about the removed item
            notifyItemRemoved(position)
        }
    }

    val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback())

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemTouchHelper.attachToRecyclerView(recyclerView)
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

