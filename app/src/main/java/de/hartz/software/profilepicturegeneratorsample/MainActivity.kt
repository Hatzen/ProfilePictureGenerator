package de.hartz.software.profilepicturegeneratorsample

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import de.hartz.software.profilepicturegenerator.PictureGenerator
import de.hartz.software.profilepicturegenerator.PictureInstance
import de.hartz.software.profilepicturegenerator.SingleShape
import de.hartz.software.profilepicturegenerator.Symmetric
import java.util.*

class MainActivity : AppCompatActivity() {

    var width = 5
    var height = 5
    var colors = intArrayOf(
        Color.rgb(30, 136, 229),
        Color.rgb(233, 30, 99),
        Color.rgb(0, 150, 136)
    )
    var shape = SingleShape.Rectangle
    var symmetric = Symmetric.X

    private lateinit var instance: PictureInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instance = PictureGenerator.generate(width, height, colors, 2, shape, symmetric)

        findViewById<Button>(R.id.symmetric).apply {
            text = "Symmetric: ${symmetric.name}"
            setOnClickListener {
                symmetric = when (symmetric) {
                    Symmetric.X -> Symmetric.Y
                    Symmetric.Y -> Symmetric.REFLECTION
                    Symmetric.REFLECTION -> Symmetric.NONE
                    Symmetric.NONE -> Symmetric.X
                }
                text = "Symmetric: ${symmetric.name}"
                refreshImage()
            }
        }

        findViewById<Button>(R.id.shape).apply {
            text = "Shape: ${shape.name}"
            setOnClickListener {
                shape = when (shape) {
                    SingleShape.Rectangle -> SingleShape.Circle
                    SingleShape.Circle -> SingleShape.Triangle
                    SingleShape.Triangle -> SingleShape.Rectangle
                }
                text = "Shape: ${shape.name}"
                refreshImage()
            }
        }

        findViewById<Button>(R.id.color).apply {
            text = "Randomize Colors"
            setOnClickListener {
                colors = intArrayOf(getRandomColor(), getRandomColor(), getRandomColor())
                instance.colors = colors
                instance.updateColor()
                findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.generate())
            }
        }

        findViewById<Button>(R.id.generate).setOnClickListener {
            createNew()
        }

        // Render initial profile picture
        createNew()
    }

    private fun createNew() {
        instance.width = width
        instance.height = height
        instance.colors = colors
        instance.singleShape = shape
        instance.symmetric = symmetric
        instance.seed = UUID.randomUUID().toString()
        findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.createNew())
    }

    private fun refreshImage() {
        instance.singleShape = shape
        instance.symmetric = symmetric
        findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.generate())
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}
