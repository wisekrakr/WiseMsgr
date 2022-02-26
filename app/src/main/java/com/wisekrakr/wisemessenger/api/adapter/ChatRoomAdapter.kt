package com.wisekrakr.wisemessenger.api.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import com.wisekrakr.wisemessenger.utils.Extensions.getLatestChatMessage
import org.ocpsoft.prettytime.PrettyTime
import kotlin.collections.ArrayList

class ChatRoomAdapter : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>(){

    private lateinit var holder: ChatRoomViewHolder
    private var listener: OnItemClickListener? = null
    private var context: Context? = null
    private var chatRooms = ArrayList<ChatRoom>()
    private var messages = ArrayList<ChatMessage>()

    class ChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        val contact: TextView = view.findViewById(R.id.tv_contact_name_private)
        val message: TextView = view.findViewById(R.id.tv_message_private)
        val date: TextView = view.findViewById(R.id.tv_date_private)
        val avatar: ImageView = view.findViewById(R.id.img_contact_avatar_private)
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

        Log.d(TAG, "Set data for ChatRoomAdapter: conversation size =  $itemCount")
    }


    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        this.holder = holder
        val chatRoom = chatRooms[position]

        showChatRoomParticipants(chatRoom, holder)
        showChatRoomMessages(chatRoom, holder)

        holder.itemView.rootView.setOnClickListener {
            listener!!.onClick(chatRooms[position])
        }
    }


    /**
     * Run through all chat room participants, the one that is not the current user, get its profile.
     * With the User profile we can populate name and avatar of the contact item.
     */
    private fun showChatRoomParticipants(chatRoom: ChatRoom, holder: ChatRoomViewHolder){
        chatRoom.participants.forEach {
            if(it.uid != firebaseAuth.uid){

                ApiManager.Profiles.onGetUser(it.uid){ user->
                    Actions.ImageActions.loadImage(user.avatarUrl, holder.avatar)
                }

                holder.contact.text = it.username
            }
        }
    }

    /**
     * Since the chatroom has to be updated with every new message, this method has to be public for use
     * in the activity or fragment where the chatroom list gets updated.
     *
     * Run through all chat room chat message uid's and add them to a array list, to ultimately
     * find the latest delivered chat message.
     * This way we order the chat messages by latest date.
     */
    fun showChatRoomMessages(chatRoom: ChatRoom, holder: ChatRoomViewHolder?){

        chatRoom.messages.keys.forEach {
            ApiManager.Messages.onGetChatMessage(
                it,
                messages
            ){
                showLatestMessage(messages, this@ChatRoomAdapter.holder)
            }
        }
    }

    private fun showLatestMessage(messages: ArrayList<ChatMessage>,holder: ChatRoomViewHolder) {
        if(messages.isNotEmpty()){
            val lastMsg = getLatestChatMessage(messages)

            if(lastMsg != null){
                holder.message.text = lastMsg.message
                val p = PrettyTime()
                holder.date.text = p.format(lastMsg.date)
            }
        }
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