package dev.synople.homehacks.auditor.fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.auditor.AppContext

import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.auditor.adapters.AuditAdapter
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.synthetic.main.fragment_schedule_audit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class ScheduleAuditFragment : Fragment(), CoroutineScope {

    private val availableAudits = mutableListOf<Audit>()
    private lateinit var adapter: AuditAdapter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_schedule_audit, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AuditAdapter(availableAudits) { audit ->
            AlertDialog.Builder(context!!).setMessage("Are you sure?")
                .setPositiveButton("Yes") { _, _ ->
                    audit.auditorId = AppContext.user.id
                    audit.auditorName = AppContext.user.name
                    AppContext.user.scheduledAudits.add(audit)

                    launch {
                        val pendingAudit = async {
                            FirebaseFirestore.getInstance()
                                .collection("pendingAudits")
                                .document(audit.homeownerId)
                                .update(
                                    "auditorId",
                                    AppContext.user.id,
                                    "auditorName",
                                    AppContext.user.name
                                )
                        }

                        val auditorsUpdate = async {
                            FirebaseFirestore.getInstance()
                                .collection("auditors")
                                .document(AppContext.user.id)
                                .set(AppContext.user)
                        }

                        pendingAudit.await()
                        auditorsUpdate.await()

                        Navigation.findNavController(view)
                            .navigate(R.id.action_scheduleAuditFragment_to_calendarFragment)
                    }
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
        rvAudits.adapter = adapter

        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            retrieveAvailableAudits()
        }
        retrieveAvailableAudits()

        tvLbl.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_scheduleAuditFragment_to_calendarFragment)
        }
    }

    private fun retrieveAvailableAudits() {
        FirebaseFirestore.getInstance().collection("pendingAudits").get()
            .addOnSuccessListener {
                availableAudits.clear()
                for (document in it) {
                    val audit = document.toObject(Audit::class.java)
                    if (audit.auditorId.isEmpty()) {
                        availableAudits.add(audit)
                    }
                }
                adapter.notifyDataSetChanged()

                swipeRefresh.isRefreshing = false
            }
    }
}
