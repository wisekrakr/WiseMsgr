package com.wisekrakr.wisemessenger.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.utils.Actions.ImageActions.loadImage
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var context: Context? = null
    private var contacts = ArrayList<UserProfile>()


    class ContactsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.tv_contact_name_private)
        val status: TextView = view.findViewById(R.id.tv_message_private)
        val avatar: ImageView = view.findViewById(R.id.img_contact_avatar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {

        context = parent.context
        return ContactsViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    viewType,
                    parent,
                    false
                )
        )
    }

    fun setData(arrayData: ArrayList<UserProfile>) {
        contacts = arrayData
        notifyDataSetChanged()

        Log.d(TAG, "Set data for ContactsAdapter: contacts size =  $itemCount")
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {

        holder.name.text = contacts[position].username
        holder.status.text = contacts[position].status
        loadImage(contacts[position].avatarUrl, holder.avatar)

        holder.itemView.rootView.setOnClickListener {
            listener!!.onClick(contacts[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.contact_item
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun setClickListener(clickListener: OnItemClickListener) {
        this.listener = clickListener
    }

    interface OnItemClickListener {
        fun onClick(contact: UserProfile)
    }

}