package com.wisekrakr.wisemessenger.api.adapter.utils

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ChatViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, context: Context?)
}