package dev.synople.homehacks.homeowner.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.common.models.Audit
import dev.synople.homehacks.common.models.Question
import dev.synople.homehacks.homeowner.AppContext

import dev.synople.homehacks.homeowner.R
import dev.synople.homehacks.homeowner.adapters.AvailabilityAdapter
import kotlinx.android.synthetic.main.fragment_audit.*
import java.text.SimpleDateFormat
import java.util.*

class AuditFragment : Fragment() {

    private var audit: Audit? = null
    private val date = Calendar.getInstance()
    private val startTime = Calendar.getInstance()
    private val endTime = Calendar.getInstance()

    private val dateFormat = SimpleDateFormat("EEE MMMdd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_audit, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            fetchPendingAudit()
        }
        fetchPendingAudit()

        // Setting up scheduler
        dateFormat.timeZone = date.timeZone

        vPAvailabilities.adapter =
            AvailabilityAdapter(audit?.availabilities ?: run { mutableListOf<Long>() })
        TabLayoutMediator(tabs, vPAvailabilities) { tab, position ->
            val currDate = Calendar.getInstance()
            currDate.add(Calendar.DATE, position)
            tab.text = dateFormat.format(currDate.time)
        }.attach()


        btnContinue.setOnClickListener {
            startTime.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            )
            endTime.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            )

            val questions = mutableListOf<Question>()
            val question =
                Question("Kitchen", "Is there a fire extinguisher?", "", "", mutableListOf())
            questions.add(question)

            val audit = Audit(
                UUID.randomUUID().toString(),
                "",
                "",
                AppContext.user.id,
                AppContext.user.name,
                AppContext.user.address,
                questions,
                mutableListOf(),
                0, 0
            )

            FirebaseFirestore.getInstance()
                .collection("pendingAudits")
                .document(AppContext.user.id)
                .set(audit)
                .addOnSuccessListener {
                    tvStatus.text = "You have an audit pending assignment"
                    disabled.visibility = View.VISIBLE
                }
        }
    }

    private fun fetchPendingAudit() {
        FirebaseFirestore.getInstance().collection("pendingAudits").document(AppContext.user.id)
            .get().addOnSuccessListener {
                it.toObject(Audit::class.java)?.let { audit ->
                    this.audit = audit

                    if (audit.auditorId.isEmpty()) {
                        tvStatus.text = "You have an audit pending assignment"
                    } else {
//                        tvStatus.text =
//                            "You have an audit scheduled for \n" + dateFormatter.format(audit.scheduledTime)
                    }
                    disabled.visibility = View.VISIBLE
                } ?: run {
                    audit = null
                    tvStatus.text = "Schedule an audit"
                    disabled.visibility = View.GONE
                }

                swipeRefresh.isRefreshing = false
            }
    }
}
