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

    val readStudyTimeResult: MutableLiveData<List<StudyTime>> = MutableLiveData(listOf(
        StudyTime("thrif60", "2023-02-26T19:25:32.000Z", 60000 * 60, "", 0),
        StudyTime("thrif60", "2023-02-27T19:25:32.000Z", 60000 * 60 * 2, "", 0),
        StudyTime("thrif60", "2023-02-28T19:25:32.000Z", 60000 * 60 * 3, "", 1),

        StudyTime("thrif60", "2023-03-01T19:25:32.000Z", 60000 * 60 * 4, "", 0),
        StudyTime("thrif60", "2023-03-02T19:25:32.000Z", 60000 * 60 * 1, "", 0),
        StudyTime("thrif60", "2023-03-03T19:25:32.000Z", 60000 * 60 * 2, "", 2),
        StudyTime("thrif60", "2023-03-04T19:25:32.000Z", 60000 * 60 * 4, "", 0),
        StudyTime("thrif60", "2023-03-05T19:25:32.000Z", 60000 * 60 * 3, "", 0),
        StudyTime("thrif60", "2023-03-06T19:25:32.000Z", 60000 * 60 * 1, "", 4),
        StudyTime("thrif60", "2023-03-07T19:25:32.000Z", 60000 * 60 * 1, "", 0),
        StudyTime("thrif60", "2023-03-08T19:25:32.000Z", 60000 * 60 * 2, "", 0),
        StudyTime("thrif60", "2023-03-09T19:25:32.000Z", 60000 * 60 * 3, "", 0),
        StudyTime("thrif60", "2023-03-10T19:25:32.000Z", 60000 * 60 * 3, "", 0),
        StudyTime("thrif60", "2023-03-11T19:25:32.000Z", 60000 * 60 * 3, "", 0),
        StudyTime("thrif60", "2023-03-12T19:25:32.000Z", 60000 * 60 * 6, "", 4),
        StudyTime("thrif60", "2023-03-13T19:25:32.000Z", 60000 * 60 * 7, "", 0),
        StudyTime("thrif60", "2023-03-14T19:25:32.000Z", 60000 * 60 * 3, "", 0),
        StudyTime("thrif60", "2023-03-15T19:25:32.000Z", 60000 * 60 * 4, "", 0),
        StudyTime("thrif60", "2023-03-16T19:25:32.000Z", 60000 * 60 * 5, "", 0),
        StudyTime("thrif60", "2023-03-17T19:25:32.000Z", 60000 * 60 * 6, "", 2),
        StudyTime("thrif60", "2023-03-18T19:25:32.000Z", 60000 * 60 * 2, "", 0),
        StudyTime("thrif60", "2023-03-19T19:25:32.000Z", 60000 * 60 * 3, "", 3),
        StudyTime("thrif60", "2023-03-20T19:25:32.000Z", 60000 * 60 * 4, "", 0),
        StudyTime("thrif60", "2023-03-21T19:25:32.000Z", 60000 * 60 * 1, "", 0),
        StudyTime("thrif60", "2023-03-22T19:25:32.000Z", 60000 * 60 * 2, "", 0),
        StudyTime("thrif60", "2023-03-23T19:25:32.000Z", 60000 * 60 * 1, "", 0),
        StudyTime("thrif60", "2023-03-24T19:25:32.000Z", 60000 * 60 * 1, "", 0),
        StudyTime("thrif60", "2023-03-25T19:25:32.000Z", 60000 * 60 * 1, "", 1),
    ))

    fun readStudyTime() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val res = ApiClient.studyService.readUserStudyTime()
            if(res.isSuccessful) {
                //readStudyTimeResult.postValue(res.body()?.userSelectReseult)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}