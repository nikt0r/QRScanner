package pl.polciuta.qrscanner.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_row.view.*
import pl.polciuta.qrscanner.R


class CardAdapter : ListAdapter<InfoRow, CardAdapter.ViewHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<InfoRow>() {
        override fun areItemsTheSame(oldItem: InfoRow, newItem: InfoRow) = oldItem.label == newItem.label
        override fun areContentsTheSame(oldItem: InfoRow, newItem: InfoRow) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_row, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(row: InfoRow) {
            itemView.label.text = row.label
            itemView.info.text = row.info
        }

    }

}
