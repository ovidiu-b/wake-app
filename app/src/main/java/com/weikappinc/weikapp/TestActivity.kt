package com.weikappinc.weikapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.weikappinc.weikapp.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val mBinding = getLayoutBinding(this, R.layout.activity_test) as ActivityTestBinding

        mBinding.image = "https://i.imgur.com/DvpvklR.png"

        //processFile()
    }

    private fun processFile() {
        val fileChooser = FileChooser(this@TestActivity)

        fileChooser.setFileListener { file ->
            // ....do something with the file
            val filename = file.absolutePath
            Log.d("File Name", filename)
            // then actually do something in another module
        }
        // Set up and filter my extension I am looking for
        fileChooser.setExtension("mp3")
        fileChooser.showDialog()
    }

}
