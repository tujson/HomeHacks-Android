package dev.synople.homehacks.auditor.adapters

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.synople.homehacks.auditor.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_image.view.*

class SurveyImageAdapter(
    private val images: MutableList<Uri>,
    private val onClick: () -> Unit
) : RecyclerView.Adapter<SurveyImageAdapter.ViewHolder>() {

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindImage(photoFilePath: Uri, onClick: () -> Unit) {

            // Deprecated, but alternative has minSdkVersion 28
            containerView.ivImage.setImageBitmap(
                MediaStore.Images.Media.getBitmap(
                    containerView.context.contentResolver,
                    photoFilePath
                )
            )

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