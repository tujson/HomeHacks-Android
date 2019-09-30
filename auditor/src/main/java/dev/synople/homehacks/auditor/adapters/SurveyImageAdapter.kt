package dev.synople.homehacks.auditor.adapters

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.auditor.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_image.view.*

class SurveyImageAdapter(
    private val images: MutableList<Bitmap>,

    private val onClick: () -> Unit
) : RecyclerView.Adapter<SurveyImageAdapter.ViewHolder>() {

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindImage(bitmap: Bitmap, onClick: () -> Unit) {

            containerView.ivImage.setImageDrawable(BitmapDrawable(containerView.context.resources, bitmap))

            containerView.setOnClickListener { onClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_image, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindImage(images[position], onClick)
    }

    override fun getItemCount() = images.size
}