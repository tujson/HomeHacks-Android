package dev.synople.homehacks.homeowner.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
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

        btnContinue.setOnClickListener {
            val audit = Audit(
                UUID.randomUUID().toString(),
                "",
                "",
                AppContext.user.id,
                AppContext.user.name,
                AppContext.user.address,
                "v1",
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

    private fun fetchPendingAudit() {
        FirebaseFirestore.getInstance()
            .collection("pendingAudits")
            .document(AppContext.user.id)
            .get()
            .addOnSuccessListener {
                it.toObject(Audit::class.java)?.let { audit ->
                    this.audit = audit

                    if (audit.auditorId.isEmpty()) {
                        tvStatus.text = "You have an audit pending assignment"
                        disabled.visibility = View.GONE
                        btnContinue.text = "Save Changes"
                        setupScheduler()
                    } else {
                        val statusDateFormat = SimpleDateFormat("EEEE MMM dd h:mm a", Locale.US)

                        tvStatus.text =
                            "You have an audit scheduled for \n" + statusDateFormat.format(audit.scheduledTime)
                        disabled.visibility = View.VISIBLE
                    }
                } ?: run {
                    audit = null
                    tvStatus.text = "Schedule an audit"
                    disabled.visibility = View.GONE
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
