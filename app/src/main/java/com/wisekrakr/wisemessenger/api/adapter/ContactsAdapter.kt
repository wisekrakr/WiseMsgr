package com.wisekrakr.wisemessenger.api.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.model.User
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.utils.Actions.ImageActions.loadImage
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {


    private lateinit var holder: ContactsViewHolder
    private var listener: OnItemClickListener? = null
    private var context: Context? = null
    private var contacts = listOf<UserProfile>()


    class ContactsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.tv_contact_name)
        val status: TextView = view.findViewById(R.id.tv_contact_status)
        val avatar: ImageView = view.findViewById(R.id.img_contact_avatar)
        val statusImageView: ImageView = view.findViewById(R.id.img_status)
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

    fun setData(arrayData: List<UserProfile>) {
        contacts = arrayData
        notifyDataSetChanged()

        Log.d(TAG, "Set data for ContactsAdapter: contacts size =  $itemCount")
    }


    @SuppressLint("ResourceAsColor")
    fun select() {
        holder.itemView.setBackgroundResource(R.color.primaryColor)
    }

    @SuppressLint("ResourceAsColor")
    fun deselect() {
        holder.itemView.setBackgroundResource(R.color.transparent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        this.holder = holder

        val userProfile: UserProfile = contacts[position]

        holder.name.text = userProfile.username

        if (userProfile.state.isNullOrEmpty())
            holder.status.text = "Offline"
        else {
            if (userProfile.state["state"] == "Online") {
                holder.status.text = "Online"
                holder.statusImageView.setImageResource(R.drawable.round_image_online)
            } else if (userProfile.state["state"] == "Offline") {
                holder.status.text = "Last seen: " + userProfile.state["time"].toString() + " " +
                        userProfile.state["date"].toString()
                holder.statusImageView.setImageResource(R.drawable.round_image_offline)
            }
        }

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