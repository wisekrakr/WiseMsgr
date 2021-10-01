package com.wisekrakr.wisemessenger.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

class ContactsSearchAdapter(options: FirebaseRecyclerOptions<User>) : FirebaseRecyclerAdapter<User, ContactsAdapter.ContactsViewHolder>(
    options)  {

    private var listener: OnItemClickListener? = null
    private var context: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ContactsAdapter.ContactsViewHolder {

        context = parent.context
        return ContactsAdapter.ContactsViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    viewType,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(
        holder: ContactsAdapter.ContactsViewHolder,
        position: Int,
        model: User,
    ) {
        holder.name.text = model.username
        holder.status.text = model.status
        Actions.ImageActions.loadImage(model.avatarUrl, holder.avatar)

        Log.d(TAG, " Contact shown: " + model.username)

        holder.itemView.rootView.setOnClickListener {
            listener!!.onClick(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.contact_item
    }


    fun setClickListener(clickListener: OnItemClickListener) {
        this.listener = clickListener
    }

    interface OnItemClickListener {
        fun onClick(contact: User)
    }

}