package com.sgether.networks

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class AppSdpObserver: SdpObserver {
    companion object {
        const val TAG = ".RoomActivity"
    }

    override fun onCreateSuccess(sdp: SessionDescription?) {
    }

    override fun onSetSuccess() {
    }

    override fun onCreateFailure(s: String?) {
        Log.d(TAG, "onCreateFailure: $s")
    }

    override fun onSetFailure(s: String?) {
        Log.d(TAG, "onSetFailure: $s")
    }
}