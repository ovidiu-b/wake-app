package com.weikappinc.weikapp.adapters.main_activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.data_view_models.MessageItemModel
import com.weikappinc.weikapp.databinding.MessageListItemBinding

class MessageListAdapter : RecyclerView.Adapter<MessageItemViewHolder>() {

    private var messages: MutableList<MessageItemModel>? = null

    fun updateListOfAlarms(messages: MutableList<MessageItemModel>) {
        this.messages = messages
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemViewHolder {
        val mBinding: MessageListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.message_list_item, parent, false)

        return MessageItemViewHolder(mBinding)
    }

    override fun getItemCount(): Int = messages?.size ?: 0

    override fun onBindViewHolder(holder: MessageItemViewHolder, position: Int) {
        holder.bind(messages!![position])
        holder.mBinding.executePendingBindings()
    }
}

class MessageItemViewHolder(val mBinding: MessageListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(message: MessageItemModel) { mBinding.message = message }
}
