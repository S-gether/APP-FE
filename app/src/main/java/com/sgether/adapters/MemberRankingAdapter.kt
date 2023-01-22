package com.sgether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemMemberRankingBinding
import com.sgether.models.MemberRanking

class MemberRankingAdapter : RecyclerView.Adapter<MemberRankingAdapter.UserRankingViewHolder>() {

    var list: List<MemberRanking> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class UserRankingViewHolder(val binding: ItemMemberRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberRanking: MemberRanking) {
            binding.textMemberRank.text = memberRanking.rank.toString()
            binding.textMemberName.text = memberRanking.name
            binding.textUserElapsed.text = "22d 12h 59m" // TODO : 랭킹 시간 설정
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRankingViewHolder {
        val binding =
            ItemMemberRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        return UserRankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserRankingViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size


}