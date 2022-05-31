package com.the.firsttask.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.karebo.teamapp.R
import com.karebo.teamapp.dataclass.questionDataModel


class QuestionAdapter(
    private val mList: List<questionDataModel>,
    var mCtx: Context,
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

     var switchStatus  = MutableLiveData<String>().apply { postValue("false")}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        switchStatus.value="false"
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_question, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.question_switch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            mList[viewHolder.adapterPosition].value=b
            checkAllSwitch()

        }
        return viewHolder
    }

    private fun checkAllSwitch() {
        var status=true
        mList.forEach { item->
            if(!item.value){
                status=false
            }
        }
        mList.forEach { item->
            Log.e("TAG", "checkAllSwitch: "+item.questions+":"+item.value, )
        }
        if(!status)
        {
            switchStatus.value="false"
        }
        else{
            switchStatus.value="true"
        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        if(position>=mList.size-2){
            holder.question.text = "Toolbox Talk \n"+item.questions.toString()
        }
        else    {
            holder.question.text = item.questions.toString()
        }

        holder.question_switch.isSelected=false

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var question: TextView = itemView.findViewById(R.id.tv_question)
        var question_switch: Switch = itemView.findViewById(R.id.sw_question)

    }
}