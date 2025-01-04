package ipn.mx.wearables.presentation

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("get_memes")
    fun getMemes(): Call<MemeResponse>
}