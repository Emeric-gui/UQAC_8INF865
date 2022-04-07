package com.example.project_uqac.ui.my_account

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.project_uqac.R
import com.example.project_uqac.ui.home.popupDiscussion.DialogFragmentDiscussion
import com.example.project_uqac.ui.my_account.dialogue.DialogueDeleteAccount
import com.example.project_uqac.ui.post.PostFragmentNature
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_account_sign_up.*
import java.io.ByteArrayOutputStream


class MyAccountLogged : Fragment() {

    private lateinit var viewPager2 : ViewPager2
    private lateinit var viewee : View
    private lateinit var imageView: ImageView
    private var reloaded : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account_logged, container, false)
        imageView = view.findViewById(R.id.imageViewMyAccountLogged)
//        img.setImageResource(R.drawable.ic_action_arrows_left)
//        Picasso.get().load("https://picsum.photos/300/300?random").into(img)
        viewee = view
//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//            // Handle the back button event
//        }

        // Print username
        val user = Firebase.auth.currentUser
        val username : TextView = view.findViewById(R.id.username)
        if (user != null) {
            username.text = user.displayName.toString()
        } else {
            username.text = "Error"
        }

        if (user != null && !reloaded) {
            // Create a storage reference from our app
            if(user.photoUrl != null){
                try {
                    val storageRef = Firebase.storage.getReferenceFromUrl(user.photoUrl.toString())
                    Log.d("Lets gowwww", user.photoUrl.toString())
//            var islandRef = storageRef.child("profil_pics/myImage")

                    val ONE_MEGABYTE: Long = 1024 * 1024
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
//                val myImage : ByteArray = it
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                        // Data for "images/island.jpg" is returned, use this as needed
                    }.addOnFailureListener {
//                    Log.d("MyAccountLogged : ", "No profil pic in database")
                    }
                } catch (e: Exception) {
                    imageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
                }

            } else {
                imageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
            }
//            imageView.setImageURI(user.photoUrl)
        }

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager2 = view.findViewById(R.id.my_account_logged_viewer)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = MyAccountViewPager2FragmentAdapter(this)
        viewPager2.adapter = pagerAdapter

        val TAB_TITLES = arrayOf("Home", "Informations", "My Posts")

        val tabLayout : TabLayout = view.findViewById(R.id.my_account_logged_tabs)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()

        val cardViewImageView : CardView = view.findViewById(R.id.cardViewImageViewMyAccountLogged)
//        val buttonTakePicture : Button = view.findViewById(R.id.my_account_take_pic)
        cardViewImageView.setOnClickListener {
            dispatchTakePictureIntent()
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
        Log.d("TAG", "azseghjk")
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
            val cropImg: Bitmap = Bitmap.createBitmap(imageBitmap, cropW, cropH, newWidth, newHeight)

            imageView.setImageBitmap(cropImg)

            // Update profil pic in db - Old style
//            val profileUpdates = userProfileChangeRequest {
//                photoUri = context?.let { getImageUri(it, imageView.drawable.toBitmap()) }
//            }
//            val user = Firebase.auth.currentUser
//            user!!.updateProfile(profileUpdates)
//                .addOnCompleteListener { innerTask ->
//                    if (innerTask.isSuccessful) {
//                        val fragment = MyAccountLogged()
//                        val transaction = fragmentManager?.beginTransaction()
//                        transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
//                    } else {
//                        Log.d(TAG, "Error with username.")
//                    }
//                }

        updatePic(cropImg)
        }
    }

    fun updatePic(img : Bitmap){
        reloaded = true
        // Update profil pic in db - New style
        Firebase.storage.reference.child("profil_pics/" + (Firebase.auth.currentUser?.email)).delete().addOnCompleteListener {
            val storageReferenceu = FirebaseStorage.getInstance().getReference("profil_pics/" + (Firebase.auth.currentUser?.email))
            var myUri = context?.let { getImageUri(it, img, "profil_pic." + Firebase.auth.currentUser?.email) }!!
            Log.d(TAG, myUri.toString())
            storageReferenceu.putFile(myUri).addOnSuccessListener {
                storageReferenceu.downloadUrl.addOnSuccessListener { bob ->
//                            Log.d(TAG, it.toString())
                    // Set profil pic
                    val profileUpdates = userProfileChangeRequest {
                        photoUri = bob
                    }
                    Firebase.auth.currentUser!!.updateProfile(profileUpdates)
                }
            }
        }
    }


    fun getImageUri(inContext: Context, inImage: Bitmap, name : String): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            name,
            null
        )
        return Uri.parse(path)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

                if(viewPager2.currentItem > 0)
                    viewPager2.currentItem = viewPager2.currentItem - 1
                else{
//                    requireActivity().onBackPressed()
                    Log.d("TAG", "message")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    fun goBackLogin(compteDeleted : Boolean){
//        Log.d(TAG, "zsdefgh.")
//        if(Firebase.auth.currentUser != null ){
//            FirebaseAuth.getInstance().signOut()
//        }
        if (compteDeleted){
            Toast.makeText(context, "Votre compte a bien été supprimé !", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Votre avez bien été déconnecté !", Toast.LENGTH_SHORT).show()
        }

        val fragment = MyAccountLogin()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()
//        Log.d("TAG", "J'y suis arrive !")
    }

    fun updateUsername(username : String){
        val usernamePlace : TextView = viewee.findViewById(R.id.username)
        usernamePlace.text = username
    }

}


//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(this, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    Log.d(TAG, "Fragment back pressed invoked")
//                    // Do custom work here
//
//                    // if you want onBackPressed() to be called as normal afterwards
//                    if (isEnabled) {
//                        isEnabled = false
//                        requireActivity().onBackPressed()
//                    }
//                }
//            }
//            )

//        val viewPager : ViewPager = view.findViewById(R.id.my_account_logged_viewer) // Good
//        viewPager.adapter = activity?.let { MyAccountViewPagerFragmentAdapter(it.supportFragmentManager) }

//        val tabLayout : TableLayout = view.findViewById(R.id.my_account_logged_tabs)
//        tabLayout.setupWithViewPager(viewPager)

//        TabLayoutMediator(view.findViewById(R.id.my_account_logged_tabs), view.findViewById(R.id.my_account_logged_viewer)) {
//                tab, position -> tab.text = TAB_TITLES[position]
//        }.attach()
//        val tabLayout : TabLayout = view.findViewById(R.id.my_account_logged_tabs)
//        tabLayout.addTab(tabLayout.newTab().setText("Bar"));
//        tabLayout.addTab(tabLayout.newTab().setText("Foo"));
//        val myAccountViewPagerFragmentAdapter : MyAccountViewPagerFragmentAdapter = MyAccountViewPagerFragmentAdapter()
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        val viewPager : ViewPager = view.findViewById(R.id.my_account_logged_viewer)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs : TabLayout = view.findViewById(R.id.my_account_logged_tabs)
//        tabs.setupWithViewPager(viewPager)

//        val bob : Button = view.findViewById(R.id.bob)
//        bob.setOnClickListener(){
//            //creation du fragment de dialogue
//            val dialogPage = DialogueDeleteAccount()
//
//            //ajout des infos dans le dialog
//            dialogPage.arguments(this)
//
//            dialogPage.show(childFragmentManager, "Custom Dialog")
//        }
//        val fragment = MyAccountLogin()
//        val transaction = fragmentManager?.beginTransaction()
//        transaction?.replace(R.id.my_account_fragment_navigation, fragment)?.commit()