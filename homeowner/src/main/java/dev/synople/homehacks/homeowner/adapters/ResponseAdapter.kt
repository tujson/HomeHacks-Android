package dev.synople.homehacks.homeowner.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.common.models.Question
import dev.synople.homehacks.common.models.Response
import dev.synople.homehacks.homeowner.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_response.view.*

class ResponseAdapter(
    private val questions: MutableList<Question>,
    private val responses: List<Response>,
    private val itemClick: () -> Unit
) : RecyclerView.Adapter<ResponseAdapter.ViewHolder>() {

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindResponse(
            question: Question,
            response: Response,
            itemClick: () -> Unit
        ) {
            containerView.tvQuestion.text = question.question
            containerView.tvComments.text = response.comments

            if (response.response.isEmpty()) {
                containerView.tvResponse.visibility = View.GONE
            } else {
                containerView.tvResponse.text = response.response.substring(0, 1).toUpperCase() +
                        response.response.substring(1).toLowerCase()
                containerView.tvResponse.visibility = View.VISIBLE
            }

            containerView.setOnClickListener {
                containerView.gDetails.visibility =
                    if (containerView.gDetails.visibility == View.GONE) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_response,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindResponse(questions[position], responses[position], itemClick)
    }

    override fun getItemCount() = questions.size
}