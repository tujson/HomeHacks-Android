package dev.synople.homehacks.homeowner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.common.auditEndTime
import dev.synople.homehacks.common.auditStartTime
import dev.synople.homehacks.common.auditTimeLength
import dev.synople.homehacks.homeowner.R
import kotlinx.android.extensions.LayoutContainer
import java.text.SimpleDateFormat
import java.util.*

class AvailabilityDayAdapter(
    private val availabilities: MutableList<Long>,
    private val currDate: Calendar
) :
    RecyclerView.Adapter<AvailabilityDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AvailabilityDayViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.availability_day, parent, false)
        )

    override fun onBindViewHolder(holder: AvailabilityDayViewHolder, position: Int) {

        holder.bindAvailability(availabilities, position, currDate)
    }

    override fun getItemCount() = (auditEndTime - auditStartTime) * (60 / auditTimeLength) + 1
}

class AvailabilityDayViewHolder internal constructor(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val dateFormat = SimpleDateFormat("h:mm a", Locale.US)

    fun bindAvailability(availabilities: MutableList<Long>, position: Int, currDate: Calendar) {
        val startTime = currDate.clone() as Calendar
        startTime.set(Calendar.HOUR_OF_DAY, auditStartTime)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.MILLISECOND, 0)
        startTime.add(Calendar.MINUTE, position * auditTimeLength)
        containerView.findViewById<TextView>(R.id.tvDay).text = dateFormat.format(startTime.time)

        if (availabilities.contains(startTime.timeInMillis)) {
            containerView.setBackgroundColor(ContextCompat.getColor(containerView.context, R.color.mintGreen))
        } else {
            containerView.setBackgroundColor(ContextCompat.getColor(containerView.context, R.color.white))
        }

        containerView.setOnClickListener {
            if (availabilities.contains(startTime.timeInMillis)) {
                availabilities.remove(startTime.timeInMillis)
                containerView.setBackgroundColor(ContextCompat.getColor(containerView.context, R.color.white))
            } else {
                availabilities.add(startTime.timeInMillis)
                containerView.setBackgroundColor(ContextCompat.getColor(containerView.context, R.color.mintGreen))
            }
        }
    }
}
