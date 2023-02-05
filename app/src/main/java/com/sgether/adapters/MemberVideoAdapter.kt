package com.sgether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemMemberVideoBinding
import com.sgether.models.MemberData
import com.sgether.webrtc.MyPeerManager

class MemberVideoAdapter(var peerManager: MyPeerManager) : RecyclerView.Adapter<MemberVideoAdapter.MemberVideoVideHolder>(){

    var list: List<MemberData> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MemberVideoVideHolder(val binding: ItemMemberVideoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(memberData: MemberData){
            binding.textProfile.text = memberData.name
            if(memberData.isLocal){
                peerManager.startLocalSurface(binding.root.context, binding.surfaceViewRenderer)

            }
            memberData.mediaStream?.videoTracks?.get(0)?.addSink(binding.surfaceViewRenderer)
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