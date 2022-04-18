package com.example.project_uqac.ui.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.project_uqac.R
import com.example.project_uqac.ui.chat.ChatFragment
import com.example.project_uqac.ui.chat.Message
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.NotificationParams
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseNotificationService : FirebaseMessagingService() {

    private val appUtil = AppUtil()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.data.isNotEmpty()){
            val map : Map<String,String> = message.data

            val title : String? = map["title"]
            val message : String? = map["message"]
            val hisId : String? = map["hisId"]
            val chatId : String? = map["chatId"]

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                createOreoNotification(title!!, message!!, hisId!!, chatId!!)
            } else {
                createNormalNotification(title!!, message!!, hisId!!, chatId!!)
            }
        }
    }

    private fun updateToken(token:String){

        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(appUtil.getUserID()!!)
        val map : MutableMap<String,Any> = HashMap()
        map["token"] = token
        databaseReference.setValue(map)


    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNormalNotification(title:String, message:String, hisId:String, chatId:String){
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, "Message Channel")
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_small_icon)
            .setAutoCancel(true)
            .setSound(uri)

        val intent = Intent(this, ChatFragment::class.java)
        intent.putExtra("hisId", hisId)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent : PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager : NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(85 - 65), builder.build())

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createOreoNotification(title:String, message:String, hisId:String, chatId:String){
        val channel = NotificationChannel("Message channel", "Message", NotificationManager.IMPORTANCE_HIGH)
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager : NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, ChatFragment::class.java)
        intent.putExtra("hisId", hisId)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent : PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, "Message channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_small_icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }

}