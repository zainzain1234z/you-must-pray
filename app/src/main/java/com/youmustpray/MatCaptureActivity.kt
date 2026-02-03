package com.youmustpray

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.youmustpray.databinding.ActivityMatCaptureBinding

class MatCaptureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMatCaptureBinding

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            binding.matPreview.setImageBitmap(bitmap)
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureButton.setOnClickListener {
            cameraLauncher.launch(null)
        }
    }
}
