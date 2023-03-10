package com.sgether.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgether.databinding.ItemMemberRankingBinding
import com.sgether.model.MemberRanking
import com.sgether.util.DateHelper

class MemberRankingAdapter : RecyclerView.Adapter<MemberRankingAdapter.MemberRankingViewHolder>() {

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
                binding.textStudyTime.text = DateHelper.diffFormat(it.studyTime)

                binding.imageMaster.visibility = if(it.isMaster) View.VISIBLE else View.INVISIBLE

                Glide.with(binding.root)
                    .load(it.imageUrl)
                    .circleCrop()
                    .into(binding.imageProfile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberRankingViewHolder {
        val binding =
            ItemMemberRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberRankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberRankingViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size


}