package aleskey.vasiliev.appselecttesttask.view

import aleskey.vasiliev.appselecttesttask.R
import aleskey.vasiliev.appselecttesttask.databinding.VideosBinding
import aleskey.vasiliev.appselecttesttask.model.NetworkInstance
import aleskey.vasiliev.appselecttesttask.model.NetworkInstance.loadPhotoByURL
import aleskey.vasiliev.appselecttesttask.model.SharedData.VIDEOS
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.System.exit
import kotlin.concurrent.thread

/**
 * Данный класс - активность, на которой выводятся фильмы с описанием и заголовком.
 */

class Videos : AppCompatActivity() {

    private lateinit var binding: VideosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VideosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val videoList = VIDEOS!!
        val myData = arrayListOf<NetworkInstance.VideoUpdated>()
        videoList.forEach { _ -> myData.add(NetworkInstance.VideoUpdated(null, null, null)) }
        val recyclerView = view.getViewById(R.id.videos_recyclerview) as RecyclerView
        val myAdapter = VideosRecyclerViewAdapter(myData)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = myAdapter
            setHasFixedSize(true)
        }
        thread {
            for (index in videoList.indices) {
                val bm = loadPhotoByURL(videoList[index].photo_url_string)
                myData[index] = NetworkInstance.VideoUpdated(
                    videoList[index].title,
                    videoList[index].description,
                    bm
                )
                runOnUiThread {
                    myAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * Для корректной работы splashscreen.
     */

    override fun onBackPressed() {
        exit(0)
    }
}