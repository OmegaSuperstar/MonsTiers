package com.example.monstiers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MailAdapter(private val mailList: List<Mail>) : RecyclerView.Adapter<MailAdapter.MailViewHolder>() {

    class MailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textFrom: TextView = itemView.findViewById(R.id.textFrom)
        val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        val textTimestamp: TextView = itemView.findViewById(R.id.textTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mail, parent, false)
        return MailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        val mail = mailList[position]
        holder.textFrom.text = "From: ${mail.from}"
        holder.textMessage.text = mail.message
        holder.textTimestamp.text = java.text.SimpleDateFormat("dd MMM yyyy HH:mm").format(mail.timestamp)
    }

    override fun getItemCount(): Int = mailList.size
}
