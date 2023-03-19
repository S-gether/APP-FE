package com.sgether.adapter

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.sgether.databinding.ItemMemberVideoBinding
import com.sgether.model.MemberData
import com.sgether.webrtc.SocketManager
import com.sgether.util.Constants
import com.sgether.webrtc.MyPeerManager
import com.sgether.webrtc.observer.AppSdpObserver
import com.sgether.webrtc.observer.PeerConnectionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import org.webrtc.EglRenderer
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import java.io.ByteArrayOutputStream
import kotlin.concurrent.timer

class MemberVideoAdapter(var localUserName: String, var peerManager: MyPeerManager, var socketManager: SocketManager, var module: Module) : RecyclerView.Adapter<MemberVideoAdapter.MemberVideoVideHolder>(){

    var list: List<MemberData> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface BitmapListener {
        fun onBitmap(bitmap: Bitmap)
    }

    inner class MemberVideoVideHolder(val binding: ItemMemberVideoBinding): RecyclerView.ViewHolder(binding.root) {

        private var peerConnection: PeerConnection? = null

        fun bind(memberData: MemberData){
            if(memberData.isLocal){
                peerManager.startLocalSurface(binding.root.context, binding.surfaceViewRenderer)


                val listener = object: BitmapListener {
                    override fun onBitmap(bitmap: Bitmap) {
                        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB)

                        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
                        val score = outputTensor.dataAsFloatArray
                        var max = -1f
                        var maxIndex = 0
                        for (i in score.indices) {
                            if(score[i] > max) {
                                max = score[i]
                                maxIndex = i
                            }
                        }
                        val result = when(maxIndex) {
                            0 -> "기쁨"
                            1 -> "당황"
                            2 -> "분노"
                            3 -> "불안"
                            4 -> "상처"
                            5 -> "슬픔"
                            6 -> "중립"
                            else -> "NULL"
                        }

                        if(result == "중립") {
                            Log.d("PYTORCH", "onBitmap: ${System.currentTimeMillis()} 집중")
                        } else {
                            Log.d("PYTORCH", "onBitmap: ${System.currentTimeMillis()} 집중X")

                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            //Glide.with(binding.root)
                            //    .load(bitmap)
                            //    .into(binding.imageCapture)
                            //TODO 이미지 캡쳐한 것 모델에 넣기

                        }
                    }
                }
                timer(period = 1000) {
                    binding.surfaceViewRenderer.addFrameListener({
                         listener.onBitmap(it)
                    }, 0.5f)
                }
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

    fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
}