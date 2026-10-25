package de.hartz.software.profilepicturegeneratorsample

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import de.hartz.software.profilepicturegenerator.PictureGenerator
import de.hartz.software.profilepicturegenerator.PictureInstance
import de.hartz.software.profilepicturegenerator.SingleShape
import de.hartz.software.profilepicturegenerator.Symmetric
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    var width = 5
    var height = 5
    var colors = intArrayOf(
        Color.rgb(30, 136, 229),
        Color.rgb(233, 30, 99),
        Color.rgb(0, 150, 136)
    )
    var shape = SingleShape.Rectangle
    var symmetric = Symmetric.X

    private lateinit var instance: PictureInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instance = PictureGenerator.generate(width, height, colors, 2, shape, symmetric)
        handleIntent(intent)

        findViewById<Button>(R.id.symmetric).apply {
            text = "Symmetric: ${symmetric.name}"
            setOnClickListener {
                symmetric = when (symmetric) {
                    Symmetric.X -> Symmetric.Y
                    Symmetric.Y -> Symmetric.REFLECTION
                    Symmetric.REFLECTION -> Symmetric.NONE
                    Symmetric.NONE -> Symmetric.X
                }
                text = "Symmetric: ${symmetric.name}"
                refreshImage()
            }
        }

        findViewById<Button>(R.id.shape).apply {
            text = "Shape: ${shape.name}"
            setOnClickListener {
                shape = when (shape) {
                    SingleShape.Rectangle -> SingleShape.Circle
                    SingleShape.Circle -> SingleShape.Triangle
                    SingleShape.Triangle -> SingleShape.Rectangle
                }
                text = "Shape: ${shape.name}"
                refreshImage()
            }
        }

        findViewById<Button>(R.id.color).apply {
            text = "Randomize Colors"
            setOnClickListener {
                colors = intArrayOf(getRandomColor(), getRandomColor(), getRandomColor())
                instance.colors = colors
                instance.updateColor()
                findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.generate())
            }
        }

        findViewById<Button>(R.id.generate).setOnClickListener {
            createNew()
        }

        refreshImage()

        // Export real profiles to external storage if requested
        exportRealAvatars()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
        refreshImage()
    }

    private fun handleIntent(intent: Intent?) {
        intent?.getStringExtra("shape")?.let {
            shape = when (it.toLowerCase(Locale.ROOT)) {
                "circle" -> SingleShape.Circle
                "triangle" -> SingleShape.Triangle
                else -> SingleShape.Rectangle
            }
            findViewById<Button>(R.id.shape)?.text = "Shape: ${shape.name}"
        }
        intent?.getStringExtra("symmetric")?.let {
            symmetric = when (it.toUpperCase(Locale.ROOT)) {
                "Y" -> Symmetric.Y
                "REFLECTION" -> Symmetric.REFLECTION
                "NONE" -> Symmetric.NONE
                else -> Symmetric.X
            }
            findViewById<Button>(R.id.symmetric)?.text = "Symmetric: ${symmetric.name}"
        }
        intent?.getStringExtra("seed")?.let {
            instance.seed = it
        }
    }

    private fun createNew() {
        instance.width = width
        instance.height = height
        instance.colors = colors
        instance.singleShape = shape
        instance.symmetric = symmetric
        instance.seed = UUID.randomUUID().toString()
        findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.createNew())
    }

    private fun refreshImage() {
        instance.width = width
        instance.height = height
        instance.colors = colors
        instance.singleShape = shape
        instance.symmetric = symmetric
        findViewById<ImageView>(R.id.generatedImageView).setImageBitmap(instance.generate())
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    private fun exportRealAvatars() {
        try {
            val dir = File(getExternalFilesDir(null), "generated_profiles")
            dir.mkdirs()

            data class ProfilePreset(
                val filename: String,
                val label: String,
                val width: Int,
                val height: Int,
                val colors: IntArray,
                val shape: SingleShape,
                val symmetric: Symmetric,
                val seed: String
            )

            val presets = listOf(
                ProfilePreset("avatar_01_github_rect_x.png", "GitHub 5x5 (X)", 5, 5, intArrayOf(Color.rgb(25, 118, 210), Color.rgb(13, 71, 161)), SingleShape.Rectangle, Symmetric.X, "octocat@github.com"),
                ProfilePreset("avatar_02_gitlab_rect_x.png", "GitLab 6x6 (X)", 6, 6, intArrayOf(Color.rgb(230, 74, 25), Color.rgb(245, 124, 0)), SingleShape.Rectangle, Symmetric.X, "gitlab_contributor"),
                ProfilePreset("avatar_03_dots_blue_x.png", "Dots 8x8 (X)", 8, 8, intArrayOf(Color.rgb(0, 151, 167), Color.rgb(0, 131, 143)), SingleShape.Circle, Symmetric.X, "sarah_connor"),
                ProfilePreset("avatar_04_dots_purple_reflection.png", "Dots 8x8 (Refl)", 8, 8, intArrayOf(Color.rgb(123, 31, 162), Color.rgb(233, 30, 99)), SingleShape.Circle, Symmetric.REFLECTION, "cyberpunk_2077"),
                ProfilePreset("avatar_05_triangle_neon_x.png", "Triangles 7x7 (X)", 7, 7, intArrayOf(Color.rgb(0, 191, 165), Color.rgb(100, 255, 218)), SingleShape.Triangle, Symmetric.X, "vector_geometry"),
                ProfilePreset("avatar_06_triangle_amber_reflection.png", "Triangles 7x7 (Refl)", 7, 7, intArrayOf(Color.rgb(255, 143, 0), Color.rgb(255, 213, 79)), SingleShape.Triangle, Symmetric.REFLECTION, "golden_ratio"),
                ProfilePreset("avatar_07_mandala_reflection.png", "Mandala 6x6 (Refl)", 6, 6, intArrayOf(Color.rgb(48, 63, 159), Color.rgb(124, 77, 255)), SingleShape.Rectangle, Symmetric.REFLECTION, "kaleidoscope"),
                ProfilePreset("avatar_08_totem_y.png", "Totem 6x6 (Y)", 6, 6, intArrayOf(Color.rgb(56, 142, 60), Color.rgb(129, 199, 132)), SingleShape.Rectangle, Symmetric.Y, "vertical_totem"),
                ProfilePreset("avatar_09_mosaic_none.png", "Mosaic 8x8 (None)", 8, 8, intArrayOf(Color.rgb(211, 47, 47), Color.rgb(255, 82, 82)), SingleShape.Rectangle, Symmetric.NONE, "retro_arcade"),
                ProfilePreset("avatar_10_dots_pastel_x.png", "Pastel 10x10 (X)", 10, 10, intArrayOf(Color.rgb(255, 64, 129), Color.rgb(83, 109, 254)), SingleShape.Circle, Symmetric.X, "pastel_dream"),
                ProfilePreset("avatar_11_triangle_coral_reflection.png", "Coral 8x8 (Refl)", 8, 8, intArrayOf(Color.rgb(255, 110, 64), Color.rgb(255, 171, 64)), SingleShape.Triangle, Symmetric.REFLECTION, "coral_reef"),
                ProfilePreset("avatar_12_emerald_rect_x.png", "Emerald 5x5 (X)", 5, 5, intArrayOf(Color.rgb(0, 121, 107), Color.rgb(38, 166, 154)), SingleShape.Rectangle, Symmetric.X, "emerald_city")
            )

            val generatedBitmaps = mutableListOf<Bitmap>()
            for (p in presets) {
                val gen = PictureGenerator.generate(p.width, p.height, p.colors, p.colors.size, p.shape, p.symmetric, p.seed)
                gen.outputWidth = 400
                gen.outputHeight = 400
                gen.backgroundColor = Color.WHITE
                val bmp = gen.createNew()
                val outFile = File(dir, p.filename)
                FileOutputStream(outFile).use { fos ->
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
                generatedBitmaps.add(bmp)
            }

            // --- 1. Composite Showcase Banner (1200 x 980) ---
            val bannerBmp = Bitmap.createBitmap(1200, 980, Bitmap.Config.ARGB_8888)
            val bannerCanvas = Canvas(bannerBmp)
            bannerCanvas.drawColor(Color.rgb(18, 24, 38)) // dark slate background

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 40f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(148, 163, 184)
                textSize = 20f
                textAlign = Paint.Align.CENTER
            }
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(203, 213, 225)
                textSize = 15f
                textAlign = Paint.Align.CENTER
            }

            bannerCanvas.drawText("ProfilePictureGenerator Showcase", 600f, 60f, titlePaint)
            bannerCanvas.drawText("100% Real Generated Avatars • Procedural • Deterministic • Multi-Axis Symmetry", 600f, 98f, subtitlePaint)

            val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }

            val itemSize = 210
            val padX = 70
            val padY = 55
            val startY = 135

            for (i in 0 until 12) {
                val row = i / 4
                val col = i % 4
                val x = padX + col * (itemSize + 72)
                val y = startY + row * (itemSize + padY)

                bannerCanvas.drawRect(x.toFloat(), y.toFloat(), (x + itemSize).toFloat(), (y + itemSize).toFloat(), cardPaint)
                bannerCanvas.drawBitmap(Bitmap.createScaledBitmap(generatedBitmaps[i], itemSize, itemSize, true), x.toFloat(), y.toFloat(), null)
                bannerCanvas.drawText(presets[i].label, (x + itemSize / 2).toFloat(), (y + itemSize + 24).toFloat(), labelPaint)
            }

            FileOutputStream(File(dir, "real_showcase_banner.png")).use { fos ->
                bannerBmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            // --- 2. Symmetry Modes Comparison Banner (1200 x 420) ---
            val symBmp = Bitmap.createBitmap(1200, 420, Bitmap.Config.ARGB_8888)
            val symCanvas = Canvas(symBmp)
            symCanvas.drawColor(Color.rgb(18, 24, 38))

            symCanvas.drawText("Symmetry Modes Comparison (Same Seed)", 600f, 50f, titlePaint)
            symCanvas.drawText("Identical procedural seed rendered across all 4 symmetry modes", 600f, 85f, subtitlePaint)

            val symModes = listOf(
                Pair(Symmetric.NONE, "1. NONE (Asymmetric)"),
                Pair(Symmetric.X, "2. X-AXIS (Horizontal Mirror)"),
                Pair(Symmetric.Y, "3. Y-AXIS (Vertical Mirror)"),
                Pair(Symmetric.REFLECTION, "4. REFLECTION (Dual-Axis)")
            )

            val symColors = intArrayOf(Color.rgb(30, 136, 229), Color.rgb(233, 30, 99))
            val symCardSize = 220
            val symPadX = 64
            val symSpacing = 68
            val symY = 125

            for ((idx, mode) in symModes.withIndex()) {
                val gen = PictureGenerator.generate(6, 6, symColors, 2, SingleShape.Rectangle, mode.first, "identicon-seed-2026")
                gen.outputWidth = 300
                gen.outputHeight = 300
                gen.backgroundColor = Color.WHITE
                val bmp = gen.createNew()

                val x = symPadX + idx * (symCardSize + symSpacing)
                symCanvas.drawRect(x.toFloat(), symY.toFloat(), (x + symCardSize).toFloat(), (symY + symCardSize).toFloat(), cardPaint)
                symCanvas.drawBitmap(Bitmap.createScaledBitmap(bmp, symCardSize, symCardSize, true), x.toFloat(), symY.toFloat(), null)
                symCanvas.drawText(mode.second, (x + symCardSize / 2).toFloat(), (symY + symCardSize + 30).toFloat(), labelPaint)

                // Save individual file as well
                FileOutputStream(File(dir, "compare_sym_${mode.first.name.toLowerCase(Locale.ROOT)}.png")).use { fos ->
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
            }

            FileOutputStream(File(dir, "real_symmetry_comparison.png")).use { fos ->
                symBmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            // --- 3. Geometric Shapes Comparison Banner (1000 x 420) ---
            val shapeBmp = Bitmap.createBitmap(1000, 420, Bitmap.Config.ARGB_8888)
            val shapeCanvas = Canvas(shapeBmp)
            shapeCanvas.drawColor(Color.rgb(18, 24, 38))

            shapeCanvas.drawText("Tile Shape Styles Comparison (Same Seed)", 500f, 50f, titlePaint)
            shapeCanvas.drawText("Identical seed & palette rendered with Rectangle, Circle, and Triangle", 500f, 85f, subtitlePaint)

            val shapeModes = listOf(
                Pair(SingleShape.Rectangle, "Rectangle (8-Bit Retro)"),
                Pair(SingleShape.Circle, "Circle (Dot-Matrix)"),
                Pair(SingleShape.Triangle, "Triangle (Geometric)")
            )

            val shapeColors = intArrayOf(Color.rgb(0, 150, 136), Color.rgb(255, 152, 0))
            val shapeCardSize = 240
            val shapePadX = 70
            val shapeSpacing = 70
            val shapeY = 125

            for ((idx, s) in shapeModes.withIndex()) {
                val gen = PictureGenerator.generate(6, 6, shapeColors, 2, s.first, Symmetric.X, "avatar-shape-demo")
                gen.outputWidth = 300
                gen.outputHeight = 300
                gen.backgroundColor = Color.WHITE
                val bmp = gen.createNew()

                val x = shapePadX + idx * (shapeCardSize + shapeSpacing)
                shapeCanvas.drawRect(x.toFloat(), shapeY.toFloat(), (x + shapeCardSize).toFloat(), (shapeY + shapeCardSize).toFloat(), cardPaint)
                shapeCanvas.drawBitmap(Bitmap.createScaledBitmap(bmp, shapeCardSize, shapeCardSize, true), x.toFloat(), shapeY.toFloat(), null)
                shapeCanvas.drawText(s.second, (x + shapeCardSize / 2).toFloat(), (shapeY + shapeCardSize + 30).toFloat(), labelPaint)

                // Save individual file as well
                FileOutputStream(File(dir, "compare_shape_${s.first.name.toLowerCase(Locale.ROOT)}.png")).use { fos ->
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
            }

            FileOutputStream(File(dir, "real_shapes_comparison.png")).use { fos ->
                shapeBmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            Log.i("ProfileGen", "Exported real profile avatars and composite banners to ${dir.absolutePath}")
        } catch (e: Exception) {
            Log.e("ProfileGen", "Error exporting real avatars", e)
        }
    }
}
