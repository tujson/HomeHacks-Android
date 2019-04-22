package dev.synople.homehacks.auditor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.extensions.LayoutContainer

class AuditAdapter(private val audits: MutableList<Audit>, private val itemClick: (Audit) -> Unit) :
    RecyclerView.Adapter<AuditAdapter.ViewHolder>() {
    class ViewHolder(override val containerView: View, private val itemClick: (Audit) -> Unit) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindAudit(audit: Audit) {
            containerView.findViewById<TextView>(R.id.tvHomeownerName).text = audit.homeownerName
            containerView.findViewById<TextView>(R.id.tvAddress).text = audit.address
            containerView.findViewById<TextView>(R.id.tvDate).text = audit.homeownerName

            containerView.setOnClickListener { itemClick(audit) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_audit, parent, false), itemClick
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindAudit(audits[position])
    }

    override fun getItemCount() = audits.size
}