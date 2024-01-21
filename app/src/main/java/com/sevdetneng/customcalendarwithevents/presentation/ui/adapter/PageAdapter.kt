package com.sevdetneng.customcalendarwithevents.presentation.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sevdetneng.customcalendarwithevents.databinding.PageRecyclerItemBinding
import com.sevdetneng.customcalendarwithevents.model.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PageAdapter(val context: Context, val events: List<Date>,val onItemClick : (date : Date?) -> Unit) :
    ListAdapter<Date, PageAdapter.ViewHolder>(PageAdapterDiffCallback()) {
    var selectedDate: Date? = null
    var selectedMonthPos: Int? = null

    inner class ViewHolder(private val binding: PageRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {

            // Get the month for the given date
            val list = daysInMonthList(date)

            val adapter = CalendarAdapter(events, context, selectedDate, date) { calendarDay ->

                // Single selection
                // If clicked on the selected day, unselect
                if (areDatesEqual(selectedDate, calendarDay.date)) {
                    val lastPos = selectedMonthPos
                    selectedMonthPos = null
                    selectedDate = null
                    notifyItemChanged(lastPos!!)
                } else {

                    // If clicked on an unselected day, select
                    // If not selected a day before, select
                    if (selectedMonthPos == null) {
                        selectedMonthPos = adapterPosition
                        selectedDate = calendarDay.date
                    } else {

                        // If already selected a day before, switch the selected date
                        notifyItemChanged(selectedMonthPos!!)
                        selectedMonthPos = adapterPosition
                        selectedDate = calendarDay.date
                    }
                    notifyItemChanged(selectedMonthPos!!)
                    onItemClick(selectedDate)
                }
            }
            binding.apply {
                daysRecycler.layoutManager = GridLayoutManager(context, 7)
                daysRecycler.adapter = adapter
                daysRecycler.itemAnimator = null
            }
            adapter.submitList(list)
        }

    }

    fun areDatesEqual(dateFirst: Date?, dateSecond: Date?): Boolean {
        // Function to compare two dates are the same day
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        if (dateFirst == null || dateSecond == null) {
            return false
        }
        return sdf.format(dateFirst).equals(sdf.format(dateSecond))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PageRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val vh = ViewHolder(binding)
        return vh
    }

    fun getItemPos(date: Date): Int {
        var selectedDate: Date? = null
        currentList.forEach {
            if (areDatesEqual(it, date)) {
                selectedDate = it
                return@forEach
            }
        }
        return currentList.indexOf(selectedDate)
    }

    fun getItemByPos(pos: Int): Date {
        return currentList[pos]
    }

    fun daysInMonthList(date: Date): List<CalendarDay> {

        val daysInMonthList: MutableList<CalendarDay> = mutableListOf()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val month = calendar.get(Calendar.MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        when (dayOfWeek) {
            // FILL PREVIOUS MONTH
            0 -> {
                // SUNDAY
                val lastMonthCalendar = Calendar.getInstance()
                lastMonthCalendar.time = calendar.time
                lastMonthCalendar.add(Calendar.MONTH, -1)
                val lastMonthCalendarLastDay =
                    lastMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                lastMonthCalendar.set(Calendar.DAY_OF_MONTH, lastMonthCalendarLastDay - 5)
                for (i in 1..6) {
                    daysInMonthList.add(
                        CalendarDay(
                            lastMonthCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                            lastMonthCalendar.time
                        )
                    )
                    lastMonthCalendar.add(Calendar.DATE, 1)
                }
            }

            else -> {
                // OTHER DAYS
                val lastMonthCalendar = Calendar.getInstance()
                lastMonthCalendar.time = calendar.time
                lastMonthCalendar.add(Calendar.MONTH, -1)
                val lastMonthCalendarLastDay =
                    lastMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                lastMonthCalendar.set(Calendar.DAY_OF_MONTH, lastMonthCalendarLastDay)
                for (i in 2..dayOfWeek) {
                    lastMonthCalendar.add(Calendar.DATE, -(dayOfWeek - i))
                    daysInMonthList.add(
                        CalendarDay(
                            lastMonthCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                            lastMonthCalendar.time
                        )
                    )
                    lastMonthCalendar.time = calendar.time
                    lastMonthCalendar.add(Calendar.MONTH, -1)
                    lastMonthCalendar.set(Calendar.DAY_OF_MONTH, lastMonthCalendarLastDay)
                }
            }
        }
        while (month == calendar.get(Calendar.MONTH)) {
            // FILL THE DAYS OF MONTH
            daysInMonthList.add(
                CalendarDay(
                    calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    calendar.time
                )
            )
            calendar.add(Calendar.DATE, 1)
        }
        val nextMonthCalendar = Calendar.getInstance()
        nextMonthCalendar.time = calendar.time
        nextMonthCalendar.add(Calendar.MONTH, 1)
        nextMonthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (daysInMonthList.size < 42) {
            // FILL NEXT MONTH
            daysInMonthList.add(
                CalendarDay(
                    nextMonthCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                    nextMonthCalendar.time

                )
            )
            nextMonthCalendar.add(Calendar.DATE, 1)
        }
        Log.d("daylist", daysInMonthList.toString())
        return daysInMonthList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PageAdapterDiffCallback() : DiffUtil.ItemCallback<Date>() {
    override fun areItemsTheSame(oldItem: Date, newItem: Date): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Date, newItem: Date): Boolean {
        return oldItem == newItem
    }

}