package com.sgether.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemSearchLogBinding
import com.sgether.models.GroupSearchLog
import com.sgether.utils.DateHelper

class SearchLogAdapter(var listener: OnClickListener) : RecyclerView.Adapter<SearchLogAdapter.SearchLogViewHolder>() {

    var list = listOf<GroupSearchLog>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class SearchLogViewHolder(val binding: ItemSearchLogBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(groupSearchLog: GroupSearchLog){
            binding.textKeyword.text = groupSearchLog.keyword
            Log.d(".ViewTest", "bind: ${groupSearchLog.keyword}")
            binding.textCreated.text = DateHelper.dateToString(groupSearchLog.date)
            initViewListeners(groupSearchLog)
        }

        private fun initViewListeners(groupSearchLog: GroupSearchLog) {
            binding.btnClose.setOnClickListener {
                listener.onClose(groupSearchLog)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLogViewHolder {
        val binding = ItemSearchLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchLogViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    interface OnClickListener {
        fun onClose(groupSearchLog: GroupSearchLog)
    }
}
