package com.sgether.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgether.R
import com.sgether.databinding.ItemDateBinding
import com.sgether.model.DateColor
import com.sgether.model.DateModel

class CalendarAdapter: RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selectedIndex: Int = 0

    var list = listOf<DateModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class CalendarViewHolder(var binding: ItemDateBinding): RecyclerView.ViewHolder(binding.root) {
        private val textDate = itemView.findViewById<TextView>(R.id.text_date)
        private val viewBackground = itemView.findViewById<View>(R.id.text_date)
        fun bind(dateModel: DateModel) {

            dateModel.let {
                binding.textDate.text = it.date.toString()
            }
            // 학습 시간 데이터가 있는 경우, 배경색을 설정합니다.
            if(dateModel.dateColor == DateColor.Q1){
                viewBackground.setBackgroundResource(R.color.date_q1)
            }
            else if(dateModel.dateColor == DateColor.Q2){
                viewBackground.setBackgroundResource(R.color.date_q2)
            }
            else if(dateModel.dateColor == DateColor.Q3){
                viewBackground.setBackgroundResource(R.color.date_q3)
            }
            else if (dateModel.dateColor == DateColor.Q4){
                viewBackground.setBackgroundResource(R.color.date_q4)
            }
            // 학습 시간 데이터가 없는 경우, 기본 배경색을 설정합니다.
            else {
                viewBackground.setBackgroundResource(R.color.white)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CalendarViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(list[position])

        var item = list[position]

        holder.itemView.setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
        }

        if(selectedIndex == position) {
            holder.itemView.setBackgroundResource(R.drawable.backgorund_date_selected)
            ((holder.itemView.background as GradientDrawable).mutate() as GradientDrawable).setColor(holder.binding.root.context.resources.getColor(item.dateColor.getColor()))
        } else {
            holder.itemView.setBackgroundResource(item.dateColor.getColor())
        }
    }
}