package com.example.chatme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.date_header.view.*
import kotlinx.android.synthetic.main.list_item_chat_sent_message.view.*
import kotlinx.android.synthetic.main.list_item_chat_sent_message.view.content


class ChatAdapter (private val list: MutableList<ChatEvent>, private val mCurrentId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //var highFiveClick:((id:String, status:Boolean)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflate={layout:Int ->
            LayoutInflater.from(parent.context).inflate(layout,parent,false)
        }

        return when(viewType){
            TEXT_MESSAGE_RECIEVED ->{
                MessageViewHolder(inflate(R.layout.list_item_chat_recieve_message))
            }
            TEXT_MESSAGE_SENT ->{
                MessageViewHolder(inflate(R.layout.list_item_chat_sent_message))
            }
            DATE_HEADER ->{
                DateViewHolder(inflate(R.layout.date_header))
            }
            else ->MessageViewHolder(inflate(R.layout.list_item_chat_recieve_message))

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = list[position]){
            is DateHeader ->{
                holder.itemView.date_header.text = item.date
            }
            is Message ->{
                holder.itemView.apply {
                    content.text = item.msg
                    time.text = item.sentAt.formatAsTime()
                    
            }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when ( val event =list[position]){
            is Message ->{
                if (event.senderId ==mCurrentId){
                    TEXT_MESSAGE_SENT
                }else{
                    TEXT_MESSAGE_RECIEVED
                }
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    class DateViewHolder(view : View) : RecyclerView.ViewHolder(view)

    class MessageViewHolder(view : View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECIEVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }

}