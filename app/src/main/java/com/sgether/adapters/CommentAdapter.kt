package com.sgether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemReceivedCommentBinding
import com.sgether.databinding.ItemSentCommentBinding
import com.sgether.models.Comment

class CommentAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        const val TYPE_SENT = 0
        const val TYPE_RECEIVED = 1
    }

    var list: List<Comment> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class SentCommentViewHolder(val binding: ItemSentCommentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.textMessage.text = comment.message
        }
    }

    inner class ReceivedCommentViewHolder(val binding: ItemReceivedCommentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.textMessage.text = comment.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(list[position].userId == ""){
            TYPE_SENT
        } else {
            TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_SENT -> {
                val binding = ItemSentCommentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SentCommentViewHolder(binding)
            }
            else -> {
                val binding = ItemReceivedCommentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ReceivedCommentViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == TYPE_SENT) {
            (holder as SentCommentViewHolder).bind(list[position])
        } else {
            (holder as ReceivedCommentViewHolder).bind(list[position])
        }
    }

    override fun getItemCount() = list.size
}