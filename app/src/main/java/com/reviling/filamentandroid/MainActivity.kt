package com.reviling.filamentandroid

import android.content.Intent
import android.os.Bundle
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    var surfaceView: SurfaceView? = null
    var customViewer: CustomViewer = CustomViewer()
    private lateinit var dataUrl: String
    private lateinit var dataImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        surfaceView = findViewById<View>(R.id.surface_view) as SurfaceView

        dataUrl = intent.getStringExtra(URL).toString()
        dataImage = intent.getStringExtra(IMAGE).toString()

        val imageView: ImageView = findViewById(R.id.image_ketuk)

        if (dataImage != "fileImage" && dataImage.isNotEmpty()) {
            Glide.with(this@MainActivity)
                .load(dataImage)
                .transform(CenterCrop(), RoundedCorners(20))
                .into(imageView)
        }

        customViewer.run {

            loadEntity()
            setSurfaceView(requireNotNull(surfaceView))
            lifecycleScope.launch {
                isLoading(true)
                loadGlbFromUrl(this@MainActivity, dataUrl, modelViewer)
                isLoading(false)
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
    }
}