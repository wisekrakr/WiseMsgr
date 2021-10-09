package com.wisekrakr.wisemessenger.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

class ChatRoomAdapter : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>(){

    private var listener: OnItemClickListener? = null
    private var context: Context? = null
    private var chatRooms = ArrayList<Any?>() as ArrayList<ChatRoom>

    class ChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        val contact: TextView = view.findViewById(R.id.tv_contact_name_private)
        val message: TextView = view.findViewById(R.id.tv_message_private)
        val date: TextView = view.findViewById(R.id.tv_date_private)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        context = parent.context
        return ChatRoomViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    viewType,
                    parent,
                    false
                )
        )
    }

    fun setData(arrayData: ArrayList<ChatRoom>) {
        chatRooms = arrayData
        notifyDataSetChanged()

        Log.d(TAG, "Set data for GroupsAdapter: groups size =  $itemCount")
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {

        chatRooms[position].participants.forEach {
            if(it.uid != firebaseAuth.uid){
                holder.contact.text = it.username
            }
        }

        //todo chatRooms[position].messages.forEach --> if message owner uid != me... show message (latest) and show date
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.private_chat_item
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    fun setClickListener(clickListener: OnItemClickListener) {
        this.listener = clickListener
    }

    interface OnItemClickListener {
        fun onClick(chatRoom: ChatRoom)
    }


}