package com.wisekrakr.wisemessenger.components

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.adapter.ChatMessageAdapter
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

object ChatMessageUtils {

    fun onChatMessageLongClick(
        context: Context,
        chatRoom: ChatRoom,
        text: EditText,
        onShowMessages: Unit
    ): ChatMessageAdapter.OnItemLongClickListener {
        return object : ChatMessageAdapter.OnItemLongClickListener {
            override fun onLongClick(chatMessage: ChatMessage) {
                Log.d(TAG, "Clicked on chat message ${chatMessage.message}")

                val dialog = CustomDialog(context)
                dialog.show()

                val title = dialog.findViewById<TextView>(R.id.dialog_title)
                title.text = "What would you like to do?"

                val body = dialog.findViewById<TextView>(R.id.dialog_body)
                body.text = "Remove or Copy Or Forward"

                val remove = dialog.findViewById<ImageView>(R.id.dialog_remove_msg)
                val copy = dialog.findViewById<ImageView>(R.id.dialog_copy_msg)
                val forward = dialog.findViewById<ImageView>(R.id.dialog_forward_msg)
                val close = dialog.findViewById<Button>(R.id.dialog_close_btn)

                remove.setOnClickListener {
                    EventManager.onRemovingChatMessage(chatMessage, chatRoom, context)
                    onShowMessages
                }

                copy.setOnClickListener {
                    text.setText(chatMessage.message)
                }

                forward.setOnClickListener {

                }

                close.setOnClickListener {
                    dialog.dismiss()
                }

                if(chatMessage.sender!!.uid != FirebaseUtils.firebaseAuth.uid){
                    remove.visibility = View.GONE
                    body.text = "Copy Or Forward"
                }
            }
        }
    }

}