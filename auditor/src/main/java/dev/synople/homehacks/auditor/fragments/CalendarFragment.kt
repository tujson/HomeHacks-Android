package dev.synople.homehacks.auditor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dev.synople.homehacks.auditor.AppContext
import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.auditor.adapters.AuditAdapter
import kotlinx.android.synthetic.main.fragment_calendar.*

class CalendarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_calendar, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when {
            AppContext.user.scheduledAudits.size == 0 -> tvStatus.text =
                "You have no audits scheduled."
            AppContext.user.scheduledAudits.size == 1 -> tvStatus.text =
                "Hi, ${AppContext.user.name}!\nYou have ${AppContext.user.scheduledAudits.size} audit scheduled."
            else -> tvStatus.text =
                "Hi, ${AppContext.user.name}!\nYou have ${AppContext.user.scheduledAudits.size} audits scheduled."
        }

        AppContext.user.scheduledAudits =
            AppContext.user.scheduledAudits.sortedBy { it.scheduledTime }.toMutableList()

        val adapter = AuditAdapter(AppContext.user.scheduledAudits) {
            Navigation.findNavController(view).navigate(
                CalendarFragmentDirections.actionCalendarFragmentToViewAuditFragment(it)
            )
        }
        rvAudits.adapter = adapter

        fab.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_calendarFragment_to_scheduleAuditFragment)
        }
    }
}
