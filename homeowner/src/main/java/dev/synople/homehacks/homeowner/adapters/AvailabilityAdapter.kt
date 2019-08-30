package dev.synople.homehacks.homeowner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.common.auditLookahead
import dev.synople.homehacks.homeowner.R
import kotlinx.android.extensions.LayoutContainer
import java.util.*

class AvailabilityAdapter(
    private val availabilities: MutableList<Long>
) :
    RecyclerView.Adapter<AvailabilityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AvailabilityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.page_availability, parent, false)
        )

    override fun onBindViewHolder(holder: AvailabilityViewHolder, position: Int) {

        holder.bindAvailability(availabilities, position)
    }

    override fun getItemCount() = auditLookahead

    fun getAllItems() = availabilities
}

class AvailabilityViewHolder internal constructor(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bindAvailability(availabilities: MutableList<Long>, position: Int) {
        val currDate = Calendar.getInstance()
        currDate.add(Calendar.DAY_OF_WEEK, position)

        val rvAvailabilityDay = containerView.findViewById<RecyclerView>(R.id.rvAvailabilityDay)
        rvAvailabilityDay.adapter = AvailabilityDayAdapter(availabilities, currDate)

        rvAvailabilityDay.addItemDecoration(
            DividerItemDecoration(
                rvAvailabilityDay.context,
                LinearLayoutManager.VERTICAL
            )
        )
    }
}
