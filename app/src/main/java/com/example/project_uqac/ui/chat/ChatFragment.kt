package com.example.project_uqac.ui.chat

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project_uqac.R
import com.example.project_uqac.databinding.FragmentChatBinding
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.scaledrone.lib.*
import kotlin.math.floor
import kotlin.random.Random


class ChatFragment : Fragment(), RoomListener{
    private val channelID = "1VL6drCySuNRSOYl"
    private val roomName = "observable-room"
    private var data = MemberData(getRandomName(), getRandomColor())
    private val scaledrone: Scaledrone = Scaledrone(channelID, data)
    private var messageAdapter: MessageAdapter? = null
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var messagesView: ListView
    private var imageButton: ImageButton? = null
    private var message: EditText? = null
    private lateinit var _context: Context


    private lateinit var dashboardViewModel: ChatViewModel
    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        messageAdapter = MessageAdapter(_context)
        messagesView = root.findViewById(R.id.messages_view)
        messagesView.adapter = messageAdapter

        val obj = object : Listener {
            override fun onOpen() {
                println("Scaledrone connection open")
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone.subscribe(roomName, ChatFragment())
            }

            override fun onOpenFailure(ex:Exception) {
                System.err.println(ex)
            }

            override fun onFailure(ex:Exception) {
                System.err.println(ex)
            }

            override fun onClosed(reason:String) {
                System.err.println(reason)
            }
        }
        scaledrone.connect(obj)

        imageButton = root.findViewById(R.id.sendMessage)
        imageButton?.setOnClickListener {
            println(messagesView)
            message = root.findViewById(R.id.editText)
            val messageString = message?.text.toString()
            if (messageString.isNotEmpty()) {
                scaledrone.publish("observable-room", messageString)
                message?.text?.clear()
            }
        }

        return root
    }

    // Successfully connected to Scaledrone room
    override fun onOpen(room: Room?) {
        println("Connected to room")
    }

    // Connecting to Scaledrone room failed
    override fun onOpenFailure(room: Room?, ex: java.lang.Exception?) {
        System.err.println(ex)
    }

    override fun onMessage(room: Room?, receivedMessage: com.scaledrone.lib.Message) {
        val mapper = ObjectMapper()

        try {
            val data = mapper.treeToValue(
                receivedMessage.member.clientData,
                MemberData::class.java
            )
            val belongsToCurrentUser = receivedMessage.clientID == scaledrone.clientID
            val message = Message(receivedMessage.data.asText(), data, belongsToCurrentUser)
            mHandler.post{
                println(message.text)
                messageAdapter?.add(message)
                //messagesView.setSelection(messagesView.count - 1)
            }
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }



    private fun getRandomName(): String {
        val adjs = arrayOf(
            "autumn",
            "hidden",
            "bitter",
            "misty",
            "silent",
            "empty",
            "dry",
            "dark",
            "summer",
            "icy",
            "delicate",
            "quiet",
            "white",
            "cool",
            "spring",
            "winter",
            "patient",
            "twilight",
            "dawn",
            "crimson",
            "wispy",
            "weathered",
            "blue",
            "billowing",
            "broken",
            "cold",
            "damp",
            "falling",
            "frosty",
            "green",
            "long",
            "late",
            "lingering",
            "bold",
            "little",
            "morning",
            "muddy",
            "old",
            "red",
            "rough",
            "still",
            "small",
            "sparkling",
            "throbbing",
            "shy",
            "wandering",
            "withered",
            "wild",
            "black",
            "young",
            "holy",
            "solitary",
            "fragrant",
            "aged",
            "snowy",
            "proud",
            "floral",
            "restless",
            "divine",
            "polished",
            "ancient",
            "purple",
            "lively",
            "nameless"
        )
        val nouns = arrayOf(
            "waterfall",
            "river",
            "breeze",
            "moon",
            "rain",
            "wind",
            "sea",
            "morning",
            "snow",
            "lake",
            "sunset",
            "pine",
            "shadow",
            "leaf",
            "dawn",
            "glitter",
            "forest",
            "hill",
            "cloud",
            "meadow",
            "sun",
            "glade",
            "bird",
            "brook",
            "butterfly",
            "bush",
            "dew",
            "dust",
            "field",
            "fire",
            "flower",
            "firefly",
            "feather",
            "grass",
            "haze",
            "mountain",
            "night",
            "pond",
            "darkness",
            "snowflake",
            "silence",
            "sound",
            "sky",
            "shape",
            "surf",
            "thunder",
            "violet",
            "water",
            "wildflower",
            "wave",
            "water",
            "resonance",
            "sun",
            "wood",
            "dream",
            "cherry",
            "tree",
            "fog",
            "frost",
            "voice",
            "paper",
            "frog",
            "smoke",
            "star"
        )
        return adjs[floor(Math.random() * adjs.size).toInt()] +
                "_" +
                nouns[floor(Math.random() * nouns.size).toInt()]
    }

    private fun getRandomColor(): String {
        val r = Random(7)
        val sb = StringBuffer("#")
        while (sb.length < 7) {
            sb.append(Integer.toHexString(r.nextInt()))
        }
        return sb.toString().substring(0, 7)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context=context
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val root: View = binding.root

        messageAdapter = MessageAdapter(_context)
        messagesView = root.findViewById(R.id.messages_view)
        messagesView.adapter = messageAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}