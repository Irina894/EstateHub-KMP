package ua.edu.chnu.estatehub.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import ua.edu.chnu.estatehub.domain.model.ListingCategory

/**
 * Ілюстрація-заглушка замість фото (фото з'являться з API). Малюється векторно,
 * тому однаково працює на Android і Desktop.
 */
@Composable
fun ListingArt(category: ListingCategory, seed: Long, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val sky = if (seed % 2L == 0L) scheme.primaryContainer else scheme.secondaryContainer
    val ground = scheme.surfaceVariant
    val main = scheme.primary
    val accent = scheme.secondary
    val light = scheme.onPrimary

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(brush = Brush.verticalGradient(listOf(sky, ground)))
        drawCircle(color = accent.copy(alpha = 0.85f), radius = h * 0.11f, center = Offset(w * 0.82f, h * 0.24f))

        when (category) {
            ListingCategory.APARTMENT -> {
                val bx = w * 0.30f
                val bw = w * 0.40f
                val by = h * 0.16f
                drawRect(color = main, topLeft = Offset(bx, by), size = Size(bw, h - by))
                val cols = 4
                val rows = 5
                val cw = bw / (cols + 1)
                val ch = (h - by) / (rows + 1)
                for (r in 1..rows) for (c in 1..cols) {
                    drawRect(
                        color = light.copy(alpha = if ((r + c + seed.toInt()) % 3 == 0) 0.85f else 0.35f),
                        topLeft = Offset(bx + cw * c - cw * 0.3f, by + ch * r - ch * 0.25f),
                        size = Size(cw * 0.6f, ch * 0.5f),
                    )
                }
                drawRect(color = main.copy(alpha = 0.45f), topLeft = Offset(w * 0.12f, h * 0.46f), size = Size(w * 0.16f, h * 0.54f))
                drawRect(color = main.copy(alpha = 0.45f), topLeft = Offset(w * 0.72f, h * 0.38f), size = Size(w * 0.18f, h * 0.62f))
            }
            ListingCategory.HOUSE -> {
                val hx = w * 0.26f
                val hw = w * 0.48f
                val top = h * 0.50f
                drawRect(color = main, topLeft = Offset(hx, top), size = Size(hw, h - top))
                val roof = Path().apply {
                    moveTo(hx - w * 0.04f, top)
                    lineTo(hx + hw / 2f, h * 0.20f)
                    lineTo(hx + hw + w * 0.04f, top)
                    close()
                }
                drawPath(path = roof, color = accent)
                drawRect(color = light.copy(alpha = 0.85f), topLeft = Offset(hx + hw * 0.42f, h * 0.68f), size = Size(hw * 0.16f, h * 0.32f))
                drawRect(color = light.copy(alpha = 0.5f), topLeft = Offset(hx + hw * 0.12f, h * 0.62f), size = Size(hw * 0.2f, h * 0.14f))
                drawRect(color = light.copy(alpha = 0.5f), topLeft = Offset(hx + hw * 0.68f, h * 0.62f), size = Size(hw * 0.2f, h * 0.14f))
            }
            ListingCategory.LAND -> {
                val hill1 = Path().apply {
                    moveTo(0f, h)
                    lineTo(0f, h * 0.62f)
                    quadraticTo(w * 0.30f, h * 0.38f, w * 0.60f, h * 0.64f)
                    quadraticTo(w * 0.80f, h * 0.78f, w, h * 0.58f)
                    lineTo(w, h)
                    close()
                }
                drawPath(path = hill1, color = main.copy(alpha = 0.75f))
                val hill2 = Path().apply {
                    moveTo(0f, h)
                    lineTo(0f, h * 0.82f)
                    quadraticTo(w * 0.45f, h * 0.62f, w, h * 0.86f)
                    lineTo(w, h)
                    close()
                }
                drawPath(path = hill2, color = main)
                for (i in 0..6) {
                    val x = w * (0.10f + i * 0.13f)
                    drawRect(color = light.copy(alpha = 0.7f), topLeft = Offset(x, h * 0.80f), size = Size(w * 0.012f, h * 0.14f))
                }
            }
        }
    }
}
