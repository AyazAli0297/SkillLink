package com.example.skilllink.network

import com.example.skilllink.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface SafePayApiService {
    
    @POST("checkout/create")
    suspend fun createCheckout(
        @Header("Authorization") authToken: String,
        @Body request: SafePayRequest
    ): Response<SafePayResponse>
    
    @GET("checkout/{token}")
    suspend fun getCheckoutStatus(
        @Header("Authorization") authToken: String,
        @Path("token") token: String
    ): Response<SafePayTracker>
    
    @POST("payment/capture")
    suspend fun capturePayment(
        @Header("Authorization") authToken: String,
        @Body captureRequest: CaptureRequest
    ): Response<CaptureResponse>
    
    @POST("payment/refund")
    suspend fun refundPayment(
        @Header("Authorization") authToken: String,
        @Body refundRequest: RefundRequest
    ): Response<RefundResponse>
}

data class CaptureRequest(
    val token: String,
    val amount: Int? = null
)

data class CaptureResponse(
    val success: Boolean,
    val message: String,
    val tracker: SafePayTracker? = null
)

data class RefundRequest(
    val token: String,
    val amount: Int? = null,
    val reason: String? = null
)

data class RefundResponse(
    val success: Boolean,
    val refund_id: String? = null,
    val message: String,
    val amount: Int? = null
)
