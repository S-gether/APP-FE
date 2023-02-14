package com.sgether.ui.group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.sgether.R
import com.sgether.networks.RetrofitHelper
import com.sgether.networks.request.group.CreateAndEditGroupBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddGroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        lifecycleScope.launch(Dispatchers.IO) {
            val res = RetrofitHelper.groupService.createGroup(CreateAndEditGroupBody(
                "내방2",
                5,
                "1q2w3e4r"
            ))
            if(res.isSuccessful) {
                val message = res.body()?.message
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddGroupActivity, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("TAG", "onCreate: ${res.errorBody()?.string()}")
            }
        }
    }
}