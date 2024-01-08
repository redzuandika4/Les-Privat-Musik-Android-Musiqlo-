package com.redzuandika.musiqlo

import com.redzuandika.musiqlo.model_payment.MidtransNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MidtransApiService {
    @POST("/webhook/midtrans")
    fun handleMidtransNotification(@Body notification: MidtransNotification): Call<Void>

}