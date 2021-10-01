package com.wisekrakr.wisemessenger.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.utils.Actions.ImageActions.loadImage
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>(){

    private var listener: OnItemClickListener? = null
    private var context: Context? = null
    private var groups = ArrayList<Any?>() as ArrayList<Group>

    class GroupsViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        val name: TextView = view.findViewById(R.id.tv_group_name)
        val avatar: ImageView = view.findViewById(R.id.img_group_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        context = parent.context
        return GroupsViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    viewType,
                    parent,
                    false
                )
        )
    }

    fun setData(arrayData: ArrayList<Group>) {
        groups = arrayData
        notifyDataSetChanged()

        Log.d(TAG, "Set data for GroupsAdapter: groups size =  $itemCount")
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {

        holder.name.text = groups[position].groupName
        if(groups[position].avatarUrl.isBlank()){
            Picasso.get().load(R.drawable.avatar).into(holder.avatar)
        }else{
            loadImage(groups[position].avatarUrl, holder.avatar)
        }


        Log.d(TAG, " Group shown: " + groups[position].uid)

        holder.itemView.rootView.setOnClickListener {
            listener!!.onClick(groups[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.group_item
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    fun setClickListener(clickListener: OnItemClickListener) {
        this.listener = clickListener
    }

    interface OnItemClickListener {
        fun onClick(group: Group)
    }


}