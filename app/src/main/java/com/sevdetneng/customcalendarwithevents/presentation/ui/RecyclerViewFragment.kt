package com.sevdetneng.customcalendarwithevents.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sevdetneng.customcalendarwithevents.base.BaseFragment
import com.sevdetneng.customcalendarwithevents.databinding.FragmentRecyclerViewBinding
import com.sevdetneng.customcalendarwithevents.presentation.ui.adapter.PageAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RecyclerViewFragment : BaseFragment<FragmentRecyclerViewBinding>(FragmentRecyclerViewBinding::inflate) {
    private val loadedDates : MutableList<Date> = mutableListOf()
    private var selectedMonthDate : Date? = null
    var latestPos = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val calendar = Calendar.getInstance()

        // Start from the current month
        selectedMonthDate = calendar.time
        binding.monthText.text = dateFormatter(selectedMonthDate!!)

        // Initiate the months list
        startList()

        // Start from the center of the list
        latestPos = loadedDates.size/2

        // Create dummy events list
        val events = mutableListOf<Date>()
        for(i in 1..10){
            calendar.add(Calendar.DATE,i)
            events.add(calendar.time)
        }

        val adapter = PageAdapter(requireContext(),events){
            val sdf = SimpleDateFormat("EE dd/MM/yyyy",Locale.getDefault())
            Toast.makeText(requireContext(),"Selected date is : ${sdf.format(it)}",Toast.LENGTH_LONG).show()
        }
        binding.pageRecyclerView.adapter = adapter
        binding.pageRecyclerView.itemAnimator = null
        adapter.submitList(loadedDates)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
        binding.pageRecyclerView.layoutManager = layoutManager

        // Add PagerSnapHelper to make RecyclerView scroll like a Pager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.pageRecyclerView)

        // Start calendar from the current month
        binding.pageRecyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate!!))

        binding.pageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    // Get the position of the selected month
                    val pos = layoutManager.findFirstVisibleItemPosition()

                    // If switched to a different month
                    if(latestPos != pos){
                        // Assign new selected date
                        selectedMonthDate = adapter.getItemByPos(pos)
                        binding.monthText.text = dateFormatter(selectedMonthDate!!)
                        // Assign new latest position
                        latestPos = pos

                        // If at the start of the list, load previous months and scroll back to the same month
                        if(pos == 0){
                            loadPreviousMonths()
                            adapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate!!))
                        }
                        // If at the end of the list, load next months and scroll back to the same month
                        if(pos == loadedDates.size-1){
                            loadNextMonths()
                            adapter.submitList(loadedDates.toMutableList())
                            recyclerView.scrollToPosition(adapter.getItemPos(selectedMonthDate!!))
                        }
                    }
                }
            }
        })
        binding.nextMonth.setOnClickListener{
            // Go to the next month
            if(latestPos +1 <= loadedDates.size-1)
                binding.pageRecyclerView.smoothScrollToPosition(latestPos+1)
        }
        binding.previousMonth.setOnClickListener {
            // Go to the previous month
            if(latestPos - 1 >= 0){
                binding.pageRecyclerView.smoothScrollToPosition(latestPos-1)
            }
        }
        super.onViewCreated(view, savedInstanceState)


    }

    private fun dateFormatter(date : Date) : String {
        val sdf = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun startList(){
        // Initiate the calendar months list
        val calendar = Calendar.getInstance()
        calendar.time = selectedMonthDate!!
        val currentDate = calendar.time

        // Add 5 months before current month
        for(i in -5..-1){
            calendar.add(Calendar.MONTH,i)
            loadedDates.add(calendar.time)
            calendar.time = currentDate
        }
        // Add current month
        loadedDates.add(currentDate)
        calendar.time = currentDate
        // Add 5 months after current month
        for(i in 1..5){
            calendar.add(Calendar.MONTH,i)
            loadedDates.add(calendar.time)
            calendar.time = currentDate
        }
    }

    private fun loadPreviousMonths(){
        val calendar = Calendar.getInstance()
        calendar.time = loadedDates[0]

        // Load the previous year
        for(i in 1..12){
            calendar.add(Calendar.MONTH,-1)
            loadedDates.add(calendar.time)
        }
        // Sort by time
        latestPos += 12
        loadedDates.sort()
    }
    private fun loadNextMonths(){
        val calendar = Calendar.getInstance()
        calendar.time = loadedDates.last()

        // Load the next year
        for(i in 1..12){
            calendar.add(Calendar.MONTH,1)
            loadedDates.add(calendar.time)
        }
        // Sort by time
        loadedDates.sort()
    }
}