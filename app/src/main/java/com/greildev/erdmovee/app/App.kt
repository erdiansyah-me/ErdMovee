package com.greildev.erdmovee.app

import android.app.Application
import com.google.android.gms.time.TrustedTimeClient
import com.google.android.material.color.DynamicColors
import com.greildev.core.utils.TrustedTimeClientAccessor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(){

    @Inject
    lateinit var trustedTimeClientAccessor: TrustedTimeClientAccessor

    var trustedTimeClient: TrustedTimeClient? = null
        private set
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        //trusted time
        trustedTimeClientAccessor.createClient().addOnCompleteListener { task ->
            trustedTimeClient = if (task.isSuccessful) {
                task.result
            } else {
                null
            }
        }
    }
}
