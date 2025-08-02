package com.example.skilllink.network

import com.example.skilllink.config.PaymentConfig
import com.example.skilllink.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SafePayGateway {
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(PaymentConfig.SAFEPAY_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val safePayService = retrofit.create(SafePayApiService::class.java)
    
    suspend fun createCheckout(
        amount: Double,
        customer: SafePayCustomer,
        orderId: String
    ): SafePayResponse {
        return try {
            val request = SafePayRequest(
                amount = amount,
                currency = "PKR",
                customer = customer,
                order_id = orderId,
                webhook = PaymentConfig.SAFEPAY_WEBHOOK_URL,
                redirect_url = PaymentConfig.SAFEPAY_REDIRECT_URL
            )
            
            val response = safePayService.createCheckout(
                authToken = PaymentConfig.generateAuthToken(),
                request = request
            )
            
            if (response.isSuccessful) {
                response.body() ?: SafePayResponse(
                    success = false,
                    message = "Empty response from SafePay"
                )
            } else {
                SafePayResponse(
                    success = false,
                    message = "HTTP ${response.code()}: ${response.message()}",
                    error = "api_error"
                )
            }
        } catch (e: Exception) {
            SafePayResponse(
                success = false,
                message = "Network error: ${e.message}",
                error = "network_error"
            )
        }
    }
    
    suspend fun getPaymentStatus(token: String): SafePayTracker? {
        return try {
            val response = safePayService.getCheckoutStatus(
                authToken = PaymentConfig.generateAuthToken(),
                token = token
            )
            
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun capturePayment(token: String, amount: Int? = null): CaptureResponse {
        return try {
            val request = CaptureRequest(token = token, amount = amount)
            val response = safePayService.capturePayment(
                authToken = PaymentConfig.generateAuthToken(),
                captureRequest = request
            )
            
            response.body() ?: CaptureResponse(
                success = false,
                message = "Empty response from SafePay"
            )
        } catch (e: Exception) {
            CaptureResponse(
                success = false,
                message = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun refundPayment(token: String, amount: Int? = null, reason: String? = null): RefundResponse {
        return try {
            val request = RefundRequest(token = token, amount = amount, reason = reason)
            val response = safePayService.refundPayment(
                authToken = PaymentConfig.generateAuthToken(),
                refundRequest = request
            )
            
            response.body() ?: RefundResponse(
                success = false,
                message = "Empty response from SafePay"
            )
        } catch (e: Exception) {
            RefundResponse(
                success = false,
                message = "Network error: ${e.message}"
            )
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: SafePayGateway? = null
        
        fun getInstance(): SafePayGateway {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SafePayGateway().also { INSTANCE = it }
            }
        }
    }
}
