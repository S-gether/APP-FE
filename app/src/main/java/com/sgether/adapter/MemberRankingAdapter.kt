package com.sgether.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgether.databinding.ItemMemberRankingBinding
import com.sgether.model.MemberRanking
import com.sgether.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.format

class MemberRankingAdapter(var token: String, var userId: String) : RecyclerView.Adapter<MemberRankingAdapter.MemberRankingViewHolder>() {

    var list: List<MemberRanking> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MemberRankingViewHolder(val binding: ItemMemberRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberRanking: MemberRanking) {
            memberRanking.let {
                binding.textName.text = it.name
                binding.textIntroduce.text = it.introduce
                binding.textStudyTime.text = formatDuration(it.studyTime)

                binding.imageMaster.visibility = if(it.isMaster) View.VISIBLE else View.INVISIBLE
                loadUserProfile(it.user_id, token, binding.imageProfile)
            }
        }
    }

    fun formatDuration(milliseconds: Long): String {
        val seconds = (milliseconds / 1000).toInt()
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return "%02d일 %02d시간 %02d분".format(days, hours % 24, minutes % 60)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberRankingAdapter.MemberRankingViewHolder {
        val binding =
            ItemMemberRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberRankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberRankingAdapter.MemberRankingViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size


}