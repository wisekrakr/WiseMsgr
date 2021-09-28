package com.wisekrakr.wisemessenger.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.adapter.utils.ChatViewHolder
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

internal class ChatAdapter : RecyclerView.Adapter<ChatViewHolder<*>>(){

    private var context: Context? = null
    private var messages = ArrayList<ChatMessage>()

    class ChatMeViewHolder(val view: View) : ChatViewHolder<ChatMessage>(view) {
        private val msg = view.findViewById<TextView>(R.id.tv_message_chat)
        private val date = view.findViewById<TextView>(R.id.tv_date_chat_me)

        override fun bind(item: ChatMessage, context: Context?) {
            msg.text = item.message
            msg.setTextColor(ContextCompat.getColor(context!!, R.color.light_gray))

            date.text = item.date.toString()
        }
    }


    class ChatOtherViewHolder(val view: View) : ChatViewHolder<ChatMessage>(view) {
        private val msg = view.findViewById<TextView>(R.id.tv_message_chat)
//        private val avatar = view.findViewById<CircleImageView>(R.id.circle_img_avatar_to)
        private val date = view.findViewById<TextView>(R.id.tv_date_chat_other)
        private val username = view.findViewById<TextView>(R.id.tv_username_chat_other)

        override fun bind(item: ChatMessage, context: Context?) {
            msg.text = item.message
            msg.setTextColor(ContextCompat.getColor(context!!, R.color.light_gray))
            username.text = item.sender?.username
//            Actions.ImageActions.loadImage(item.to.avatarUrl, avatar)

            date.text = item.date.toString()

        }
    }

    fun setData(arrayData: ArrayList<ChatMessage>) {
        messages = arrayData
        notifyDataSetChanged()

        Log.d(TAG, "Set data for ChatAdapter: chat size =  $itemCount")
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder<*> {

        context = parent.context

        return if(viewType == ChatMessage.TYPE_ME_MESSAGE){
            val view = LayoutInflater.from(context).inflate(R.layout.chat_message_me, parent, false)
            ChatMeViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_other, parent, false)
            ChatOtherViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder<*>, position: Int) {
        val item = messages[position]

        when (holder) {
            is ChatMeViewHolder -> holder.bind(item, context)
            is ChatOtherViewHolder -> holder.bind(item, context)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int = messages[position].messageType

}