package com.fitbit.fitbitapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.fitbit.authentication.*
import com.fitbit.fitbitapp.activities.LoginActivity

class MyApplication : Application() {
    /**
     * 1. When the application starts, load our keys and configure the AuthenticationManager
     */
    override fun onCreate() {
        super.onCreate()
        AuthenticationManager.configure(this, generateAuthenticationConfiguration(this, LoginActivity::class.java))
    }

    companion object {
        /**
         * These client credentials come from creating an app on https://dev.fitbit.com.
         *
         *
         * To use with your own app, register as a developer at https://dev.fitbit.com, create an application,
         * set the "OAuth 2.0 Application Type" to "Client", enter a word for the redirect url as a url
         * (like `https://finished` or `https://done` or `https://completed`, etc.), and save.
         *
         *
         */
//!! THIS SHOULD BE IN AN ANDROID KEYSTORE!! See https://developer.android.com/training/articles/keystore.html
        private const val CLIENT_SECRET = "86401692efd006045a157f45755000d0"
        //private static final String CLIENT_SECRET = "29688d74fd8ee887ee056045f026ad54";
        /**
         * This key was generated using the SecureKeyGenerator [java] class. Run as a Java application (not Android)
         * This key is used to encrypt the authentication token in Android user preferences. If someone decompiles
         * your application they'll have access to this key, and access to your user's authentication token
         */
//!! THIS SHOULD BE IN AN ANDROID KEYSTORE!! See https://developer.android.com/training/articles/keystore.html
        private const val SECURE_KEY = "CVPdQNAT6fBI4rrPLEn9x0+UV84DoqLFiNHpKOPLRW0="
        //private static final String SECURE_KEY = "nUL2i3eaS+2fbO/RdKs0LcJvLoL5HBDq3SL6ZKZFWf8=";
        /**
         * This method sets up the authentication config needed for
         * successfully connecting to the Fitbit API. Here we include our client credentials,
         * requested scopes, and  where to return after login
         */
        fun generateAuthenticationConfiguration(context: Context, mainActivityClass: Class<out Activity?>?): AuthenticationConfiguration {
            return try {
                val ai = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val bundle = ai.metaData
                // Load clientId and redirectUrl from application manifest
                val clientId = bundle.getString("com.fitbit.fitbitapp.CLIENT_ID")
                val redirectUrl = bundle.getString("com.fitbit.fitbitapp.REDIRECT_URL")
                val CLIENT_CREDENTIALS = ClientCredentials(clientId, CLIENT_SECRET, redirectUrl)
                AuthenticationConfigurationBuilder()
                        .setClientCredentials(CLIENT_CREDENTIALS)
                        .setEncryptionKey(SECURE_KEY)
                        .setTokenExpiresIn(2592000L) // 30 days
                        .setBeforeLoginActivity(Intent(context, mainActivityClass))
                        .addRequiredScopes(Scope.profile, Scope.settings)
                        .addOptionalScopes(Scope.activity, Scope.weight)
                        .setLogoutOnAuthFailure(true)
                        .build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}