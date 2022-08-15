package com.beekay.thoughts

import android.app.Application
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator

private const val KEY_STORE = "AndroidKeyStore"
private const val THOUGHTS_ALIAS = "Thoughts_alias"

class Thoughts : Application() {
    override fun onCreate() {
        println("Came to application")
        super.onCreate()
        println("AM I called?")
        checkAndCreateKey()
    }

    private fun checkAndCreateKey() {
        val keystore = KeyStore.getInstance(KEY_STORE)
        keystore.load(null)
        println("Does ")
        if (!keystore.containsAlias(THOUGHTS_ALIAS)) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE)
        keyGenerator.init(
            KeyGenParameterSpec
                .Builder(
                    THOUGHTS_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build()
        )
        keyGenerator.generateKey()
    }

    companion object {
        fun getKey(): Key {
            val keystore = KeyStore.getInstance(KEY_STORE)
            keystore.load(null)
            return keystore.getKey(THOUGHTS_ALIAS, null)
        }
    }
}