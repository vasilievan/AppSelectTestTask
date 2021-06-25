package aleskey.vasiliev.appselecttesttask.view

import aleskey.vasiliev.appselecttesttask.R
import aleskey.vasiliev.appselecttesttask.model.NetworkInstance.getVideos
import aleskey.vasiliev.appselecttesttask.model.SharedData.APPLICATION_CONTEXT
import aleskey.vasiliev.appselecttesttask.model.SharedData.PHONE_WIDTH
import aleskey.vasiliev.appselecttesttask.model.SharedData.VIDEOS
import aleskey.vasiliev.appselecttesttask.model.SharedData.openVideos
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

/**
 * Тестовое приложение для AppSelect. Представляет собой галерею для заданных фильмов
 * с сайта <a href="https://developer.nytimes.com/"></a>
 * @author <a href="mailto:enthusiastic.programmer@yandex.ru">Алексей Васильев</a>
 * @version 1.0
 *
 * Данный класс - splashscreen.
 */

class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        APPLICATION_CONTEXT = applicationContext
        setContentView(R.layout.splashscreen)
        setPhoneWidth()
        thread {
            VIDEOS = getVideos()
            openVideos()
        }
    }

    private fun setPhoneWidth() {
        val display = windowManager.defaultDisplay
        val realMetrics = DisplayMetrics()
        display.getRealMetrics(realMetrics)
        PHONE_WIDTH = realMetrics.widthPixels
    }
}