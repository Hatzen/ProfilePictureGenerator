package de.hartz.software.profilepicturegenerator

/**
 * Defines the geometric unit shape used to draw each cell in the identicon.
 */
enum class SingleShape {
    /**
     * Classic square/rectangular pixel blocks (retro 8-bit / GitHub style).
     */
    Rectangle,

    /**
     * Geometric triangle tiles with vertical axis symmetry.
     */
    Triangle,

    /**
     * Modern circular dot-matrix tiles.
     */
    Circle
}