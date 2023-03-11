package com.sgether.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgether.R
import com.sgether.databinding.ItemDateBinding
import com.sgether.model.DateModel

class CalendarAdapter: RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selectedIndex: Int = 0

    var list = listOf<DateModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class CalendarViewHolder(var binding: ItemDateBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(dateModel: DateModel) {
            dateModel.let {
                binding.textDate.text = it.date.toString()
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