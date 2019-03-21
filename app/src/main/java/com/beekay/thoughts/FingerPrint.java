package com.beekay.thoughts;


import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.beekay.thoughts.util.FingerPrintHandler;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerPrint extends AppCompatActivity {

    private static final String KEY_NAME = "myKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView messageText;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(
                this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
        } else {
            initContent();
        }
    }

    private void initContent() {
        setContentView(R.layout.activity_finger_print);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            messageText = (TextView) findViewById(R.id.print_message);

            if (!fingerprintManager.isHardwareDetected()) {
                messageText.setText("Your Device Doesn't Support Fingerprint Authentication");
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                messageText.setText("No permission");
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                messageText.setText("No FingerPrints");
            }

            if (!keyguardManager.isKeyguardSecure()) {
                messageText.setText("Please enable lock screen security");
            } else {
                try {
                    generateKey();
                } catch (Exception e) {

                }

                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerPrintHandler helper = new FingerPrintHandler(FingerPrint.this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }

    private boolean initCipher() {
        try{
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            +KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return true;
        } catch(KeyPermanentlyInvalidatedException e){
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void generateKey() {
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);

            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initContent();
                } else {
                    System.exit(1);
                }
        }
    }
}
