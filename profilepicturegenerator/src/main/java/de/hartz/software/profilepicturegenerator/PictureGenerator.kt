package de.hartz.software.profilepicturegenerator

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Entry point and factory object for configuring and generating profile pictures.
 */
object PictureGenerator {

    /**
     * Generates a sample profile picture bitmap using default settings.
     */
    fun generateSample(): Bitmap {
        val instance = PictureInstance()
        instance.width = 14
        instance.height = 14
        instance.colors = intArrayOf(Color.GREEN, Color.LTGRAY)
        instance.singleShape = SingleShape.Rectangle
        instance.symmetric = Symmetric.NONE
        return instance.createNew()
    }

    /**
     * Creates and returns a configured [PictureInstance].
     */
    fun generate(
        width: Int = 12,
        height: Int = 12,
        colors: IntArray = intArrayOf(Color.CYAN, Color.RED),
        maxColors: Int = 2,
        shape: SingleShape = SingleShape.Rectangle,
        symmetric: Symmetric = Symmetric.NONE
    ): PictureInstance {
        val instance = PictureInstance()
        instance.width = width
        instance.height = height
        instance.colors = colors
        instance.maxColors = maxColors
        instance.singleShape = shape
        instance.symmetric = symmetric
        return instance
    }

    /**
     * Creates and returns a configured [PictureInstance] with a custom seed.
     */
    fun generate(
        width: Int,
        height: Int,
        colors: IntArray,
        maxColors: Int,
        shape: SingleShape,
        symmetric: Symmetric,
        seed: String
    ): PictureInstance {
        val instance = generate(width, height, colors, maxColors, shape, symmetric)
        instance.seed = seed
        return instance
    }

    /**
     * Convenient one-shot method to generate a profile picture [Bitmap] in a single call.
     */
    fun create(
        width: Int = 5,
        height: Int = 5,
        colors: IntArray = intArrayOf(Color.rgb(33, 150, 243), Color.rgb(233, 30, 99)),
        maxColors: Int = 2,
        shape: SingleShape = SingleShape.Rectangle,
        symmetric: Symmetric = Symmetric.X,
        seed: String? = null,
        outputSize: Int = 512
    ): Bitmap {
        val instance = generate(width, height, colors, maxColors, shape, symmetric)
        if (seed != null) {
            instance.seed = seed
        }
        instance.outputWidth = outputSize
        instance.outputHeight = outputSize
        return instance.createNew()
    }
}