package com.nasahacker.steelmind.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasahacker.steelmind.adapter.HistoryAdapter.HistoryViewMolder
import com.nasahacker.steelmind.databinding.ItemHistoryBinding
import com.nasahacker.steelmind.R
import com.nasahacker.steelmind.util.MmkvManager

class HistoryAdapter(private val context: Context) : RecyclerView.Adapter<HistoryViewMolder>() {

    private var historyList = MmkvManager.getHistory()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewMolder {
        return HistoryViewMolder(
            LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: HistoryViewMolder,
        position: Int
    ) {

        val data = historyList[position]
        holder.binding.tvTitle.text = data.action
        holder.binding.tvSubtitle.text = data.remarks
        holder.binding.tvTimestamp.text = data.time
    }

    override fun getItemCount(): Int = historyList.size


    inner class HistoryViewMolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemHistoryBinding.bind(itemView)
    }


    fun refresh() {
        historyList = MmkvManager.getHistory()
        notifyDataSetChanged()
    }

}