package com.example.project_uqac.ui.my_account

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.project_uqac.R
import com.example.project_uqac.ui.chat.ChatFragment
import com.example.project_uqac.ui.chat.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_my_account_sign_up.*
import kotlinx.android.synthetic.main.fragment_post_lieu_animal.*
import java.io.ByteArrayOutputStream


class MyAccountRegister : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private var imageSet: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_account_sign_up, container, false)
        // Initialize Firebase Auth
        auth = Firebase.auth

        val buttonObject2 : Button = view.findViewById(R.id.sign_up)
        buttonObject2.setOnClickListener {
            signUpUser()
        }
        val cardViewImageView : CardView = view.findViewById(R.id.my_account_cardView_imageView)
        imageView = view.findViewById(R.id.my_account_imageView)
//        val buttonTakePicture : Button = view.findViewById(R.id.my_account_take_pic)
        cardViewImageView.setOnClickListener {
            dispatchTakePictureIntent()
        }



        return view
    }

    private fun signUpUser() {

        if (sign_up_username.text.toString().isEmpty()) {
            sign_up_username.error = "Veuillez entrer un nom d' utilisateur"
            sign_up_username.requestFocus()
            return
        }

        if (sign_up_email.text.toString().isEmpty()) {
            sign_up_email.error = "Veuillez entrer une adresse courriel"
            sign_up_email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(sign_up_email.text.toString()).matches()) {
            sign_up_email.error = "Veuillez entrer une adresse courriel valide"
            sign_up_email.requestFocus()
            return
        }

        if (sign_up_password.text.toString().isEmpty()) {
            sign_up_password.error = "Veuillez entrer un mot de passe"
            sign_up_password.requestFocus()
            return
        }

//        if (!this::imageView.isInitialized){
//            sign_up_password.requestFocus()
//            return
//        }

        auth.createUserWithEmailAndPassword(sign_up_email.text.toString(), sign_up_password.text.toString()).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                Toast.makeText(context, "Account Created !",
                    Toast.LENGTH_SHORT).show()
                Log.d(TAG, auth.currentUser.toString())

                // Set img uri
//                var myUri : Uri = Uri.parse("android.resource://com.example.project_uqac/drawable/ic_action_my_account")
//                var myUri : Uri = Uri.parse("android.resource://com.example.project_uqac/drawable/ic_action_my_account")
                if(imageSet){
                    var myUri = context?.let { getImageUri(it, imageView.drawable.toBitmap()) }!!
                    // Upload profil pic
                    val storageReferenceu = FirebaseStorage.getInstance().getReference("profil_pics/" + sign_up_email.text.toString())
                    storageReferenceu.putFile(myUri).addOnSuccessListener {
                        storageReferenceu.downloadUrl.addOnSuccessListener {
                            // Set username and profil pic
                            val profileUpdates = userProfileChangeRequest {
                                displayName = sign_up_username.text.toString()
                                photoUri = it
                            }
                            val user = Firebase.auth.currentUser
                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { innerTask ->
                                    if (innerTask.isSuccessful) {
                                        val fragment = MyAccountLogged()
                                        val transaction = fragmentManager?.beginTransaction()
                                        transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
                                    } else {
                                        Log.d(TAG, "Error with username.")
                                    }
                                }
                        }
                    }
                } else {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = sign_up_username.text.toString()
                        photoUri = null
                    }
                    val user = Firebase.auth.currentUser
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { innerTask ->
                            if (innerTask.isSuccessful) {
                                val fragment = MyAccountLogged()
                                val transaction = fragmentManager?.beginTransaction()
                                transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
                            } else {
                                Log.d(TAG, "Error with username.")
                            }
                        }
                }



            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(context, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()

            }
        }
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
        Log.d("TAG", "azseghjk")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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
            val cropImg: Bitmap = Bitmap.createBitmap(imageBitmap, cropW, cropH, newWidth, newHeight)

            imageView.setImageBitmap(cropImg)
            imageSet = true
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun onImageSelected(uri: Uri) {
        val db = Firebase.database
//        Log.d("MainActivity", "Uri: $uri")
        val user = auth.currentUser
        val tempMessage = Message(null, getUserName(), getPhotoUrl(), "https://www.google.com/images/spin-32.gif")
        db.reference
            .child("profil_picture")
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.w(
                            "MainActivity", "Unable to write message to database.",
                            databaseError.toException()
                        )
                        return@CompletionListener
                    }

                    // Build a StorageReference and then upload the file
                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference("profil_pic")
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putImageInStorage(storageReference, uri, key)
                })
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ChatFragment.ANONYMOUS
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        val storageReferenceu = FirebaseStorage.getInstance().getReference("profil_pics/myImage")
        storageReferenceu.putFile(uri).addOnSuccessListener { taskSnapshot ->
            val snap = taskSnapshot
            val url = taskSnapshot.metadata?.reference?.downloadUrl
//            Log.d("Lets gowwww", storageReferenceu.downloadUrl.toString())
            storageReferenceu.downloadUrl.addOnSuccessListener {
                Log.d("Lets gowwww", it.toString())
                Firebase.auth.currentUser!!.updateProfile(userProfileChangeRequest { photoUri = it }).addOnSuccessListener {
                    val fragment = MyAccountLogged()
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
                }
            }
        }
//            storageReferenceu.downloadUrl.addOnCompleteListener()
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
    }



//        Log.d("MainActivity", "Uri: $uri key: $key")
//        val db = Firebase.database
//        // First upload the image to Cloud Storage
//        activity?.let {
//            storageReference.putFile(uri)
//                .addOnSuccessListener(
//                    it
//                ) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
//                    // and add it to the message.
//                    taskSnapshot.metadata!!.reference!!.downloadUrl
//                        .addOnSuccessListener { uri ->
//                            val friendlyMessage =
//                                Message(null, getUserName(), getPhotoUrl(), uri.toString())
//                            db.reference
//                                .child("profil_picture")
//                                .child(key!!)
//                                .setValue(friendlyMessage)
//                        }
//                }
//                .addOnFailureListener(requireActivity()) { e ->
//                    Log.w(
//                        "MainActivity",
//                        "Image upload task was unsuccessful.",
//                        e
//                    )
//                }
//        }
 // Test depuis VSCode
}