package com.greildev.erdmovee.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object Analytics {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    fun logEvent(
        eventName: String,
        bundle: Bundle
    ) {
        firebaseAnalytics.logEvent(
            eventName,
            bundle
        )
    }
}