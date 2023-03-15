package com.sgether.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sgether.adapter.CalendarAdapter
import com.sgether.databinding.FragmentHomeBinding
import com.sgether.model.DateColor
import com.sgether.model.DateModel
import com.sgether.ui.MainActivity
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.TemporalAdjusters

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    private val calendarAdapter by lazy { CalendarAdapter() }

    private var yearMonth = YearMonth.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewListeners()
    }

    private fun initView() {
        binding.textMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"


        binding.calendar.adapter = calendarAdapter.apply {
            list = getDateList(yearMonth.year, yearMonth.monthValue)
        }
        // 백엔드 서버로부터 데이터를 받아온다고 가정
        val studyTime = 120
        val focusTime = 60
        val aiCount = 10

        // 각각의 TextView에 데이터를 설정
        binding.studyTimeTextview.text = studyTime.toString()
        binding.focusTimeTextview.text = focusTime.toString()
        binding.aiCountTextview.text = aiCount.toString()
    }

    private fun initViewListeners() {
        binding.btnPrev.setOnClickListener {
            yearMonth = yearMonth.minusMonths(1)
            binding.textMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            calendarAdapter.list = getDateList(yearMonth.year, yearMonth.monthValue)
        }

        binding.btnNext.setOnClickListener {
            yearMonth = yearMonth.plusMonths(1)
            binding.textMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            calendarAdapter.list = getDateList(yearMonth.year, yearMonth.monthValue)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getDateList(year: Int, month: Int, day: Int = 1): List<DateModel> {
        val realStartDate = LocalDate.of(year, month, day)
        val startDate = realStartDate
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endDate = realStartDate.with(TemporalAdjusters.lastDayOfMonth())
        val dateList = mutableListOf<DateModel>()
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            dateList.add(DateModel(currentDate.monthValue, currentDate.dayOfMonth, DateColor.NONE))
            currentDate = currentDate.plusDays(1)
        }

        return dateList
    }
}