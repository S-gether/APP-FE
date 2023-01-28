package com.sgether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemCommentSenderBinding
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

    inner class SentCommentViewHolder(val binding: ItemCommentSenderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.textMessage.text = comment.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_SENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_SENT -> {
                val binding = ItemCommentSenderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SentCommentViewHolder(binding)
            }
            else -> { // TODO: Receiver
                val binding = ItemCommentSenderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SentCommentViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == TYPE_SENT) {
            (holder as SentCommentViewHolder).bind(list[position])
        }
    }

    override fun getItemCount() = list.size
}