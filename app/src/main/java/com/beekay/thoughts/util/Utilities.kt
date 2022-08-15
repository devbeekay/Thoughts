package com.beekay.thoughts.util

import android.util.Base64
import com.beekay.thoughts.Thoughts
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

fun encryptThought(thought: String) =
    try {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivBytes = ByteArray(12)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(ivBytes)
        cipher.init(
            Cipher.ENCRYPT_MODE, Thoughts.getKey(),
            GCMParameterSpec(128, ivBytes)
        )
        val encodedBytes = cipher.doFinal(thought.toByteArray(Charsets.UTF_8))
        val encodedString = Base64.encodeToString(encodedBytes, 0)
        val encodedIv = Base64.encodeToString(ivBytes, 0)
        "$encodedIv:$encodedString"
    } catch (ex: Exception) {
        Base64.encodeToString(thought.toByteArray(Charsets.UTF_8), 0)
    }

fun decryptThought(encryptedThought: String) =
    try {
        val iv = encryptedThought.split(":")[0]
        val encryptedPass = encryptedThought.split(":")[1]
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE, Thoughts.getKey(),
            GCMParameterSpec(128, Base64.decode(iv, 0))
        )
        val encryptedBytes = Base64.decode(encryptedPass, 0)
        val decodedBytes = cipher.doFinal(encryptedBytes)
        String(decodedBytes)
    } catch (ex: Exception) {
            encryptedThought

    }