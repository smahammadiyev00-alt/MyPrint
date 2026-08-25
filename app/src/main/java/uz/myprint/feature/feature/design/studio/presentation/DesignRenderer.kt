package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.ImageLayer
import uz.myprint.feature.feature.design.studio.domain.ShapeKind
import uz.myprint.feature.feature.design.studio.domain.ShapeLayer
import uz.myprint.feature.feature.design.studio.domain.TextAlign
import uz.myprint.feature.feature.design.studio.domain.TextLayer
import androidx.compose.ui.text.style.TextAlign as ComposeTextAlign

/**
 * Maketni chizadigan YAGONA funksiya.
 *
 * Ekran ham, 300 DPI eksport ham shuni chaqiradi — farqi faqat
 * geometry.pxPerMm da. Shu sababli ekranda ko'ringan narsa
 * bosmadan aynan shunday chiqadi.
 */
fun DrawScope.drawDocument(
    document: DesignDocument,
    geometry: CanvasGeometry,
    textMeasurer: TextMeasurer
) {

    // Fon bleed maydonini ham qoplaydi, aks holda kesishda
    // chetda oq chiziq qolib ketadi.
    drawRect(
        color = document.background,
        topLeft = geometry.originPx,
        size = geometry.fullSizePx
    )

    document.layers
        .filter { it.isVisible }
        .forEach { layer ->
            drawLayer(layer, geometry, textMeasurer)
        }
}

private fun DrawScope.drawLayer(
    layer: DesignLayer,
    geometry: CanvasGeometry,
    textMeasurer: TextMeasurer
) {

    val topLeft = geometry.layerTopLeftPx(layer)

    val size = geometry.layerSizePx(layer)

    val center = geometry.layerCenterPx(layer)

    rotate(degrees = layer.transform.rotationDeg, pivot = center) {

        when (layer) {

            is ShapeLayer -> drawShapeLayer(layer, topLeft, size, geometry)

            is TextLayer -> drawTextLayer(
                layer, topLeft, size, geometry, textMeasurer
            )

            // Rasm qatlami bitmap yuklashni talab qiladi, u alohida
            // bosqichda qo'shiladi. Hozircha joyi belgilanadi.
            is ImageLayer -> drawRect(
                color = Color(0xFFE5E7EB),
                topLeft = topLeft,
                size = size,
                alpha = layer.transform.opacity
            )
        }
    }
}

private fun DrawScope.drawShapeLayer(
    layer: ShapeLayer,
    topLeft: Offset,
    size: Size,
    geometry: CanvasGeometry
) {

    val alpha = layer.transform.opacity

    val strokePx = geometry.mmToPx(layer.strokeWidthMm)

    when (layer.kind) {

        ShapeKind.RECTANGLE -> {

            val radius = CornerRadius(
                geometry.mmToPx(layer.cornerRadiusMm)
            )

            layer.fill?.let {
                drawRoundRect(
                    color = it,
                    topLeft = topLeft,
                    size = size,
                    cornerRadius = radius,
                    alpha = alpha
                )
            }

            layer.strokeColor?.let {
                drawRoundRect(
                    color = it,
                    topLeft = topLeft,
                    size = size,
                    cornerRadius = radius,
                    alpha = alpha,
                    style = Stroke(width = strokePx)
                )
            }
        }

        ShapeKind.ELLIPSE -> {

            layer.fill?.let {
                drawOval(
                    color = it,
                    topLeft = topLeft,
                    size = size,
                    alpha = alpha
                )
            }

            layer.strokeColor?.let {
                drawOval(
                    color = it,
                    topLeft = topLeft,
                    size = size,
                    alpha = alpha,
                    style = Stroke(width = strokePx)
                )
            }
        }

        ShapeKind.LINE -> {

            val color = layer.strokeColor ?: layer.fill ?: Color.Black

            drawLine(
                color = color,
                start = Offset(topLeft.x, topLeft.y + size.height / 2f),
                end = Offset(topLeft.x + size.width, topLeft.y + size.height / 2f),
                strokeWidth = strokePx,
                alpha = alpha
            )
        }
    }
}

private fun DrawScope.drawTextLayer(
    layer: TextLayer,
    topLeft: Offset,
    size: Size,
    geometry: CanvasGeometry,
    textMeasurer: TextMeasurer
) {

    val fontSizePx = geometry.mmToPx(layer.fontSizeMm)

    val style = TextStyle(
        color = layer.color,
        fontSize = fontSizePx.toSp(),
        fontWeight = if (layer.isBold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (layer.isItalic) FontStyle.Italic else FontStyle.Normal,
        lineHeight = (fontSizePx * layer.lineHeightMultiplier).toSp(),
        letterSpacing = geometry.mmToPx(layer.letterSpacingMm).toSp(),
        textAlign = when (layer.align) {
            TextAlign.START -> ComposeTextAlign.Start
            TextAlign.CENTER -> ComposeTextAlign.Center
            TextAlign.END -> ComposeTextAlign.End
        }
    )

    val result = textMeasurer.measure(
        text = layer.text,
        style = style,
        constraints = Constraints(
            maxWidth = size.width.toInt().coerceAtLeast(1)
        )
    )

    drawText(
        textLayoutResult = result,
        topLeft = topLeft,
        alpha = layer.transform.opacity
    )
}
