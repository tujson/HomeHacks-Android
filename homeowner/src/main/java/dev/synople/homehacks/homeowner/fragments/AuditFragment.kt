package dev.synople.homehacks.homeowner.fragments


import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import dev.synople.homehacks.common.SURVEY_VERSION
import dev.synople.homehacks.common.AUDIT_TIME_LENGTH
import dev.synople.homehacks.common.models.Audit
import dev.synople.homehacks.homeowner.AppContext
import dev.synople.homehacks.homeowner.R
import dev.synople.homehacks.homeowner.adapters.AvailabilityAdapter
import kotlinx.android.synthetic.main.fragment_audit.*
import java.text.SimpleDateFormat
import java.util.*

class AuditFragment : Fragment() {

    private var audit: Audit? = null
    private val date = Calendar.getInstance()

    private val tabDateFormat = SimpleDateFormat("EEE MMMdd", Locale.US)

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
        tabDateFormat.timeZone = date.timeZone

        btnCalendar.setOnClickListener {
            audit?.let { audit ->
                createCalendarEvent(audit)
            }
        }

        btnContinue.setOnClickListener {
            val audit = Audit(
                UUID.randomUUID().toString(),
                "",
                "",
                AppContext.user.id,
                AppContext.user.name,
                AppContext.user.address,
                SURVEY_VERSION,
                mutableListOf(),
                (vpAvailabilities.adapter as AvailabilityAdapter).getAllItems()
            )

            FirebaseFirestore.getInstance()
                .collection("pendingAudits")
                .document(AppContext.user.id)
                .set(audit)
                .addOnSuccessListener {
                    tvStatus.text = "You have an audit pending assignment"
                }
        }
    }

    private fun createCalendarEvent(audit: Audit) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, audit.scheduledTime)
            .putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                audit.scheduledTime + (AUDIT_TIME_LENGTH * 60000)
            )
            .putExtra(
                CalendarContract.Events.TITLE,
                "[HomeHacks] Audit from ${audit.auditorName}"
            )
            .putExtra(
                CalendarContract.Events.DESCRIPTION,
                "Audit from ${audit.auditorName}.\nFrom HomeHacks app."
            )
            .putExtra(CalendarContract.Events.EVENT_LOCATION, audit.address)
            .putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )

        startActivity(intent)
    }

    private fun fetchPendingAudit() {
        FirebaseFirestore.getInstance()
            .collection("pendingAudits")
            .document(AppContext.user.id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.toObject(Audit::class.java)?.let { audit ->
                    this.audit = audit

                    if (audit.auditorId.isEmpty()) {
                        tvStatus.text = "You have an audit pending assignment"
                        btnContinue.text = "Save Changes"
                        setupScheduler()
                    } else {
                        val statusDateFormat = SimpleDateFormat("EEEE MMM dd h:mm a", Locale.US)

                        tvStatus.text =
                            "You have an audit scheduled for:\n" + statusDateFormat.format(audit.scheduledTime)

                        swipeRefresh.layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0,
                                4.0f
                            )
                        clContent.visibility = View.GONE

                        btnCalendar.visibility = View.VISIBLE
                        tvAuditorName.text = audit.auditorName
                        tvAuditorName.visibility = View.VISIBLE
                        ivAuditor.visibility = View.VISIBLE

                        FirebaseStorage.getInstance()
                            .getReference("profilePictures")
                            .child(audit.auditorId)
                            .downloadUrl
                            .addOnSuccessListener {
                                Picasso.get()
                                    .load(it)
                                    .into(ivAuditor)
                            }.addOnFailureListener {
                                Log.e(
                                    "AuditFragment",
                                    "FirebaseStorage retrieving auditor profile picture",
                                    it
                                )
                            }
                    }
                } ?: run {
                    audit = null
                    tvStatus.text = "Schedule an audit"
                    swipeRefresh.layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0,
                            1.0f
                        )
                    clContent.visibility = View.VISIBLE
                    tvAuditorName.visibility = View.GONE
                    btnCalendar.visibility = View.GONE
                    ivAuditor.visibility = View.GONE
                    setupScheduler()
                }

                swipeRefresh.isRefreshing = false
            }
    }

    private fun setupScheduler() {
        vpAvailabilities.adapter =
            AvailabilityAdapter(audit?.availabilities
                ?: run {
                    mutableListOf<Long>()
                })
        TabLayoutMediator(tabs, vpAvailabilities) { tab, position ->
            val currDate = Calendar.getInstance()
            currDate.add(Calendar.DATE, position)
            tab.text = tabDateFormat.format(currDate.time)
        }.attach()
    }
}
