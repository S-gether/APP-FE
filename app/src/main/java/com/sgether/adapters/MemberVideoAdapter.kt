package com.sgether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgether.databinding.ItemMemberVideoBinding
import com.sgether.models.MemberData
import com.sgether.networks.MyPeerManager
import kotlinx.coroutines.NonDisposableHandle.parent

class MemberVideoAdapter : RecyclerView.Adapter<MemberVideoAdapter.MemberVideoVideHolder>(){

    var list: List<MemberData> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MemberVideoVideHolder(val binding: ItemMemberVideoBinding, val peerManager: MyPeerManager): RecyclerView.ViewHolder(binding.root) {
        fun bind(memberData: MemberData){
            binding.textProfile.text = memberData.name
            if(memberData.isLocal){
                peerManager.startLocalSurface(binding.root.context, binding.surfaceViewRenderer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberVideoVideHolder {
        val binding = ItemMemberVideoBinding.inflate(LayoutInflater.from(parent.context))

        val myPeerManager = MyPeerManager(parent.context)
        myPeerManager.initSurfaceView(binding.surfaceViewRenderer)
        //myPeerManager.startLocalSurface(parent.context, binding.surfaceViewRenderer)


        return MemberVideoVideHolder(binding, myPeerManager)
    }

    override fun onBindViewHolder(holder: MemberVideoVideHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}