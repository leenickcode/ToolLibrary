package com.nicklee.nfcdemo

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class BaseBLNFCActivity : AppCompatActivity() {

    companion object {
        val permissionsArray = arrayOf(Manifest.permission.NFC)
    }

    lateinit var mNfcAdapter: NfcAdapter

    lateinit var mPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onStart() {
        super.onStart()

            mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
            mPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass), 0)



    }

    override fun onResume() {
        super.onResume()

            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null)



    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter.disableForegroundDispatch(this)
    }


}