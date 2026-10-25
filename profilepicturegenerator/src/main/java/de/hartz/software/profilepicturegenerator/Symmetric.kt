package de.hartz.software.profilepicturegenerator

/**
 * Defines the symmetry axis for generating identicons and profile avatars.
 */
enum class Symmetric {
    /**
     * Horizontal symmetry across the vertical center line (left half mirrors onto right half).
     * This produces classic GitHub / GitLab style identicons.
     */
    X,

    /**
     * Vertical symmetry across the horizontal center line (top half mirrors onto bottom half).
     */
    Y,

    /**
     * Dual-axis reflection across both horizontal and vertical center lines.
     * Produces 4-quadrant symmetric kaleidoscopic patterns.
     */
    REFLECTION,

    /**
     * No symmetry. All cells on the grid are randomized independently.
     */
    NONE
}