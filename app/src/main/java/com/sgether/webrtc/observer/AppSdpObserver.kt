package com.sgether.webrtc.observer

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class AppSdpObserver: SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription?) {
    }

    override fun onSetSuccess() {
    }

    override fun onCreateFailure(s: String?) {
        Log.d(this::class.java.simpleName, "onCreateFailure: $s")
    }

    override fun onSetFailure(s: String?) {
        Log.d(this::class.java.simpleName, "onSetFailure: $s")
    }
}