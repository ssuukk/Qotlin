package pl.qus.qotlin

import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import pl.qus.qotlin.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*
import java.util.concurrent.TimeoutException

/**
 * A connection layer to an IBM Quantum Experience server.
 */
open class Qotlin
/**
 * Creates a session for a given user.
 *
 * @param apiToken api token of the user
 */
{
    /**
     * An access token for the current session.
     */
    private var sessionToken: String = ""

    var devices = listOf<QADevice>()

    private val gson = GsonBuilder()
            .setLenient()
            .create()

    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    //private val anotherInterceptor =

    private val cookies = object : CookieJar {
        var cookies = mutableListOf<Cookie>()

        override fun saveFromResponse(p0: HttpUrl?, p1: MutableList<Cookie>) {
            cookies = p1
        }

        override fun loadForRequest(p0: HttpUrl?): MutableList<Cookie> {
            return cookies
        }

    }

    private val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("surfproxy.de.db.com", 3128))

    private val okHttp = OkHttpClient.Builder()
            .proxy(proxy)
            .cookieJar(cookies)
            .addInterceptor {
                chain ->

                val resp = chain.proceed(chain.request())

                val body = resp.body()?.string()

                //println("stat=${resp.code()} ${resp.body()!!.contentType().toString()}***$body***")

                val newBody = ResponseBody.create(resp.body()!!.contentType(),body)

                if(resp.code()==400)
                    resp.newBuilder().code(200).body(newBody).build()
                else
                resp.newBuilder().body(newBody).build()
            }
            //.addInterceptor(interceptor)
            .build()

    private var retrofit: Retrofit = Retrofit.Builder()
            .client(okHttp)
            .baseUrl("https://quantumexperience.ng.bluemix.net")
            .addConverterFactory(GsonConverterFactory.create(gson))
            //.addCallAdapterFactory(instance())
            .build()

    private var api = retrofit.create(QARetrofitInterface::class.java)

    fun login(apiToken: String) {
            val result = api.login(apiToken).execute()

            if (result.body()?.userId?.isNotEmpty() == true) {
                sessionToken = result.body()?.id ?: ""
                true
            } else {
                false
            }
    }

    fun enumerateDevices() {
        devices = api.listDevices(sessionToken).execute()?.body() ?: listOf()

        devices.forEach {
            it.api = this
        }
    }

    fun device(name : String) = devices.firstOrNull { it.name == name && it.status ==  QADeviceStatus.on } ?: throw (IllegalStateException("No such device: $name" ))

    val simulator
        get() = devices.firstOrNull { it.simulator } ?: throw (IllegalStateException("Simulator not found" ))

    fun submitJob(newJob: QAJob): QAJob {
        val result = api.sendJob(sessionToken, newJob).execute()
        return if(result.body()?.error ==null) result.body()!! else throw RuntimeException("${result.body()?.error?.message}")
    }

    private fun receiveJob(job: QAJob): QAJob {
        val result = api.receiveJob(job.id, sessionToken).execute()
        return if(result.body()?.error ==null) result.body()!! else throw RuntimeException("${result.body()?.error?.message}")
    }


    /**
     *
     * Callback for receiving results of a job
     *
     * @param job a job to fetch from the server
     * @param maxTime fail if this waiting time is reached
     */
    fun onJobStatus(
            job: QAJob,
            maxTime: Int,
            onCompleted: (QAJob) -> Unit,
            onError: (Throwable) -> Unit = {}) {

        val jobStart = Calendar.getInstance()
        var sleep = 1

        do {
            val elapsed = (Calendar.getInstance().timeInMillis - jobStart.timeInMillis) / 1000.0

            if (sleep > maxTime) {
                onError(TimeoutException("timeout waiting for a completed job: ${elapsed}s"))
                break
            } else {
                try {
                    Thread.sleep((sleep * 1000.0).toLong())
                    sleep++
                } catch (e: InterruptedException) {
                    onError(e)
                    break
                }
            }

            try {
                val serverJob = receiveJob(job)
                if (serverJob.status == StatusEnum.COMPLETED) {
                    onCompleted(serverJob)
                    break
                }
            } catch (ex :Exception) {
                onError(ex)
                break
            }
        } while (true)
    }
}
