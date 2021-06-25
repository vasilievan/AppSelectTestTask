package aleskey.vasiliev.appselecttesttask.model

import aleskey.vasiliev.appselecttesttask.view.Videos
import android.content.Context
import android.content.Intent

/** Синглтон, в котором содержатся данные, используемые другими классами.*/

object SharedData {
    lateinit var APPLICATION_CONTEXT: Context
    var PHONE_WIDTH = 0
    const val IMAGE_MARGIN = 40
    var VIDEOS: List<NetworkInstance.Video>? = null

    /** Переход между активностями после загрузки splashscreen. */

    fun openVideos() {
        val intent = Intent(APPLICATION_CONTEXT, Videos::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        APPLICATION_CONTEXT.startActivity(intent)
    }
}