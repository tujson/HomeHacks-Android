package dev.synople.homehacks.homeowner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.common.models.Audit
import dev.synople.homehacks.homeowner.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_audit.view.*

class AuditAdapter(private val audits: MutableList<Audit>, private val itemClick: (Audit) -> Unit) :
    RecyclerView.Adapter<AuditAdapter.ViewHolder>() {
    class ViewHolder(override val containerView: View, private val itemClick: (Audit) -> Unit) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindAudit(audit: Audit) {

            containerView.tvAuditorName.text = audit.auditorName

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