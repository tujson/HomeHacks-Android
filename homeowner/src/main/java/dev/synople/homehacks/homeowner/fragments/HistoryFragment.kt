package dev.synople.homehacks.homeowner.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import dev.synople.homehacks.common.models.Audit
import dev.synople.homehacks.homeowner.AppContext

import dev.synople.homehacks.homeowner.R
import dev.synople.homehacks.homeowner.adapters.AuditAdapter
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    private val audits = mutableListOf<Audit>()
    private lateinit var adapter: AuditAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_history, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Paginate this... Look into AAC - Paging
        adapter = AuditAdapter(audits) {
            Navigation.findNavController(view)
                .navigate(HistoryFragmentDirections.actionHistoryFragmentToViewAuditFragment(it))
        }
        rvHistory.adapter = adapter

        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener { retrieveAudits() }
        retrieveAudits()
    }

    private fun retrieveAudits() {
        FirebaseFirestore.getInstance().collection("audits").document(AppContext.user.id)
            .collection("audits").get()
            .addOnSuccessListener {
                audits.clear()
                for (document in it) {
                    audits.add(document.toObject(Audit::class.java))
                }
                adapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
            }
    }
}
