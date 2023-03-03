package com.sgether.webrtc.observer

import android.util.Log
import org.webrtc.*

abstract class PeerConnectionObserver: PeerConnection.Observer {
    
    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Log.d(this::class.java.simpleName, "onSignalingChange: $p0")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Log.d(this::class.java.simpleName, "onIceConnectionChange: $p0")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Log.d(this::class.java.simpleName, "onIceConnectionReceivingChange: $p0")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Log.d(this::class.java.simpleName, "onIceGatheringChange: $p0")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
    }

    override fun onRemoveStream(p0: MediaStream?) {
    }

    override fun onDataChannel(p0: DataChannel?) {
    }

    override fun onRenegotiationNeeded() {
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
    }
}