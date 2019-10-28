package de.hartz.software.profilepicturegenerator

import android.graphics.*
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList


// https://stackoverflow.com/questions/5663671/creating-an-empty-bitmap-and-drawing-though-canvas-in-android
class PictureInstance {

    var width = 2
    var height = 2
    var colors = intArrayOf(Color.CYAN, Color.RED)
    var maxColors = 2
    var singleShape = SingleShape.Rectangle
    var symmetric = Symmetric.NONE

    private val WIDTH = 512
    private val HEIGHT = 512

    private val random = Random()
    private var templateMatrix: ArrayList<ArrayList<PictureUnit>>? = null
    
    private var pictureUnitWidth: Float = 0f
    private var pictureUnitHeight: Float = 0f


    fun createNew(): Bitmap  {
        computeTemplateMatrix()
        return computeImage()
    }

    fun generate(): Bitmap  {
        return computeImage()
    }

    private fun computeTemplateMatrix() {
        templateMatrix = arrayListOf()

        for (y in 0 until height) {
            templateMatrix!!.add(arrayListOf())
            for (x in 0 until width) {
                val hasValue = getRandomBoolean(0.40f)
                templateMatrix!![y].add(PictureUnit(hasValue, Color.BLACK))
            }
        }
        updateColor()
    }

    fun updateColor() {
        if (colors.size < maxColors) {
            throw RuntimeException("there are less colors than max colors wanted")
        }
        val choosenColors = arrayListOf<Int>()
        val reducedColors = arrayListOf<Int>()
        reducedColors.addAll(colors.asList())
        for (i in 0 until maxColors) {
            choosenColors.add(reducedColors[random.nextInt(reducedColors.size)])
            reducedColors.remove(choosenColors[i])
        }

        for (y in 0 until height) {
            templateMatrix!!.add(arrayListOf())
            for (x in 0 until width) {
                val color = choosenColors[random.nextInt(choosenColors.size)]
                templateMatrix!![y][x].color = color
            }
        }
    }

    private fun computeImage() : Bitmap {
        val conf = Bitmap.Config.ARGB_8888 // see other conf types
        val bmp = Bitmap.createBitmap(WIDTH, HEIGHT, conf) // this creates a MUTABLE bitmap
        val canvas = Canvas(bmp)

        pictureUnitWidth = WIDTH.toFloat() / width
        pictureUnitHeight = HEIGHT.toFloat() / height

        val paint = Paint()
        paint.color = colors[0]
        paint.style = Paint.Style.FILL

        for (y in 0 until height) {
            for (x in 0 until width) {
                try {
                    val pictureUnit = getPictureUnitForPoint(x, y)
                    if (!pictureUnit.hasValue) {
                        continue
                    }
                    paint.color = pictureUnit.color
                    drawPictureUnit(canvas, paint, x, y)
                } catch (e: Exception) {

                }
            }
        }

        canvas.save()
        return bmp
    }

    private fun getPictureUnitForPoint(x: Int, y: Int) : PictureUnit {
        var calculatedX = x
        var calculatedY = y

        var midX =  (width / 2)
        if (width % 2 == 0) {
            midX =  (width / 2)
        }
        if ((symmetric == Symmetric.X || symmetric == Symmetric.REFLECTION) && x >= midX) {
           calculatedX = width - (x + midX)
        }

        var midY =  (height / 2)
        if (height % 2 == 0) {
            midY =  (height / 2)
        }
        if ((symmetric == Symmetric.Y || symmetric == Symmetric.REFLECTION) && y >= midY) {
           calculatedY = height - (y + midY)
        }
        var test = templateMatrix!![calculatedY][calculatedX]

        if (symmetric == Symmetric.Y && y >= midY) {
            test = PictureUnit(test.hasValue, Color.BLACK)
        }
        if (symmetric == Symmetric.X && x >= midX) {
            test = PictureUnit(test.hasValue, Color.MAGENTA)
        }
        return test
    }

    
    private fun drawPictureUnit(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        when (singleShape) {
            SingleShape.Rectangle -> canvas.drawRect(pictureUnitWidth * x, pictureUnitHeight * y, pictureUnitWidth * x + pictureUnitWidth, pictureUnitHeight * y + pictureUnitHeight, paint);
            SingleShape.Triangle -> {
                val wallpath = Path()
                if (x % 2 == 0 || true) {
                    wallpath.lineTo(pictureUnitWidth * x, pictureUnitHeight * y)
                    wallpath.lineTo(pictureUnitWidth * x + pictureUnitWidth / 2,  pictureUnitHeight * y + pictureUnitHeight)
                    wallpath.lineTo(pictureUnitWidth * x - pictureUnitWidth / 2,  pictureUnitHeight * y - pictureUnitHeight)
                } else {
                    wallpath.lineTo(pictureUnitWidth * x,  pictureUnitHeight * y + pictureUnitHeight)
                    wallpath.lineTo( pictureUnitWidth * x + pictureUnitWidth / 2, pictureUnitHeight * y + pictureUnitHeight)
                    wallpath.lineTo(pictureUnitWidth * x - pictureUnitWidth / 2, pictureUnitHeight * y - pictureUnitHeight)
                }

                canvas.drawPath(wallpath, paint)
            }
            SingleShape.Circle -> canvas.drawCircle(pictureUnitWidth * x + (pictureUnitWidth / 2), pictureUnitHeight * y + (pictureUnitWidth / 2), pictureUnitWidth / 2, paint);
        }
    }

    private fun getRandomBoolean(p: Float) : Boolean {
        return random.nextFloat() < p
    }

}

