package de.hartz.software.profilepicturegeneratorsample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import de.hartz.software.profilepicturegenerator.PictureGenerator
import de.hartz.software.profilepicturegenerator.PictureInstance
import de.hartz.software.profilepicturegenerator.SingleShape
import de.hartz.software.profilepicturegenerator.Symmetric
import java.util.*

class MainActivity : AppCompatActivity() {

    var width = 2
    var height = 2
    var colors = intArrayOf(Color.CYAN, Color.RED)
    var shape = SingleShape.Rectangle
    var symmetric = Symmetric.NONE

    private lateinit var instance: PictureInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.symmetric).setOnClickListener {
            symmetric = when (symmetric) {
                Symmetric.X -> Symmetric.Y
                Symmetric.Y -> Symmetric.REFLECTION
                Symmetric.REFLECTION -> Symmetric.NONE
                Symmetric.NONE -> Symmetric.X
            }
            (it as Button).setText(symmetric.toString())
            generate()
        }
        findViewById<Button>(R.id.shape).setOnClickListener {
            shape = when (shape) {
                SingleShape.Rectangle -> SingleShape.Circle
                SingleShape.Circle -> SingleShape.Triangle
                SingleShape.Triangle -> SingleShape.Rectangle
            }
            (it as Button).setText(shape.toString())
            generate()
        }
        findViewById<Button>(R.id.color).setOnClickListener {
            colors = intArrayOf(getRandomColor(), getRandomColor(), getRandomColor())
            (it as Button).setText(colors[0].toString())
            instance.updateColor()
            generate()
        }

        findViewById<Button>(R.id.generate).setOnClickListener {
            createNew()
        }

        instance = PictureGenerator.generate(width, height, colors, 1, shape, symmetric)
        instance.createNew()


    }

    private fun createNew() {
        instance.colors = colors
        instance.singleShape = shape
        instance.symmetric = symmetric
        findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.createNew())
    }

    private fun generate() {
        instance.colors = colors
        instance.singleShape = shape
        instance.symmetric = symmetric
        findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.generate())
    }

    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}
