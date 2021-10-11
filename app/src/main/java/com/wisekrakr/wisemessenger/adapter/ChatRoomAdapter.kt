package com.wisekrakr.wisemessenger.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.repository.ChatMessageRepository.getChatMessage
import com.wisekrakr.wisemessenger.repository.UserProfileRepository.getUserProfile
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import com.wisekrakr.wisemessenger.utils.Extensions.getLatestChatMessage
import org.ocpsoft.prettytime.PrettyTime
import kotlin.collections.ArrayList

class ChatRoomAdapter : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>(){

    private var listener: OnItemClickListener? = null
    private var context: Context? = null
    private var chatRooms = ArrayList<Any?>() as ArrayList<ChatRoom>
    private var messages = ArrayList<Any?>() as ArrayList<ChatMessage>

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
                getUserProfile(it.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val avatarUrl = snapshot.getValue(UserProfile::class.java)!!.avatarUrl
                            Actions.ImageActions.loadImage(avatarUrl, holder.avatar)
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

                holder.contact.text = it.username
            }
        }
    }

    /**
     * Run through all chat room chat message uid's and add them to a array list, to ultimately
     * find the latest delivered chat message.
     * This way we order the chat messages by latest date.
     */
    private fun showChatRoomMessages(chatRoom: ChatRoom, holder: ChatRoomViewHolder){
        chatRoom.messages.keys.forEach {
            getChatMessage(it).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatMessage = snapshot.getValue(ChatMessage::class.java)

                    if(chatMessage != null){
                        messages.add(chatMessage)

                        showLatestMessage(messages, holder)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun showLatestMessage(messages: ArrayList<ChatMessage>,holder: ChatRoomViewHolder) {
        if(messages.isNotEmpty()){
            val lastMsg = getLatestChatMessage(messages)

            holder.message.text = lastMsg!!.message
            val p = PrettyTime()
            holder.date.text = p.format(lastMsg.date)
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