package com.sgether.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PostProcessor
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemMemberVideoBinding
import com.sgether.model.MemberData
import com.sgether.util.Constants
import com.sgether.webrtc.MyPeerManager
import com.sgether.webrtc.SocketManager
import com.sgether.webrtc.observer.AppSdpObserver
import com.sgether.webrtc.observer.PeerConnectionObserver
import es.dmoral.toasty.Toasty
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import java.io.ByteArrayOutputStream
import kotlin.concurrent.timer
import kotlin.math.pow


class MemberVideoAdapter(var listener: AiListener, var localUserName: String, var peerManager: MyPeerManager, var socketManager: SocketManager, var module: Module) : RecyclerView.Adapter<MemberVideoAdapter.MemberVideoVideHolder>(){

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

                        val matrix = Matrix()
                        matrix.postRotate(90.0f)
                        val bitmapResult = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true)
                        val resizedBitmap = Bitmap.createScaledBitmap(bitmapResult,
                            PrePostProcessor.mInputWidth,
                            PrePostProcessor.mInputHeight,
                            true
                        )

                        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                            resizedBitmap,
                            PrePostProcessor.NO_MEAN_RGB,
                            PrePostProcessor.NO_STD_RGB
                        )
                        val outputTuple: Array<IValue> =
                            module.forward(IValue.from(inputTensor)).toTuple()
                        val outputTensor = outputTuple[0].toTensor()
                        val outputs = outputTensor.dataAsFloatArray

                       val  mImgScaleX = bitmap.width.toFloat() / PrePostProcessor.mInputWidth
                        val mImgScaleY = bitmap.height.toFloat() / PrePostProcessor.mInputHeight

                        val mIvScaleX =
                            if (bitmap.width > bitmap.height) binding.surfaceViewRenderer.width.toFloat() / bitmap.width else binding.surfaceViewRenderer.height.toFloat() / bitmap.height
                        val mIvScaleY =
                            if (bitmap.height > bitmap.width) {
                                binding.surfaceViewRenderer.height.toFloat() / bitmap.height
                            } else binding.surfaceViewRenderer.width.toFloat() / bitmap.width

                        val mStartX = (binding.surfaceViewRenderer.width - mIvScaleX * bitmap.width) / 2
                        val mStartY = (binding.surfaceViewRenderer.height - mIvScaleY * bitmap.height) / 2

                        val results = PrePostProcessor.outputsToNMSPredictions(
                            outputs,
                            mImgScaleX,
                            mImgScaleY,
                            mIvScaleX,
                            mIvScaleY,
                            mStartX,
                            mStartY
                        )
                        //후처리
                        val result = postProcessing(results)
                        if (result == 0) {
                            listener.onCount()
                        }

                        Log.d(null, "postProcessing: $result")
                    }
                }
                timer(period = 3000) {
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

        val personCenterList = mutableListOf<Float>()
        val tempCenterRatioX = 0
        val tempCenterRatioY = 1

        val con_min_distance = 0.01f
        val con_max_distance = 0.8f

        private fun postProcessing(results: List<Result>): Int {
            // 인간 박스
            val personRectAreaList = results.filter { it.classIndex == 0 }.map { it.rect.width() * it.rect.height() }
            // 휴대폰 박스
            val phoneRectAreaList = results.filter { it.classIndex == 67 }.map { it.rect.width() * it.rect.height() }

            // 인간 존재
            if (personRectAreaList.isNotEmpty()) {
                // 휴대폰 존재X
                if(phoneRectAreaList.isEmpty()) {
                    val pos = results.filter { it.classIndex == 0 }
                        .maxByOrNull { it.rect.width() * it.rect.height() }

                    val centerX = pos?.rect?.centerX()?:0
                    val centerY = pos?.rect?.centerY()?:0

                    val result = ((tempCenterRatioX - (centerX.div(binding.surfaceViewRenderer.width.toFloat()))).pow(2) + (tempCenterRatioY - (centerY.div(binding.surfaceViewRenderer.height.toFloat()))).pow(2)).pow(0.5f)
                    personCenterList.add(result)

                    if(personCenterList.size >= 20) {
                        personCenterList.removeFirst()

                        personCenterList.forEach {
                            Log.d(null, "postProcessing: $it")
                        }
                        if(personCenterList.filter { it < con_min_distance }.size == 20) {
                            Toasty.error(binding.root.context, "Don't sleep!", 1000).show()
                            Log.d(null, "postProcessing: 너무 안움직여 ${personCenterList.filter { it < con_min_distance }.size}")
                            return 0
                        } else if (personCenterList.filter { it > con_max_distance }.size >= 10) {
                            Toasty.error(binding.root.context, "Be concentrate!", 1000).show()
                            Log.d(null, "postProcessing: 그만 움직여 ${personCenterList.filter { it > con_min_distance }.size}")
                            return 0
                        }
                        return 1
                    }
                    Log.d(null, "postProcessing: 데이터 수집중")
                    return 2
                } else {
                    Toasty.error(binding.root.context, "Don't use smartphone!", 1000).show()
                    Log.d(null, "postProcessing: 휴대폰 그만 만져")
                    return 0
                }
            } else {
                Toasty.error(binding.root.context, "Don't leave your seat!", 1000).show()
                Log.d(null, "postProcessing: 사람 미존재")
                return 0
            }
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

    private fun vibrate(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(pattern)
        } else {
            vibrator.vibrate(1000)
        }
    }

    interface AiListener {
        fun onCount()
    }
}