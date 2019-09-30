package dev.synople.homehacks.auditor.fragments


import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import dev.synople.homehacks.auditor.R
import dev.synople.homehacks.common.models.Audit
import kotlinx.android.synthetic.main.fragment_survey.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.coroutines.CoroutineContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.synople.homehacks.auditor.adapters.QuestionAdapter
import dev.synople.homehacks.common.models.Question
import dev.synople.homehacks.common.models.Response
import kotlinx.android.synthetic.main.screen_question.view.*
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SurveyFragment : Fragment(), CoroutineScope {

    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoUri: Uri = Uri.EMPTY

    private val args: SurveyFragmentArgs by navArgs()
    private lateinit var audit: Audit
    private lateinit var questions: List<Question>
    private val responses: MutableList<Response> by lazy {
        MutableList(questions.size) { Response() }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_survey, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audit = args.audit

        loadQuestions()

        ivLeft.setOnClickListener {
            vpQuestions.currentItem = vpQuestions.currentItem - 1
        }
        ivRight.setOnClickListener {
            vpQuestions.currentItem = vpQuestions.currentItem + 1
        }

        vpQuestions.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                tvProgress.text = "${position + 1}/${questions.size}"
                progressBar.progress = position + 1
            }
        })

        btnFinish.setOnClickListener {
            // TODO

            Navigation.findNavController(view)
                .navigate(SurveyFragmentDirections.actionSurveyFragmentToViewAuditFragment(audit))
        }
    }

    private fun loadQuestions() {
        launch {
            val tempFile = File.createTempFile("questions", "json")
            Log.v("SurveyFragment", "surveys/${audit.surveyVersion}.json")
            FirebaseStorage.getInstance().getReference("surveys/${audit.surveyVersion}.json")
                .getFile(tempFile).await()

            val text = tempFile.readText()
            val listType = object : TypeToken<ArrayList<Question>>() {

            }.type
            questions = Gson().fromJson(text, listType) as List<Question>

            withContext(Dispatchers.Main) {
                vpQuestions.adapter =
                    QuestionAdapter(questions, responses, {
                        vpQuestions.currentItem = vpQuestions.currentItem + 1
                    }, {
                        takePicture()
                    })

                progressBar.max = questions.size
            }
        }
    }

    private fun takePicture() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(this.activity!!.packageManager)
                    ?.also {
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (e: IOException) {
                            Log.e("SurveyFragment", "Creating image file", e)
                            null
                        }

                        photoFile?.also {
                            currentPhotoUri = FileProvider.getUriForFile(
                                context!!,
                                "dev.synople.homehacks.auditor.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                            startActivityForResult(
                                takePictureIntent,
                                REQUEST_IMAGE_CAPTURE
                            )
                        }
                    }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val fileName = UUID.randomUUID().toString()
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            fileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            responses[vpQuestions.currentItem].images.add(currentPhotoUri)

            vpQuestions.adapter?.notifyItemChanged(vpQuestions.currentItem)
        }
    }
}
