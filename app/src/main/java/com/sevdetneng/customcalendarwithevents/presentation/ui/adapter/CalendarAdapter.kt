package com.sevdetneng.customcalendarwithevents.presentation.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sevdetneng.customcalendarwithevents.R
import com.sevdetneng.customcalendarwithevents.databinding.CalendarCellBinding
import com.sevdetneng.customcalendarwithevents.model.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarAdapter(
    val events: List<Date>,
    val context: Context,
    val selectedDate: Date?,
    val currentMonth: Date,
    val onItemClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarAdapter.ViewHolder>(CalendarDiffCallBack()) {

    inner class ViewHolder(private val binding: CalendarCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: CalendarDay) {


            if (isInTheSelectedMonth(date.date, currentMonth)) {

                binding.root.setOnClickListener {
                    // only click current month days
                    onItemClick(date)
                }

                val calendar = Calendar.getInstance()
                // if date is inside the selected month, set background resource
                if (areDatesEqual(calendar.time, date.date)) {
                    // Today
                    binding.backgroundConstraint.background =
                        ContextCompat.getDrawable(context, R.drawable.calendar_cell_today)
                } else {
                    var isEventDay = false
                    events.forEach {
                        if (areDatesEqual(it, date.date)) {
                            isEventDay = true
                            return@forEach
                        }
                    }
                    if (isEventDay) {
                        // event day
                        binding.backgroundConstraint.background =
                            ContextCompat.getDrawable(context, R.drawable.calendar_cell_event)
                    } else {
                        // normal day
                        binding.backgroundConstraint.background =
                            ContextCompat.getDrawable(context, R.drawable.calendar_cell_background)
                    }
                }
            } else {
                // if not in the selected month, display gray
                binding.backgroundConstraint.background =
                    ContextCompat.getDrawable(context, R.drawable.calendar_cell_gray)
                binding.textView.setTextColor(Color.parseColor("#D3D3D3"))
            }
            binding.textView.text = date.dayOfMonth
            if (selectedDate != null) {
                binding.backgroundConstraint.isSelected = areDatesEqual(selectedDate, date.date)
            }
        }
    }

    fun areDatesEqual(dateFirst: Date, dateSecond: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(dateFirst).equals(sdf.format(dateSecond))
    }

    fun isInTheSelectedMonth(dateFirst: Date, dateSecond: Date): Boolean {
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        return sdf.format(dateFirst).equals(sdf.format(dateSecond))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CalendarCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position))
    }
}

class CalendarDiffCallBack() : DiffUtil.ItemCallback<CalendarDay>() {
    override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }

}