package de.hartz.software.profilepicturegenerator

import android.graphics.*
import java.lang.RuntimeException
import java.util.*


// https://stackoverflow.com/questions/5663671/creating-an-empty-bitmap-and-drawing-though-canvas-in-android
class PictureInstance {

    var width = 14
    var height = 14
    var colors = arrayListOf<Int>(Color.CYAN, Color.RED)
    var maxColors = 2
    var singleShape = SingleShape.Rectangle
    var symmetric = Symmetric.NONE

    private val WIDTH = 512
    private val HEIGHT = 512

    private val random = Random()
    private lateinit var templateMatrix: Array<Array<PictureUnit>>


    fun generate(): Bitmap  {
        computeTemplateMatrix()
        return computeImage()
    }

    private fun computeTemplateMatrix() {
        templateMatrix = arrayOf()

        if (colors.size < maxColors) {
            throw RuntimeException("there are less colors than max colors wanted")
        }
        val choosenColors = intArrayOf()
        for (i in 0..maxColors) {
            choosenColors[i] = colors[random.nextInt(colors.size)]
            colors.remove(choosenColors[i])
        }

        for (y in 0..height) {
            for (x in 0..width) {

            }
        }
    }

    private fun computeImage() : Bitmap {
        val conf = Bitmap.Config.ARGB_8888 // see other conf types
        val bmp = Bitmap.createBitmap(WIDTH, HEIGHT, conf) // this creates a MUTABLE bitmap
        val canvas = Canvas(bmp)

        val widthStep = WIDTH.toFloat() / width
        val heightStep = HEIGHT.toFloat() / height

        val paint = Paint()
        paint.color = colors[0]
        paint.setStyle(Paint.Style.FILL)

        if (symmetric == Symmetric.X) {
            height /= 2
        }

        for (w in 0..width) {
            for (h in 0..height) {
                if (getRandomBoolean(0.80f)) {
                    continue
                }
                paint.color = colors[(w + h) % 2]

                when (singleShape) {
                    SingleShape.Rectangle -> canvas.drawRect(widthStep * w, heightStep * h, widthStep * w + widthStep, heightStep * h + heightStep, paint);
                    SingleShape.Triangle -> {
                        val wallpath = Path()
                        if (w % 2 == 0) {
                            wallpath.lineTo(widthStep * w, heightStep * h)
                            wallpath.lineTo(widthStep / 2 + widthStep * w, heightStep + heightStep * h)
                            wallpath.lineTo(widthStep / 2 - widthStep * w, heightStep - heightStep * h)
                        } else {
                            wallpath.lineTo(widthStep * w, heightStep + heightStep * h)
                            wallpath.lineTo(widthStep / 2 + widthStep * w, heightStep + heightStep * h)
                            wallpath.lineTo(widthStep / 2 - widthStep * w, heightStep - heightStep * h)
                        }

                        canvas.drawPath(wallpath, paint)
                    }
                    SingleShape.Circle -> canvas.drawCircle(widthStep * w, heightStep * h, widthStep / 2, paint);
                }



            }
        }

        if (symmetric == Symmetric.X) {
            canvas.drawBitmap(
                bmp, Rect(0, 0, WIDTH, HEIGHT / 2),
                Rect(0, HEIGHT / 2, WIDTH, HEIGHT), null
            )
        }

        canvas.save()
        return bmp
    }

    private fun getRandomBoolean(p: Float) : Boolean {
        return random.nextFloat() < p
    }

}

