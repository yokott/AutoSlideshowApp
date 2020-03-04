package jp.techacademy.youko.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private  var mHandler = Handler()
    private  val image_list = arrayListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
            (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
        }else {
            getContentsInfo()
            var id = 0
            imageView.setImageURI(image_list[id])
            image_id.text = (id + 1).toString()
            image_size.text = "/" + image_list.size.toString()
            start_button.setOnClickListener {
                if (mTimer == null) {
                    start_button.text = "■"
                    right_button.isEnabled = false
                    left_button.isEnabled = false
                    Log.d("logtest", "startする")
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            Log.d("log", image_list.size.toString())
                            Log.d("log", id.toString())
                            id = if (id < image_list.size - 1) id + 1 else 0
                            mHandler.post {
                                imageView.setImageURI(image_list[id])
                                image_id.text = (id + 1).toString()
                            }
                        }
                    }, 2000, 2000)
                } else {
                    start_button.text = "▶"
                    mTimer!!.cancel()
                    mTimer = null
                    right_button.isEnabled = true
                    left_button.isEnabled = true
                }
            }
            right_button.setOnClickListener {
                id = if (id < image_list.size - 1) id + 1 else 0
                imageView.setImageURI(image_list[id])
                image_id.text = (id + 1).toString()
            }
            left_button.setOnClickListener {
                id = if (id > 0) id - 1 else image_list.size - 1
                imageView.setImageURI(image_list[id])
                image_id.text = (id + 1).toString()
            }

        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }
    private fun getContentsInfo() {
        Log.d("logtest","bbb")
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                image_list.add(imageUri)
                Log.d("log",imageUri.toString())
                Log.d("log",image_list.toString())
            }while(cursor.moveToNext())
        }
        cursor.close()
    }
}
