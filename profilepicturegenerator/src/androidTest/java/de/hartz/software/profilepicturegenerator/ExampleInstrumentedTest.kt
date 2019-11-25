package de.hartz.software.profilepicturegenerator

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("de.hartz.software.profilepicturegenerator.test", appContext.packageName)
    }


    @Test
    fun createNew_withRealisticParameters_wontCrash() {
        val width = 6
        val height = 6

        val color1 = -16742021
        val color2 = -8875876
        val colors = intArrayOf(color1, color2)
        val shape = SingleShape.Rectangle
        var seed = "#???..................................................................................???#"
        val symmetric = Symmetric.NONE

        val instance = PictureGenerator.generate(width, height, colors, 2, shape, symmetric)
        instance.colors = colors
        instance.singleShape = shape
        instance.symmetric = symmetric
        instance.seed = seed

        instance.createNew()
    }
}
