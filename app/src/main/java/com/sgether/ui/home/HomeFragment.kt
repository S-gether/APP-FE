package com.sgether.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sgether.adapter.CalendarAdapter
import com.sgether.api.response.study.StudyTime
import com.sgether.databinding.FragmentHomeBinding
import com.sgether.model.DateColor
import com.sgether.model.DateModel
import com.sgether.ui.MainActivity
import com.sgether.util.DateHelper
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjusters

class HomeFragment : Fragment(), CalendarAdapter.OnItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private val calendarAdapter by lazy { CalendarAdapter(this) }

    private var yearMonth = YearMonth.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.readStudyTime()

        initView()
        initViewListeners()
        initViewModelListeners()
        initStudyTime()
    }

    private fun initView() {
        binding.textMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"


        binding.calendar.adapter = calendarAdapter.apply {
            list = getDateList(yearMonth.year, yearMonth.monthValue)
        }
    }

    private fun initViewListeners() {
        binding.btnPrev.setOnClickListener {
            initStudyTime()
            yearMonth = yearMonth.minusMonths(1)
            binding.textMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            calendarAdapter.list = getDateList(yearMonth.year, yearMonth.monthValue)
            viewModel.readStudyTime()
        }

        binding.btnNext.setOnClickListener {
            initStudyTime()
            yearMonth = yearMonth.plusMonths(1)
            binding.textMonth.text = "${yearMonth.year}년 ${yearMonth.monthValue}월"
            calendarAdapter.list = getDateList(yearMonth.year, yearMonth.monthValue)
            viewModel.readStudyTime()
        }
    }

    private fun initStudyTime() {
        binding.studyTimeTextview.text = "학습 분석할 날짜를 선택해주세요!"
        binding.focusTimeTextview.text = ""
        binding.aiCountTextview.text = ""
    }

    private fun initViewModelListeners(){
        viewModel.readStudyTimeResult.observe(viewLifecycleOwner){ studyTimeList ->

            val originalList = getDateList(yearMonth.year, yearMonth.monthValue)

            studyTimeList.forEach {
                val date = parseDate(it.study_date)
                originalList.forEach { dateModel ->
                    if(dateModel.year == date.year && dateModel.month == date.month && dateModel.date == date.date) {
                        dateModel.studyTime += it.total_time
                        dateModel.aiCount += it.ai_count
                        dateModel.focusTime = dateModel.studyTime - dateModel.aiCount * 180000
                    }
                }
            }
            calendarAdapter.selectedIndex = -1
            calendarAdapter.list = originalList
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

    private fun getDateList(year: Int, month: Int, day: Int = 1): List<DateModel> {
        val realStartDate = LocalDate.of(year, month, day)
        val startDate = realStartDate
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endDate = realStartDate.with(TemporalAdjusters.lastDayOfMonth())
        val dateList = mutableListOf<DateModel>()
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            dateList.add(DateModel(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth, dateColor = DateColor.NONE))
            currentDate = currentDate.plusDays(1)
        }

        return dateList
    }

    fun parseDate(dateStr: String): DateModel {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(dateStr, formatter)
        return DateModel(
            year = dateTime.year,
            month = dateTime.monthValue,
            date = dateTime.dayOfMonth
        )
    }

    override fun onItemClick(dateModel: DateModel) {
        val progressBar = binding.focusProgressBar
        progressBar.max = 100
        val progress = (dateModel.focusTime.toFloat() / dateModel.studyTime.toFloat()) * 100
        progressBar.progress = progress.toInt()

        binding.studyTimeTextview.text = "학습 시간: ${convertMillisecondsToTime(dateModel.studyTime)}"
        binding.focusTimeTextview.text = "집중 시간: ${convertMillisecondsToTime(dateModel.focusTime)}"
        binding.aiCountTextview.text = "지적 당한 횟수 : ${dateModel.aiCount}회"
        binding.studyToFocusTextview.text = "학습 시간 대비 집중도: ${dateModel.focusTime.toFloat() / dateModel.studyTime.toFloat()* 100}%"
    }
}

    fun convertMillisecondsToTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60)) % 24
        return "${hours}시간 ${minutes}분 ${seconds}초"
    }
