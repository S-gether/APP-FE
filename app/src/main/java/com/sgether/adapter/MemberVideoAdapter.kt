package com.sgether.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemMemberVideoBinding
import com.sgether.model.MemberData
import com.sgether.webrtc.SocketManager
import com.sgether.util.Constants
import com.sgether.webrtc.MyPeerManager
import com.sgether.webrtc.observer.AppSdpObserver
import com.sgether.webrtc.observer.PeerConnectionObserver
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

class MemberVideoAdapter(var localUserName: String, var peerManager: MyPeerManager, var socketManager: SocketManager) : RecyclerView.Adapter<MemberVideoAdapter.MemberVideoVideHolder>(){

    var list: List<MemberData> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MemberVideoVideHolder(val binding: ItemMemberVideoBinding): RecyclerView.ViewHolder(binding.root) {

        private var peerConnection: PeerConnection? = null

        fun bind(memberData: MemberData){
            if(memberData.isLocal){
                peerManager.startLocalSurface(binding.root.context, binding.surfaceViewRenderer)
            }
            // PeerConnection 생성
            peerConnection = buildPeerConnection(object: PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) { // ICE 생성
                    socketManager.sendIce(p0, memberData.socketId) // ICE 전송
                }

                override fun onAddStream(p0: MediaStream?) {
                    p0?.videoTracks?.get(0)?.addSink(binding.surfaceViewRenderer)
                }
            })?.apply {
                addStream(peerManager.localStream)
            }
            memberData.peerConnection = peerConnection

            when(memberData.type) {
                Constants.TYPE_JOIN -> {
                    sendOffer(memberData.socketId)
                }
                Constants.TYPE_OFFER -> {
                    setRemoteDescription(memberData.socketId, memberData.sdp!!)
                }
            }
            binding.textProfile.text = memberData.name
        }

        private fun buildPeerConnection(peerConnectionObserver: PeerConnectionObserver): PeerConnection? {
            return peerManager.buildPeerConnection(binding.root.context, peerConnectionObserver)
        }

        private fun sendOffer(socketId: String) {
            peerManager.createOffer(peerConnection, object : AppSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    peerManager.setLocalDescription(peerConnection, object : AppSdpObserver() {
                        override fun onSetSuccess() {
                            socketManager.sendOffer(sdp, socketId, localUserName)
                        }
                    }, sdp)
                }
            })
        }

        private fun setRemoteDescription(socketId: String, sdp: SessionDescription) {
            peerManager.setRemoteDescription(peerConnection, object: AppSdpObserver() {
                override fun onSetSuccess() {
                    createAnswer(socketId)
                }
            }, sdp)
        }

        private fun createAnswer(socketId: String) {
            peerManager.createAnswer(peerConnection, object : AppSdpObserver() {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    setLocalDescription(socketId, sdp!!)
                }
            })
        }

        private fun setLocalDescription(socketId: String, sdp: SessionDescription) {
            peerManager.setLocalDescription(peerConnection, object : AppSdpObserver() {
                override fun onSetSuccess() {
                    // Answer 전송
                    socketManager.sendAnswer(sdp, socketId)
                }
            }, sdp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberVideoVideHolder {
        val binding = ItemMemberVideoBinding.inflate(LayoutInflater.from(parent.context))

        peerManager.initSurfaceView(binding.surfaceViewRenderer)

        return MemberVideoVideHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberVideoVideHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}