package com.sgether.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.sgether.databinding.ItemGroupBinding
import com.sgether.models.GroupModel
import com.sgether.networks.RetrofitHelper
import com.sgether.ui.group.MyGroupFragment
import com.sgether.ui.group.MyGroupFragmentDirections
import com.sgether.ui.search.SearchFragment
import com.sgether.ui.search.SearchFragmentDirections
import com.sgether.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupAdapter(private val scope: CoroutineScope, private val navController: NavController, var simpleName: String, var token: String) :
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {


    var list: List<GroupModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: GroupModel) {
            binding.textGroupName.text = group.room_name
            binding.textGroupInfo.text = group.id
            binding.imageGroupProfile.setImageBitmap(null)
            loadGroupProfile(group.id!!, token)

            binding.root.setOnClickListener {
                var action: NavDirections? = null
                when (simpleName) {
                    MyGroupFragment::class.java.simpleName -> {
                        action = MyGroupFragmentDirections.actionMyGroupFragmentToGroupInfoFragment(
                            group
                        )
                    }
                    SearchFragment::class.java.simpleName -> {
                        action = SearchFragmentDirections.actionSearchFragmentToGroupInfoFragment(
                            group
                        )
                    }
                }
                navController.navigate(action!!)
            }
        }

        private fun loadGroupProfile(groupId: String, token: String) {

            val url = GlideUrl("${Constants.serverUrl}upload/group/${groupId}", LazyHeaders.Builder()
                .addHeader(Constants.KEY_AUTHORIZATION, "Bearer $token")
                .build())

            Glide.with(binding.root)
                .load(url)
                .circleCrop()
                .into(binding.imageGroupProfile)
        }
    }

    inner class SwipeToDeleteCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(0, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val item = list[position]

            // delete the item from the adapter
            list = list.toMutableList().apply { removeAt(position) }

            // notify the adapter about the removed item
            notifyItemRemoved(position)
        }
    }

    val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback())

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}

