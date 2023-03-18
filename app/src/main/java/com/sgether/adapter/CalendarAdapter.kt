package com.sgether.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
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
import okhttp3.internal.parseCookie

class CalendarAdapter(var listener: OnItemClickListener): RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    var selectedIndex: Int = -1

    var list = listOf<DateModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class CalendarViewHolder(var binding: ItemDateBinding): RecyclerView.ViewHolder(binding.root) {
        private val viewBackground = itemView.findViewById<View>(R.id.layout_date)
        fun bind(dateModel: DateModel) {
            Log.d(".DateModel", "bind: $dateModel")

            dateModel.let {
                binding.textDate.text = it.date.toString()
            }

            // 색상 계산
            val hour = convertMillisecondsToTime(dateModel.focusTime)
            if(hour == 0L) {
                dateModel.dateColor = DateColor.NONE
            } else if(hour <= 1) {
                dateModel.dateColor = DateColor.Q1
            } else if(hour <= 2) {
                dateModel.dateColor = DateColor.Q2
            } else if(hour <= 3) {
                dateModel.dateColor = DateColor.Q3
            } else {
                dateModel.dateColor = DateColor.Q4
            }
            viewBackground.setBackgroundColor(dateModel.dateColor.getColor())

        }

        fun convertMillisecondsToTime(milliseconds: Long): Long {
            val hours = (milliseconds / (1000 * 60 * 60)) % 24
            return hours
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
            listener.onItemClick(list[position])

            notifyDataSetChanged()
        }

        if(selectedIndex != -1 && selectedIndex == position) {
            holder.itemView.setBackgroundResource(R.drawable.backgorund_date_selected)
            ((holder.itemView.background as GradientDrawable).mutate() as GradientDrawable).setColor(holder.binding.root.context.resources.getColor(item.dateColor!!.getColor()))
        } else {
            holder.itemView.setBackgroundResource(item.dateColor.getColor())
        }
    }

    interface OnItemClickListener {
        fun onItemClick(dataModel: DateModel)
    }
}