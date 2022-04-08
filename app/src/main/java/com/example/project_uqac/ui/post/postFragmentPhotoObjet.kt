package com.example.project_uqac.ui.post

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class PostFragmentPhotoObjet : Fragment(){

    private lateinit var imageView: ImageView
    private var cropImg  : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_photo_objet, container, false)


        var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        progressBar.progress = 68

        val args = Bundle()

        val textModel = requireArguments().getString("model")
        val textMarque = requireArguments().getString("marque")
        val textDescription = requireArguments().getString("description")


        imageView = view.findViewById(R.id.image_objet)

        val buttonPhoto : ImageButton = view.findViewById(R.id.button_photo_objet)
        buttonPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val buttonPrev : ImageButton = view.findViewById(R.id.imageButtonprevPhotoObjet)
        buttonPrev.setOnClickListener(){
            val fragment = PostFragmentDescriptionObjet()
            args.putString("model", textModel.toString())
            args.putString("marque", textMarque.toString())
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        val buttonNext : ImageButton = view.findViewById(R.id.imageButtonnextPhotoObjet)
        buttonNext.setOnClickListener(){
            val fragment = PostFragmentDateObjet()
            args.putString("model", textModel.toString())
            args.putString("marque", textMarque.toString())
            args.putString("description", textDescription.toString())
            args.putParcelable("image", cropImg)
            fragment.arguments = args
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.post_fragment_navigation, fragment)?.commit()
        }

        return view
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d("TAG", "I was here")
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Crop the image to have a square
            val width: Int = imageBitmap.width
            val height: Int = imageBitmap.height
            val newWidth = if (height > width) width else height
            val newHeight = if (height > width) height - (height - width) else height
            var cropW = (width - height) / 2
            cropW = if (cropW < 0) 0 else cropW
            var cropH = (height - width) / 2
            cropH = if (cropH < 0) 0 else cropH
            cropImg = Bitmap.createBitmap(imageBitmap, cropW, cropH, newWidth, newHeight)
            imageView.setImageBitmap(cropImg)
        }
    }



}