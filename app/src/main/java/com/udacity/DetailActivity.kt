package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val intent = intent
        if (intent != null && intent.hasExtra(getString(R.string.download_key))){
            val downloadFile = intent.getStringExtra(getString(R.string.download_key))
            if (downloadFile != null){
                message.text = downloadFile
                if (intent.getBooleanExtra(getString(R.string.success), false)){
                    status.text = getString(R.string.success)
                } else {
                    status.text = getString(R.string.failure)
                }
            } else {
                status.text = getString(R.string.failure)
            }
        }

        fab.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
    }
}
