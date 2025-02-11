package com.reviling.filamentandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.ByteArrayLoader.ByteBufferFactory
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {

    var surfaceView: SurfaceView? = null
    var customViewer: CustomViewer = CustomViewer()
    private lateinit var dataUrl: String
    private lateinit var dataId: String
    private lateinit var dataImage: String
    private lateinit var downloadButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private var bufferHere: ByteBuffer? = null
    private var dataIdSave: String? = null
    private var deleteFlags: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        surfaceView = findViewById<View>(R.id.surface_view) as SurfaceView

        dataUrl = intent.getStringExtra(URL).toString()
        dataImage = intent.getStringExtra(IMAGE).toString()
        dataId = intent.getStringExtra(IDTRIDI).toString()

        downloadButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)
        downloadButton.isEnabled = false
        deleteButton.isEnabled = false

        val imageView: ImageView = findViewById(R.id.image_ketuk)

        if (dataImage != "fileImage" && dataImage.isNotEmpty()) {
            Glide.with(this@MainActivity)
                .load(dataImage)
                .transform(CenterCrop(), RoundedCorners(20))
                .into(imageView)
        }

        var files: Array<String> = this.fileList()

        for (file in files) {
            if ("${dataId}.glb" == file) {
                dataIdSave = file
            }
        }

        customViewer.run {
            loadEntity()
            setSurfaceView(requireNotNull(surfaceView))
            lifecycleScope.launch {
                isLoading(true)
//                downloadButton.isEnabled = false
//                loadGlbFromUrl(this@MainActivity, dataUrl, modelViewer)
//                isLoading(false)
//                downloadButton.isEnabled = true

                downloadButton.setOnClickListener {
                    if (deleteFlags == 1) {
                        lifecycleScope.launch {
                            isLoading(true)
                            loadGlbFromUrl(this@MainActivity, dataUrl, modelViewer)
                            this@MainActivity.openFileOutput("${dataId}.glb", Context.MODE_PRIVATE).use {
                                it.write(buffer?.toByteString()?.toByteArray())
                                deleteButton.isEnabled = true
                                downloadButton.isEnabled = false
                            }
                            isLoading(false)
                        }
                    } else {
                        isLoading(true)
                        this@MainActivity.openFileOutput("${dataId}.glb", Context.MODE_PRIVATE).use {
                            it.write(buffer?.toByteString()?.toByteArray())
                            deleteButton.isEnabled = true
                            downloadButton.isEnabled = false
                        }
                        isLoading(false)
                    }
                }

                deleteButton.setOnClickListener {
                    isLoading(true)
                    var files: Array<String> = this@MainActivity.fileList()

                    for (file in files) {
                        Log.d("IsiDariFiles", file)
                    }

                    this@MainActivity.deleteFile("${dataId}.glb")
                    downloadButton.isEnabled = true
                    deleteButton.isEnabled = false
                    deleteFlags = 1
                    isLoading(false)
                }

                if (dataIdSave != null) {
                    val filesTest = filesDir.listFiles()
                    filesTest?.filter { it.canRead() && it.isFile && it.name.endsWith(".glb") && it.name == "${dataId}.glb" }?.map {
                        val bytes = it.readBytes()
                        val buffer = ByteBuffer.wrap(bytes)
                        loadGlbFromLocal(this@MainActivity, buffer, modelViewer)
                    }
                    downloadButton.isEnabled = false
                    deleteButton.isEnabled = true
                    isLoading(false)
                } else {
                    loadGlbFromUrl(this@MainActivity, dataUrl, modelViewer)
                    bufferHere = buffer
                    isLoading(false)
                    downloadButton.isEnabled = true
                    deleteButton.isEnabled = false
                }
            }

            loadIndirectLight(this@MainActivity, "venetian_crossroads_2k")

//            val builder = AlertDialog.Builder(this@MainActivity)
//            val inflater = layoutInflater
//            val dialogView = inflater.inflate(R.layout.dialog_layout, null)
//
//            val buttonDonut = dialogView.findViewById<Button>(R.id.donutButton)
//            val buttonGangsa = dialogView.findViewById<Button>(R.id.gangsaButton)
//            val buttonAngklung = dialogView.findViewById<Button>(R.id.buttonAngklung)
////            val buttonChair = dialogView.findViewById<Button>(R.id.chairButton)
//
//            builder.setView(dialogView)
//            val dialog = builder.create()
//            dialog.show()
//
//            buttonDonut.setOnClickListener {
//                //loadGltf(this@MainActivity, "warcraft", "scene");
////
//                lifecycleScope.launch {
//                    loadGlbFromUrl(this@MainActivity, dataUrl, modelViewer)
//                }
//                loadGlb(this@MainActivity, "grogu", "guntangreal")
//                //directory and model as one
//                //loadGlb(this@MainActivity, "grogu/grogu");
//
//                //Enviroments and Lightning (OPTIONAL)
//                loadIndirectLight(this@MainActivity, "venetian_crossroads_2k")
//                //loadEnviroment(this@MainActivity, "venetian_crossroads_2k");
//
//                dialog.dismiss()
//            }
////
////            buttonGangsa.setOnClickListener {
////                //directory and model each as param
////                loadGlb(this@MainActivity, "grogu", "gender")
////                //loadGltf(this@MainActivity, "warcraft", "scene");
////
////                //directory and model as one
////                //loadGlb(this@MainActivity, "grogu/grogu");
////
////                //Enviroments and Lightning (OPTIONAL)
////                loadIndirectLight(this@MainActivity, "venetian_crossroads_2k")
////                //loadEnviroment(this@MainActivity, "venetian_crossroads_2k");
////
////                dialog.dismiss()
////            }
////
////            buttonAngklung.setOnClickListener {
////                //directory and model each as param
////                loadGlb(this@MainActivity, "grogu", "cencengricik")
////
////                //Enviroments and Lightning (OPTIONAL)
////                loadIndirectLight(this@MainActivity, "venetian_crossroads_2k")
////
////                dialog.dismiss()
////            }
////
//////            buttonChair.setOnClickListener {
//////                loadGlb(this@MainActivity, "grogu", "chair")
//////
//////                loadIndirectLight(this@MainActivity, "venetian_crossroads_2k")
//////
//////                dialog.dismiss()
//////            }

        }

        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        customViewer.onResume()
    }

    override fun onPause() {
        super.onPause()
        customViewer.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        customViewer.onDestroy()
    }
    private fun isLoading(loading: Boolean) {
        val progressBar: ProgressBar = findViewById(R.id.progress_bar_main)
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finish()
//    }

    companion object {
        const val URL = "urlTridi"
        const val IMAGE = "fileImage"
        const val IDTRIDI = "idtridi"
    }
}