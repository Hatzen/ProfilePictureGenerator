package de.hartz.software.profilepicturegenerator

import org.junit.Assert.*
import org.junit.Test

class SymmetricTest {

    @Test
    fun testSymmetricX_oddDimensions() {
        val instance = PictureInstance()
        instance.width = 5
        instance.height = 5
        instance.symmetric = Symmetric.X

        // For width = 5, center is at x = 2.
        // Columns should map to: 0 -> 0, 1 -> 1, 2 -> 2, 3 -> 1, 4 -> 0
        val expectedX = listOf(0, 1, 2, 1, 0)
        for (y in 0 until 5) {
            for (x in 0 until 5) {
                val (mappedX, mappedY) = instance.getMirroredCoordinates(x, y)
                assertEquals("mappedX at x=$x, y=$y", expectedX[x], mappedX)
                assertEquals("mappedY at x=$x, y=$y", y, mappedY)
            }
        }
    }

    @Test
    fun testSymmetricX_evenDimensions() {
        val instance = PictureInstance()
        instance.width = 6
        instance.height = 6
        instance.symmetric = Symmetric.X

        // For width = 6, mid is 3.
        // Columns should map to: 0 -> 0, 1 -> 1, 2 -> 2, 3 -> 2, 4 -> 1, 5 -> 0
        val expectedX = listOf(0, 1, 2, 2, 1, 0)
        for (y in 0 until 6) {
            for (x in 0 until 6) {
                val (mappedX, mappedY) = instance.getMirroredCoordinates(x, y)
                assertEquals("mappedX at x=$x, y=$y", expectedX[x], mappedX)
                assertEquals("mappedY at x=$x, y=$y", y, mappedY)
            }
        }
    }

    @Test
    fun testSymmetricY_oddDimensions() {
        val instance = PictureInstance()
        instance.width = 5
        instance.height = 5
        instance.symmetric = Symmetric.Y

        // For height = 5, center is at y = 2.
        // Rows should map to: 0 -> 0, 1 -> 1, 2 -> 2, 3 -> 1, 4 -> 0
        val expectedY = listOf(0, 1, 2, 1, 0)
        for (y in 0 until 5) {
            for (x in 0 until 5) {
                val (mappedX, mappedY) = instance.getMirroredCoordinates(x, y)
                assertEquals("mappedX at x=$x, y=$y", x, mappedX)
                assertEquals("mappedY at x=$x, y=$y", expectedY[y], mappedY)
            }
        }
    }

    @Test
    fun testSymmetricY_evenDimensions() {
        val instance = PictureInstance()
        instance.width = 6
        instance.height = 6
        instance.symmetric = Symmetric.Y

        // For height = 6, mid is 3.
        // Rows should map to: 0 -> 0, 1 -> 1, 2 -> 2, 3 -> 2, 4 -> 1, 5 -> 0
        val expectedY = listOf(0, 1, 2, 2, 1, 0)
        for (y in 0 until 6) {
            for (x in 0 until 6) {
                val (mappedX, mappedY) = instance.getMirroredCoordinates(x, y)
                assertEquals("mappedX at x=$x, y=$y", x, mappedX)
                assertEquals("mappedY at x=$x, y=$y", expectedY[y], mappedY)
            }
        }
    }

    @Test
    fun testSymmetricReflection_quadrants() {
        val instance = PictureInstance()
        instance.width = 5
        instance.height = 5
        instance.symmetric = Symmetric.REFLECTION

        val expected = listOf(0, 1, 2, 1, 0)
        for (y in 0 until 5) {
            for (x in 0 until 5) {
                val (mappedX, mappedY) = instance.getMirroredCoordinates(x, y)
                assertEquals(expected[x], mappedX)
                assertEquals(expected[y], mappedY)
            }
        }
    }

    @Test
    fun testSymmetricNone_identity() {
        val instance = PictureInstance()
        instance.width = 8
        instance.height = 8
        instance.symmetric = Symmetric.NONE

        for (y in 0 until 8) {
            for (x in 0 until 8) {
                val (mappedX, mappedY) = instance.getMirroredCoordinates(x, y)
                assertEquals(x, mappedX)
                assertEquals(y, mappedY)
            }
        }
    }

    @Test
    fun testCoordinateBoundsSafety() {
        val instance = PictureInstance()
        instance.width = 10
        instance.height = 10
        instance.symmetric = Symmetric.REFLECTION

        // Negative values or exceeding bounds should be coerced safely
        val (safeX1, safeY1) = instance.getMirroredCoordinates(-5, -3)
        assertEquals(0, safeX1)
        assertEquals(0, safeY1)

        val (safeX2, safeY2) = instance.getMirroredCoordinates(100, 200)
        assertTrue(safeX2 in 0 until 10)
        assertTrue(safeY2 in 0 until 10)
    }

    @Test
    fun testUpdateColorDoesNotAccumulateExtraRows() {
        val instance = PictureInstance()
        instance.width = 6
        instance.height = 6
        instance.colors = intArrayOf(-16777216, -65536, -16711936)
        instance.maxColors = 2
        instance.computeTemplateMatrix()

        // Call updateColor multiple times
        for (i in 0 until 5) {
            instance.updateColor()
        }

        // All points in grid should be safely accessible without out of bounds
        for (y in 0 until 6) {
            for (x in 0 until 6) {
                val unit = instance.getPictureUnitForPoint(x, y)
                assertNotNull(unit)
            }
        }
    }

    @Test
    fun testUnitsAreSymmetricInGrid() {
        val instance = PictureInstance()
        instance.width = 5
        instance.height = 5
        instance.symmetric = Symmetric.X
        instance.computeTemplateMatrix()

        for (y in 0 until 5) {
            val leftOuter = instance.getPictureUnitForPoint(0, y)
            val rightOuter = instance.getPictureUnitForPoint(4, y)
            assertEquals("Outer column hasValue symmetry at y=$y", leftOuter.hasValue, rightOuter.hasValue)
            assertEquals("Outer column color symmetry at y=$y", leftOuter.color, rightOuter.color)

            val leftInner = instance.getPictureUnitForPoint(1, y)
            val rightInner = instance.getPictureUnitForPoint(3, y)
            assertEquals("Inner column hasValue symmetry at y=$y", leftInner.hasValue, rightInner.hasValue)
            assertEquals("Inner column color symmetry at y=$y", leftInner.color, rightInner.color)
        }
    }
}
