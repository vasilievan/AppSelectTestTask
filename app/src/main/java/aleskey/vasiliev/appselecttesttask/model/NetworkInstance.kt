package aleskey.vasiliev.appselecttesttask.model

import aleskey.vasiliev.appselecttesttask.R.string.network_issue
import aleskey.vasiliev.appselecttesttask.model.SharedData.APPLICATION_CONTEXT
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownServiceException

/** Класс для работы с сетью. */

object NetworkInstance {

    /** Запись про фильм как сущность JSON */

    data class Video(val title: String, val description: String, val photo_url_string: String)

    /** Запись про фильм как изображение с заголовком и описанием. Более низкий уровень абстракции,
     * чем предыдущий класс.
     * */

    data class VideoUpdated(val title: String?, val description: String?, val photo: Bitmap?)

    /** Используемые URL, а также константы и ключ API*/

    private const val API_KEY = "somesecretkey :)"
    private const val URL_STRING =
        "https://api.nytimes.com/svc/movies/v2/reviews/all.json?api-key=$API_KEY"
    private const val DISPLAY_TITLE = "display_title"
    private const val SUMMARY_SHORT = "summary_short"
    private const val MULTIMEDIA = "multimedia"
    private const val USER_AGENT = "User-Agent"
    private const val BROWSER_CLIENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4;" +
            "en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
    private const val RESULTS = "results"

    /** Проверка на доступность wifi или мобильного Интернета. */

    private fun isConnectionAvailable(): Boolean {
        val cm =
            APPLICATION_CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    /** Построчное чтение JSON с сайта. */

    private fun readLines(httpURLConnection: HttpURLConnection): String {
        val responseContent = StringBuilder()
        httpURLConnection.inputStream.bufferedReader().use {
            it.lines().forEach { line -> responseContent.append(line) }
        }
        return responseContent.toString()
    }

    /** Парсинг полученных данных */

    private fun getVideosRaw(response: String): List<Video> {
        val jsonResponse = JSONObject(response)
        val jsonArray = jsonResponse.getJSONArray(RESULTS)
        var index = 0
        val videos = mutableListOf<Video>()
        while (jsonArray.length() != index) {
            val currentVideoString = JSONObject(jsonArray.get(index).toString())
            val displayTitle = currentVideoString.getString(DISPLAY_TITLE)
            val summaryShort = currentVideoString.getString(SUMMARY_SHORT)
            val multimediaString = currentVideoString.getJSONObject(MULTIMEDIA).toString()
            val photoUrlString = JSONObject(multimediaString).getString("src")
            val extract = Video(displayTitle, summaryShort, photoUrlString)
            videos.add(extract)
            index++
        }
        return videos
    }

    /** Предупреждение для пользователя о недоступности сети. */

    private fun makeCaution() {
        Looper.prepare()
        Toast.makeText(APPLICATION_CONTEXT, network_issue, Toast.LENGTH_LONG).show()
    }

    /** Получение списка с видео. */

    private suspend fun getVideosAsync(): Deferred<List<Video>?> =
        withContext(Dispatchers.Default) {
            async {
                var result: List<Video>? = null
                val usersUrl = URL(URL_STRING)
                if (isConnectionAvailable()) {
                    with(usersUrl.openConnection() as HttpURLConnection) {
                        try {
                            val response = readLines(this)
                            result = getVideosRaw(response)
                        } catch (e: IOException) {
                            makeCaution()
                        } catch (e: UnknownServiceException) {
                            makeCaution()
                        }
                    }
                } else {
                    makeCaution()
                }
                result
            }
        }

    /** Получение соответствующего изображения по ссылке. */

    private suspend fun loadDeferredPhotoByURLAsync(url_string: String): Deferred<Bitmap> =
        withContext(Dispatchers.Default) {
            var myBitmap: Bitmap? = null
            async {
                if (isConnectionAvailable()) {
                    with(URL(url_string).openConnection() as HttpURLConnection) {
                        try {
                            this.setRequestProperty(
                                USER_AGENT,
                                BROWSER_CLIENT
                            )
                            val input = this.inputStream
                            myBitmap = BitmapFactory.decodeStream(input)
                        } catch (e: IOException) {
                            makeCaution()
                        } catch (e: UnknownServiceException) {
                            makeCaution()
                        }
                    }
                } else {
                    makeCaution()
                }
                myBitmap!!
            }
        }

    /** Аналогично предыдущим методам, за исключением отсутствия Deferred */

    fun getVideos() = runBlocking {
        getVideosAsync().await()
    }

    fun loadPhotoByURL(url_string: String): Bitmap = runBlocking {
        loadDeferredPhotoByURLAsync(url_string).await()
    }
}
