package pl.qus.qotlin.model

import pl.qus.qotlin.model.Const.HD_CLIENT_APP
import pl.qus.qotlin.model.Const.HD_ENCODING
import pl.qus.qotlin.model.Const.HD_JSON
import pl.qus.qotlin.model.Const.HD_USER_AGENT
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Body

object Const {
    const val HD_USER_AGENT = "User-Agent:python-requests/2.18.4"
    const val HD_CLIENT_APP = "x-qx-client-application:qiskit-api-py"
    const val HD_ENCODING = "Accept-Encoding:identity"
    const val HD_JSON = "Content-Type:application/json"
}

interface QARetrofitInterface {
    /**
     * Logs in to the server. Creates an access token for the current session.
     *
     * @return an access token - z pola id
     */
    @Headers(
            HD_USER_AGENT,
            HD_CLIENT_APP,
            HD_ENCODING
    )
    @FormUrlEncoded
    @POST("api/users/loginWithToken")
    fun login(
            @Field("apiToken") apiToken : String
    ) : Call<QALoginResponse>

    /**
     * Returns the devices (backends) on the server.
     *
     * @return a map of devices keyed with their names, null for none
     */

    @Headers(
            HD_USER_AGENT,
            HD_CLIENT_APP,
            HD_ENCODING
    )
    @GET("api/backends")
    fun listDevices(
            @Query("access_token") accessToken : String
    ) : Call<List<QADevice>>

    /**
     * Sends a new job.
     *
     * @param newJob a new job, with one or more tasks
     * @return the jobs just sent, with new ids attached
     */
    @Headers(
            HD_USER_AGENT,
            HD_CLIENT_APP,
            HD_ENCODING,
            HD_JSON
    )
    @POST("api/Jobs")
    fun sendJob(
            @Query("access_token") accessToken: String,
            @Body newJob: QAJob
    ) : Call<QAJob>

    /**
     * Receives a given job from the server, with its status and possible computation results.
     *
     * @param job a job to fetch from the server
     * @return a description of the same job, with its current status and a possible result
     */
    @Headers(
            HD_USER_AGENT,
            HD_CLIENT_APP,
            HD_ENCODING
    )
    @GET("api/Jobs/{jobId}")
    fun receiveJob(
            @Path("jobId") jobId: String,
            @Query("access_token") accessToken : String
    ): Call<QAJob>
}