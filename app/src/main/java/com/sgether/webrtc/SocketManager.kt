package com.sgether.webrtc

import android.util.Log
import org.webrtc.*
import io.socket.client.IO
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager(onJoinListener: Emitter.Listener, onOffer: Emitter.Listener, onAnswer: Emitter.Listener, onIce: Emitter.Listener, onLeaveRoom: Emitter.Listener) {

    companion object {
        const val TAG = ".SocketManager"
    }

    private val socket by lazy {
        try {
            IO.socket("https://neat-corners-marry-14-38-13-95.loca.lt/") // TODO: Set Address
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
            on("leave_room", onLeaveRoom)
            connect()
            Log.d(TAG, "연결완료: ")
        }
    }

    fun joinRoom(roomName: String, userName: String){
        socket?.emit("join", roomName, userName)
    }

    fun sendOffer(sdp: SessionDescription?, socketId: String, nickName: String) {
        val data = JSONObject()
            .put("type", sdp?.type)
            .put("sdp", sdp?.description)
        socket?.emit("offer", data, socketId, nickName)
        Log.d(null, "sendOffer: ")
    }

    fun sendAnswer(sdp: SessionDescription?, socketId: String) {
        val data = JSONObject()
            .put("type", sdp?.type)
            .put("sdp", sdp?.description)
        socket?.emit("answer", data, socketId)
        Log.d(null, "sendAnswer: ")
    }

    fun sendIce(iceCandidate: IceCandidate?, remoteSocketId: String){
        val data = JSONObject()
            .put("sdp", iceCandidate?.sdp)
            .put("sdpMid", iceCandidate?.sdpMid)
            .put("sdpMLineIndex", iceCandidate?.sdpMLineIndex)

        socket?.emit("ice", data, remoteSocketId);
        Log.d(null, "sendIce: ")
    }

    fun disconnect(){
        socket?.disconnect()
    }
}