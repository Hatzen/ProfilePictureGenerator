package de.hartz.software.profilepicturegeneratorsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import de.hartz.software.profilepicturegenerator.PictureGenerator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(PictureGenerator.generateSample())

        findViewById<Button>(R.id.generate).setOnClickListener {
            findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(PictureGenerator.generateSample())
        }

    }
}
