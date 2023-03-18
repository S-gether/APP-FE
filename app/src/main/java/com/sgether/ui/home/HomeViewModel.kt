package com.sgether.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgether.api.ApiClient
import com.sgether.api.request.study.CreateStudyTimeBody
import com.sgether.api.response.study.StudyTime
import com.sgether.model.LiveDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel: ViewModel() {

    val readStudyTimeResult: MutableLiveData<List<StudyTime>> = MutableLiveData()

    fun readStudyTime() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val res = ApiClient.studyService.readUserStudyTime()
            if(res.isSuccessful) {
                readStudyTimeResult.postValue(res.body()?.groupSelectReseult)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}