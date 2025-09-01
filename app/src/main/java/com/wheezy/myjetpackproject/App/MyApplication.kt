package com.wheezy.myjetpackproject.App

import android.app.Application
import com.wheezy.myjetpackproject.BuildConfig
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PaymentConfiguration.init(
            applicationContext,
            BuildConfig.STRIPE_PUBLISHABLE_KEY
        )
    }
}
