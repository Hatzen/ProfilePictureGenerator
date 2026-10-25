package de.hartz.software.profilepicturegenerator

import android.graphics.*
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generator instance responsible for producing procedural identicon bitmaps
 * based on seeds, shapes, symmetry axes, and color palettes.
 */
class PictureInstance {

    /** Number of horizontal grid units. Defaults to 12. */
    var width: Int = 12

    /** Number of vertical grid units. Defaults to 12. */
    var height: Int = 12

    /** Available color palette to sample from. */
    var colors: IntArray = intArrayOf(Color.CYAN, Color.RED)

    /** Maximum number of colors to select simultaneously from [colors]. */
    var maxColors: Int = 2

    /** Geometric shape used for individual tiles. Defaults to [SingleShape.Rectangle]. */
    var singleShape: SingleShape = SingleShape.Rectangle

    /** Symmetry axis for mirroring tiles. Defaults to [Symmetric.NONE]. */
    var symmetric: Symmetric = Symmetric.NONE

    /** Seed string used for deterministic procedural generation. */
    var seed: String = UUID.randomUUID().toString()

    /** Output bitmap width in pixels. Defaults to 512. */
    var outputWidth: Int = 512

    /** Output bitmap height in pixels. Defaults to 512. */
    var outputHeight: Int = 512

    /** Background color for the canvas. Defaults to transparent. */
    var backgroundColor: Int = Color.TRANSPARENT

    private val random = Random()
    private var templateMatrix: ArrayList<ArrayList<PictureUnit>>? = null

    private var pictureUnitWidth: Float = 0f
    private var pictureUnitHeight: Float = 0f

    /**
     * Recomputes the template matrix and renders a new [Bitmap].
     */
    fun createNew(): Bitmap {
        computeTemplateMatrix()
        return computeImage()
    }

    /**
     * Renders the current matrix into a [Bitmap].
     * If the template matrix has not been computed yet, computes it first.
     */
    fun generate(): Bitmap {
        if (templateMatrix == null || templateMatrix!!.size != height ||
            (templateMatrix!!.isNotEmpty() && templateMatrix!![0].size != width)) {
            computeTemplateMatrix()
        }
        return computeImage()
    }

    /**
     * Computes the internal grid matrix according to [seed], [width], and [height].
     */
    fun computeTemplateMatrix() {
        templateMatrix = arrayListOf()
        val safeSeed = if (seed.isEmpty()) "ProfilePictureGenerator" else seed
        val seedHash = safeSeed.hashCode()

        for (y in 0 until height) {
            val row = arrayListOf<PictureUnit>()
            for (x in 0 until width) {
                val seedKey = abs(x * y + 1541 * cos(x * x.toDouble()).toInt()) % safeSeed.length
                val charVal = safeSeed[seedKey].toInt()
                val cellVal = abs((charVal * 37) xor (seedHash + x * 19 + y * 31 + x * y * 7))
                val hasValue = (cellVal % 100) < 52
                row.add(PictureUnit(hasValue, Color.BLACK))
            }
            templateMatrix!!.add(row)
        }
        updateColor()
    }

    /**
     * Updates the colors of the existing matrix tiles without altering the layout pattern.
     */
    fun updateColor() {
        if (templateMatrix == null || templateMatrix!!.size != height ||
            (templateMatrix!!.isNotEmpty() && templateMatrix!![0].size != width)) {
            computeTemplateMatrix()
            return
        }
        if (colors.isEmpty()) {
            return
        }

        val chosenColors = arrayListOf<Int>()
        val reducedColors = colors.toMutableList()
        val numColors = minOf(maxColors, reducedColors.size)
        for (i in 0 until numColors) {
            val idx = random.nextInt(reducedColors.size)
            chosenColors.add(reducedColors.removeAt(idx))
        }

        val safeSeed = if (seed.isEmpty()) "ProfilePictureGenerator" else seed
        val seedHash = safeSeed.hashCode()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val seedKey = abs((y * y * y + 3 * sin(x + y.toDouble()))).toInt()
                val charVal = safeSeed[seedKey % safeSeed.length].toInt()
                val colorKey = abs(charVal * 41 + seedHash + x * 13 + y * 17)
                val color = chosenColors[colorKey % chosenColors.size]
                templateMatrix!![y][x].color = color
            }
        }
    }

    /**
     * Maps coordinate (x, y) on the grid to its mirrored template source coordinate
     * according to the configured [symmetric] mode.
     *
     * - [Symmetric.X]: Horizontal reflection. The right half mirrors the left half.
     * - [Symmetric.Y]: Vertical reflection. The bottom half mirrors the top half.
     * - [Symmetric.REFLECTION]: Both horizontal and vertical reflection (4 quadrants).
     * - [Symmetric.NONE]: Direct mapping with no mirroring.
     */
    fun getMirroredCoordinates(x: Int, y: Int): Pair<Int, Int> {
        val calculatedX = if ((symmetric == Symmetric.X || symmetric == Symmetric.REFLECTION) && x >= width / 2) {
            width - 1 - x
        } else {
            x
        }.coerceIn(0, width - 1)

        val calculatedY = if ((symmetric == Symmetric.Y || symmetric == Symmetric.REFLECTION) && y >= height / 2) {
            height - 1 - y
        } else {
            y
        }.coerceIn(0, height - 1)

        return Pair(calculatedX, calculatedY)
    }

    /**
     * Retrieves the [PictureUnit] for grid point (x, y), applying symmetry mirroring.
     */
    fun getPictureUnitForPoint(x: Int, y: Int): PictureUnit {
        if (templateMatrix == null || templateMatrix!!.size != height ||
            (templateMatrix!!.isNotEmpty() && templateMatrix!![0].size != width)) {
            computeTemplateMatrix()
        }
        val (calcX, calcY) = getMirroredCoordinates(x, y)
        return templateMatrix!![calcY][calcX]
    }

    private fun computeImage(): Bitmap {
        val conf = Bitmap.Config.ARGB_8888
        val bmp = Bitmap.createBitmap(outputWidth, outputHeight, conf)
        val canvas = Canvas(bmp)

        if (backgroundColor != Color.TRANSPARENT) {
            canvas.drawColor(backgroundColor)
        }

        pictureUnitWidth = outputWidth.toFloat() / width
        pictureUnitHeight = outputHeight.toFloat() / height

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pictureUnit = getPictureUnitForPoint(x, y)
                if (!pictureUnit.hasValue) {
                    continue
                }
                paint.color = pictureUnit.color
                drawPictureUnit(canvas, paint, x, y)
            }
        }

        canvas.save()
        return bmp
    }

    private fun drawPictureUnit(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        val left = pictureUnitWidth * x
        val top = pictureUnitHeight * y
        val right = left + pictureUnitWidth
        val bottom = top + pictureUnitHeight

        when (singleShape) {
            SingleShape.Rectangle -> {
                canvas.drawRect(left, top, right, bottom, paint)
            }
            SingleShape.Triangle -> {
                val path = Path()
                path.moveTo(left + pictureUnitWidth / 2f, top)
                path.lineTo(right, bottom)
                path.lineTo(left, bottom)
                path.close()
                canvas.drawPath(path, paint)
            }
            SingleShape.Circle -> {
                val cx = left + pictureUnitWidth / 2f
                val cy = top + pictureUnitHeight / 2f
                val radius = minOf(pictureUnitWidth, pictureUnitHeight) / 2f
                canvas.drawCircle(cx, cy, radius, paint)
            }
        }
    }
}
