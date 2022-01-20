package com.wisekrakr.wisemessenger.api.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.model.ChatRequest
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import org.ocpsoft.prettytime.PrettyTime

class ChatRequestsAdapter : RecyclerView.Adapter<ChatRequestsAdapter.RequestsViewHolder>() {

    private var listener: OnButtonClickListener? = null
    private var context: Context? = null
    private var chatRequests = ArrayList<ChatRequest>()


    class RequestsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.tv_request_name)
        val date: TextView = view.findViewById(R.id.tv_request_date)
        val btnAccept: Button = view.findViewById(R.id.btn_accept_request)
        val btnIgnore: Button = view.findViewById(R.id.btn_ignore_request)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {

        context = parent.context
        return RequestsViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    viewType,
                    parent,
                    false
                )
        )
    }

    fun setData(arrayData: ArrayList<ChatRequest>) {
        chatRequests = arrayData
        notifyDataSetChanged()

        Log.d(TAG, "Set data for ChatRequestsAdapter: requests size =  $itemCount")
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        val chatRequest = chatRequests[position]


        if(chatRequest.requestType == RequestType.RECEIVED){
            holder.name.text = chatRequest.toUsername
            holder.date.text = chatRequest.createdAt.toString()

            holder.btnAccept.setOnClickListener {
                listener!!.onAcceptClicked(position)
            }

            holder.btnIgnore.setOnClickListener {
                listener!!.onIgnoreClicked(position)
            }
        }else if(chatRequest.requestType == RequestType.SENT){
            Log.d(TAG, "REQUEST $chatRequest")

            holder.name.text = chatRequest.toUsername
            holder.date.text = PrettyTime().format(chatRequest.createdAt)

            holder.btnAccept.visibility = View.INVISIBLE
            holder.btnAccept.isEnabled = false


            holder.btnIgnore.text = "Cancel Request"
            holder.btnIgnore.setOnClickListener {
                listener!!.onIgnoreClicked(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.request_item
    }

    override fun getItemCount(): Int {
        return chatRequests.size
    }

    fun setClickListener(clickListener: OnButtonClickListener) {
        this.listener = clickListener
    }

    interface OnButtonClickListener {
        fun onAcceptClicked(position: Int)

        fun onIgnoreClicked(position: Int)
    }

}