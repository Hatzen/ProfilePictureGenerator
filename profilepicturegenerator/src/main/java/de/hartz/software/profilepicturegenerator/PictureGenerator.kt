package de.hartz.software.profilepicturegenerator

import android.graphics.Bitmap
import android.graphics.Color

object PictureGenerator {

    // TODO: Make shapes
    // https://blog.stylingandroid.com/irregular-shapes-part-4/
    // https://proandroiddev.com/creating-and-publishing-an-android-library-402976b5d9d2+
    // https://developer.android.com/studio/projects/android-library

    // https://github.com/stfalcon-studio/ChatKit/blob/master/chatkit/src/main/res/drawable-xxxhdpi/mask.png
    // https://stackoverflow.com/questions/41343305/custom-imageview-with-a-custom-shape

    fun generateSample() : Bitmap {
        val instance = PictureInstance()
        instance.width = 14
        instance.height = 14
        instance.colors = intArrayOf(Color.GREEN, Color.LTGRAY)
        instance.singleShape = SingleShape.Rectangle
        instance.symmetric = Symmetric.NONE
        return instance.generate()
    }

    fun generate(width: Int, height:Int, colors: IntArray, maxColors: Int, shape: SingleShape, symmetric: Symmetric) : PictureInstance {
        val instance = PictureInstance()
        instance.width = width
        instance.height = height
        instance.colors = colors
        instance.maxColors = maxColors
        instance.singleShape = shape
        instance.symmetric = symmetric
        return instance
    }


}