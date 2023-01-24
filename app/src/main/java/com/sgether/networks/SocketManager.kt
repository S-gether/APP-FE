package com.sgether.networks

import android.util.Log
import org.webrtc.*
import io.socket.client.IO
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager(onJoinListener: Emitter.Listener, onOffer: Emitter.Listener, onAnswer: Emitter.Listener, onIce: Emitter.Listener) {

    companion object {
        const val TAG = ".SocketManager"
    }

    private val socket by lazy {
        try {
            IO.socket("http://192.168.137.1:3000") // TODO: Set Address
            //IO.socket("https://every-squids-switch-118-221-204-105.loca.lt") // TODO: Set Address
        } catch (e: URISyntaxException){
            null
        }
    }

    init {
        socket?.apply {
            on("join", onJoinListener)
            on("offer", onOffer)
            on("answer", onAnswer)
            on("ice", onIce)
            connect()
            Log.d(TAG, "연결완료: ")
        }
    }

    fun joinRoom(room: String){
        socket?.emit("join_room", room)
    }

    fun sendOffer(sdp: SessionDescription?, room: String) {
        val data = JSONObject()
            .put("type", sdp?.type)
            .put("sdp", sdp?.description)
        socket?.emit("offer", data, room)
    }

    fun sendAnswer(sdp: SessionDescription?, room: String) {
        val data = JSONObject()
            .put("type", sdp?.type)
            .put("sdp", sdp?.description)
        socket?.emit("answer", data, room)
    }

    fun sendIce(iceCandidate: IceCandidate?, room: String){
        val data = JSONObject()
            .put("sdp", iceCandidate?.sdp)
            .put("sdpMid", iceCandidate?.sdpMid)
            .put("sdpMLineIndex", iceCandidate?.sdpMLineIndex)

        socket?.emit("ice", data, room);
    }
}