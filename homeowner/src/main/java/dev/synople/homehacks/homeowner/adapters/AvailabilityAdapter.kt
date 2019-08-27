package dev.synople.homehacks.homeowner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.homeowner.R
import kotlinx.android.extensions.LayoutContainer

class AvailabilityAdapter(
    private val availabilities: MutableList<Long>,
    private val itemClick: (Long) -> Unit
) :
    RecyclerView.Adapter<AvailabilityAdapter.ViewHolder>() {
    class ViewHolder(override val containerView: View, private val itemClick: (Long) -> Unit) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindAvailability(availability: Long) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_availability, parent, false), itemClick
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAvailability(availabilities[position])
    }

    override fun getItemCount() = availabilities.size
}