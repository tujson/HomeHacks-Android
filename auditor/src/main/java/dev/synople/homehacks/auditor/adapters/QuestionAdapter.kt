package dev.synople.homehacks.auditor.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.auditor.fragments.SurveyFragment
import dev.synople.homehacks.common.models.Question
import dev.synople.homehacks.common.models.Response
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.screen_question.view.*
import org.w3c.dom.Comment

class QuestionAdapter(
    private val questions: List<Question>,
    private val responses: MutableList<Response>,
    private val onNext: () -> Unit,
    private val takePicture: () -> Unit
) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindQuestion(
            question: Question,
            response: Response?,
            onNext: () -> Unit,
            takePicture: () -> Unit
        ) {
            containerView.tvLocation.text = question.location.capitalize()
            containerView.tvQuestion.text = question.question

            containerView.btnYes.setOnClickListener {
                containerView.btnYes.tag = "true"
                onNext()
            }

            containerView.btnNo.setOnClickListener {
                containerView.btnYes.tag = "false"
                onNext()
            }

            containerView.tag = question.id

            containerView.btnAddImage.setOnClickListener {
                takePicture()
            }

            Log.v("QuestionAdapter", "Response: ${response.toString()}")

            response?.let {
                if (it.images.isNotEmpty()) {
                    containerView.rvImages.layoutManager = LinearLayoutManager(
                        containerView.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                    containerView.rvImages.adapter = SurveyImageAdapter(it.images) {}
                    containerView.rvImages.setHasFixedSize(true)
                    containerView.rvImages.isNestedScrollingEnabled = false
                }
            }
        }
    }

    public fun getAllResponses() = responses

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.screen_question, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindQuestion(questions[position], responses[position], onNext, takePicture)
    }

    override fun getItemCount() = questions.size
}