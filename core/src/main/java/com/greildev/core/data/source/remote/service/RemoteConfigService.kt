package com.greildev.core.data.source.remote.service

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.gson.Gson
import com.greildev.core.data.source.remote.response.PaymentResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RemoteConfigService @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    fun getPaymentList(): Flow<PaymentResponse> = callbackFlow {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            val gson = Gson()
            val stringJson = remoteConfig.getString("payment_list")
            if (stringJson.isNotEmpty()) {
                val jsonModel =
                    gson.fromJson(stringJson, PaymentResponse::class.java)
                trySend(jsonModel)
            }
        }
        awaitClose()
    }

    fun getPaymentListUpdated(): Flow<PaymentResponse> = callbackFlow {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (configUpdate.updatedKeys.contains("payment_list")) {
                    remoteConfig.activate().addOnCompleteListener {
                        val gson = Gson()
                        val stringJson = remoteConfig.getString("payment_list")
                        if (stringJson.isNotEmpty()) {
                            val jsonModel =
                                gson.fromJson(stringJson, PaymentResponse::class.java)
                            trySend(jsonModel)
                        }
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
            }
        })
        awaitClose()
    }
}