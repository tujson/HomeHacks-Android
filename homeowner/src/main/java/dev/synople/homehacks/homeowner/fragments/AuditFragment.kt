package dev.synople.homehacks.homeowner.fragments


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.common.models.Audit
import dev.synople.homehacks.common.models.Question
import dev.synople.homehacks.homeowner.AppContext

import dev.synople.homehacks.homeowner.R
import kotlinx.android.synthetic.main.fragment_audit.*
import java.text.DateFormat
import java.util.*

class AuditFragment : Fragment() {

    private val date = Calendar.getInstance()
    private val startTime = Calendar.getInstance()
    private val endTime = Calendar.getInstance()

    private val dateFormatter = DateFormat.getDateInstance(DateFormat.LONG)
    private val timeFormatter = DateFormat.getTimeInstance(DateFormat.LONG)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_audit, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            fetchPendingAudit()
        }
        fetchPendingAudit()

        // Setting up scheduler
        dateFormatter.timeZone = date.timeZone
        timeFormatter.timeZone = startTime.timeZone

        btnDate.setOnClickListener {
            DatePickerDialog(
                context!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    date.set(Calendar.YEAR, year)
                    date.set(Calendar.MONTH, month)
                    date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    btnDate.text = dateFormatter.format(date.time)
                },
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnStartTime.setOnClickListener {
            TimePickerDialog(
                context!!,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    startTime.set(Calendar.MINUTE, minute)

                    btnStartTime.text = timeFormatter.format(startTime.time)
                },
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE),
                false
            ).show()
        }

        btnEndTime.setOnClickListener {
            TimePickerDialog(
                context!!,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    endTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    endTime.set(Calendar.MINUTE, minute)

                    btnEndTime.text = timeFormatter.format(endTime.time)
                },
                endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE),
                false
            ).show()
        }

        btnContinue.setOnClickListener {
            startTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
            endTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))

            val questions = mutableListOf<Question>()
            val question = Question("Kitchen", "Is there a fire extinguisher?", "", "", mutableListOf())
            questions.add(question)

            val audit = Audit(
                UUID.randomUUID().toString(),
                "",
                "",
                AppContext.user.id,
                AppContext.user.name,
                AppContext.user.address,
                startTime.timeInMillis,
                endTime.timeInMillis,
                questions,
                0, 0
            )

            FirebaseFirestore.getInstance()
                .collection("pendingAudits")
                .document(AppContext.user.id)
                .set(audit)
                .addOnSuccessListener {
                    tvStatus.text = "You have an audit pending for \n" + dateFormatter.format(startTime.time)
                    disabled.visibility = View.VISIBLE
                }
        }
    }

    private fun fetchPendingAudit() {
        FirebaseFirestore.getInstance().collection("pendingAudits").document(AppContext.user.id)
            .get().addOnSuccessListener {
                it.toObject(Audit::class.java)?.let { audit ->
                    if (audit.auditorId.isEmpty()) {
                        startTime.timeInMillis = audit.startTime
                        endTime.timeInMillis = audit.endTime

                        tvStatus.text = "You have an audit pending for \n" + dateFormatter.format(startTime.time)
                    } else {
                        val scheduledTime = Calendar.getInstance()
                        scheduledTime.timeInMillis = audit.scheduledTime
                        tvStatus.text = "You have an audit scheduled for \n" + dateFormatter.format(scheduledTime.time)
                    }
                    disabled.visibility = View.VISIBLE
                } ?: run {
                    tvStatus.text = "You have no audits scheduled."
                    disabled.visibility = View.GONE
                }

                swipeRefresh.isRefreshing = false
            }
    }
}
