package com.sgether.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemMemberRankingBinding
import com.sgether.model.MemberRanking

class MemberRankingAdapter : RecyclerView.Adapter<MemberRankingAdapter.MemberRankingViewHolder>() {

    var list: List<MemberRanking> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MemberRankingViewHolder(val binding: ItemMemberRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberRanking: MemberRanking) {
            binding.textMemberRank.text = memberRanking.rank.toString()
            binding.textMemberName.text = memberRanking.name
            binding.textUserElapsed.text = "22d 12h 59m" // TODO : 랭킹 시간 설정
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberRankingViewHolder {
        val binding =
            ItemMemberRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        return MemberRankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberRankingViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size


}