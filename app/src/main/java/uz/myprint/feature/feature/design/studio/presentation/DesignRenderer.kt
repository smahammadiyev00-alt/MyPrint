package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.data.DesignFonts
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
    textMeasurer: TextMeasurer,

    /**
     * Matn harflar KONTURI sifatida chizilsinmi.
     *
     * PDF eksportida majburiy. Sabab: Android'ning PDF chizuvchisi
     * (Skia) matnni Type3 shrift qilib yozadi va uning FontMatrix
     * qiymatida y o'qi manfiy bo'ladi. PDF standarti buni ruxsat
     * beradi, Chrome, Acrobat va Photoshop to'g'ri o'qiydi —
     * lekin CorelDRAW noto'g'ri qayta ishlaydi va harflarni ulkan
     * qilib, ag'darib chizadi. Corel esa O'zbekistondagi
     * bosmaxonalarning asosiy dasturi.
     *
     * Kontur chizilganda PDF ichida shrift obyekti UMUMAN
     * bo'lmaydi — na Type3, na boshqasi. Corel oddiy vektor
     * shakllarni ko'radi va ularni hech qachon xato ochmaydi.
     *
     * Ekranda esa odatdagi matn chizish qoladi: u tezroq va
     * ekranda Type3 muammosi yo'q.
     */
    textAsPaths: Boolean = false,

    /**
     * Yuklangan rasmlar: ImageLayer.sourceUri → bitmap.
     *
     * Chizish funksiyasi fayldan o'qimaydi. Sabab: chizish har
     * kadrda takrorlanadi, diskdan o'qish esa sekin — sakrash
     * boshlanardi. Yuklashni chaqiruvchi tomon oldindan bajaradi.
     */
    images: Map<String, ImageBitmap> = emptyMap()
) {

    // Fon bleed maydonini ham qoplaydi, aks holda kesishda
    // chetda oq chiziq qolib ketadi.
    drawRect(
        color = document.background,
        topLeft = geometry.originPx,
        size = geometry.fullSizePx
    )

    // Qirqish tartibi muhim: avval nishon chiziladi, keyin uning
    // ichidagi qatlamlar nishon shakli bilan cheklangan holda
    // ustiga qo'yiladi. Shuning uchun oddiy forEach yetmaydi —
    // har bir qatlamdan keyin uning "bolalari" tekshiriladi.
    //
    // clipToId to'ldirilgan qatlamlar bu tsiklda o'tkazib
    // yuboriladi: ular o'z nishoni bilan birga chiziladi.
    document.layers
        .filter { it.isVisible && it.clipToId == null }
        .forEach { layer ->

            drawLayer(layer, geometry, textMeasurer, textAsPaths, images)

            val children = document
                .clippedChildren(layer.id)
                .filter { it.isVisible }

            if (children.isNotEmpty()) {
                drawClipped(layer, children, geometry, textMeasurer, textAsPaths, images)
            }
        }
}

/**
 * Qatlamlarni nishon shakli ichida chizadi.
 *
 * Photoshop'dagi "clipping mask" ning soddalashtirilgani.
 * To'liq alpha-maska emas: nishonning KONTURI ishlatiladi, uning
 * shaffofligi yoki gradienti emas. Bu ataylab shunday — to'liq
 * maska har kadrda alohida saqlangan qatlam (saveLayer) talab
 * qiladi, mobil qurilmada esa bu sezilarli sekinlashuvga olib
 * keladi. To'rtburchak, aylana va uchburchak uchun kontur yetarli.
 *
 * Matn nishon bo'lsa taxminiy to'rtburchak olinadi. Harflar
 * konturi bo'yicha qirqish uchun matnni Path ga aylantirish
 * kerak — u alohida bosqichda qo'shiladi.
 */
private fun DrawScope.drawClipped(
    target: DesignLayer,
    children: List<DesignLayer>,
    geometry: CanvasGeometry,
    textMeasurer: TextMeasurer,
    textAsPaths: Boolean,
    images: Map<String, ImageBitmap>
) {

    val topLeft = geometry.layerTopLeftPx(target)

    val size = geometry.layerSizePx(target)

    val center = geometry.layerCenterPx(target)

    val shape = target as? ShapeLayer

    val rotation = target.transform.rotationDeg

    // Burilgan nishon uchun ham qirqish to'g'ri ishlashi kerak.
    // Butun blok nishon burchagiga aylantiriladi, bolalar esa
    // ichkarida teskari aylantirilib o'z holiga qaytariladi —
    // aks holda ular nishon bilan birga burilib ketardi.
    rotate(degrees = rotation, pivot = center) {

        val path = Path().apply {

            when (shape?.kind) {

                ShapeKind.ELLIPSE -> addOval(Rect(topLeft, size))

                ShapeKind.TRIANGLE -> {
                    moveTo(topLeft.x + size.width / 2f, topLeft.y)
                    lineTo(topLeft.x + size.width, topLeft.y + size.height)
                    lineTo(topLeft.x, topLeft.y + size.height)
                    close()
                }

                // Chiziq qatlamining ichi yo'q, shuning uchun u
                // nishon bo'la olmaydi — to'rtburchakka tushadi.
                else -> addRoundRect(
                    RoundRect(
                        rect = Rect(topLeft, size),
                        cornerRadius = CornerRadius(
                            geometry.mmToPx(shape?.cornerRadiusMm ?: 0f)
                        )
                    )
                )
            }
        }

        clipPath(path) {

            rotate(degrees = -rotation, pivot = center) {

                children.forEach { child ->
                    drawLayer(child, geometry, textMeasurer, textAsPaths, images)
                }
            }
        }
    }
}

private fun DrawScope.drawLayer(
    layer: DesignLayer,
    geometry: CanvasGeometry,
    textMeasurer: TextMeasurer,
    textAsPaths: Boolean,
    images: Map<String, ImageBitmap>
) {

    val topLeft = geometry.layerTopLeftPx(layer)

    val size = geometry.layerSizePx(layer)

    val center = geometry.layerCenterPx(layer)

    rotate(degrees = layer.transform.rotationDeg, pivot = center) {

        when (layer) {

            is ShapeLayer -> drawShapeLayer(layer, topLeft, size, geometry)

            is TextLayer -> drawTextLayer(
                layer, topLeft, size, geometry, textMeasurer, textAsPaths
            )

            is ImageLayer -> drawImageLayer(
                layer = layer,
                bitmap = images[layer.sourceUri],
                topLeft = topLeft,
                size = size
            )
        }
    }
}

/**
 * Rasm qatlami.
 *
 * Kesish (crop) 0..1 nisbatda saqlanadi, pikselda emas. Shuning
 * uchun foydalanuvchi rasmni almashtirsa yoki maket boshqa
 * qurilmada ochilsa ham kesim joyi buzilmaydi.
 */
private fun DrawScope.drawImageLayer(
    layer: ImageLayer,
    bitmap: ImageBitmap?,
    topLeft: Offset,
    size: Size
) {

    if (bitmap == null) {

        // Rasm hali yuklanmagan yoki fayli yo'qolgan. Bo'sh joy
        // qoldirilmaydi: foydalanuvchi qatlam qayerdaligini
        // ko'rib turishi kerak, aks holda uni tanlay olmaydi.
        drawRect(
            color = Color(0xFFE5E7EB),
            topLeft = topLeft,
            size = size,
            alpha = layer.transform.opacity
        )

        return
    }

    val srcLeft = (layer.cropLeft * bitmap.width)
        .toInt()
        .coerceIn(0, bitmap.width - 1)

    val srcTop = (layer.cropTop * bitmap.height)
        .toInt()
        .coerceIn(0, bitmap.height - 1)

    val srcWidth = ((layer.cropRight - layer.cropLeft) * bitmap.width)
        .toInt()
        .coerceIn(1, bitmap.width - srcLeft)

    val srcHeight = ((layer.cropBottom - layer.cropTop) * bitmap.height)
        .toInt()
        .coerceIn(1, bitmap.height - srcTop)

    drawImage(
        image = bitmap,
        srcOffset = IntOffset(srcLeft, srcTop),
        srcSize = IntSize(srcWidth, srcHeight),
        dstOffset = IntOffset(topLeft.x.toInt(), topLeft.y.toInt()),
        dstSize = IntSize(
            size.width.toInt().coerceAtLeast(1),
            size.height.toInt().coerceAtLeast(1)
        ),
        alpha = layer.transform.opacity
    )
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

        ShapeKind.TRIANGLE -> {

            val path = Path().apply {
                moveTo(topLeft.x + size.width / 2f, topLeft.y)
                lineTo(topLeft.x + size.width, topLeft.y + size.height)
                lineTo(topLeft.x, topLeft.y + size.height)
                close()
            }

            layer.fill?.let {
                drawPath(path = path, color = it, alpha = alpha)
            }

            layer.strokeColor?.let {
                drawPath(
                    path = path,
                    color = it,
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
                end = Offset(
                    topLeft.x + size.width,
                    topLeft.y + size.height / 2f
                ),
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
    textMeasurer: TextMeasurer,
    textAsPaths: Boolean
) {

    val result = textMeasurer.measure(
        text = layer.displayText,
        style = layer.toTextStyle(this, geometry),
        constraints = Constraints(
            maxWidth = size.width.toInt().coerceAtLeast(1)
        )
    )

    // Matn ramka ichida vertikal markazlashadi. Aks holda shrift
    // kattalashtirilganda pastga oqib ketadi va foydalanuvchi
    // ramkani har safar qayta joylashtirishga majbur bo'ladi.
    val offsetY = ((size.height - result.size.height) / 2f)
        .coerceAtLeast(0f)

    val origin = Offset(topLeft.x, topLeft.y + offsetY)

    if (textAsPaths) {

        drawTextAsPaths(layer, result, origin, geometry)

        return
    }

    drawText(
        textLayoutResult = result,
        topLeft = origin,
        alpha = layer.transform.opacity
    )
}

/**
 * Matnni harflar konturi sifatida chizadi.
 *
 * MUHIM: joylashuv Compose o'lchagan natijadan olinadi, qaytadan
 * hisoblanmaydi. Qatorlarga bo'lish, qatorlar oralig'i va
 * tekislash — bularning hammasi allaqachon TextLayoutResult
 * ichida bor. Agar ularni qo'lda qayta hisoblasak, ekrandagi
 * ko'rinish bilan PDF asta-sekin bir-biridan uzoqlashib ketardi,
 * va bu hozirgi muammodan ancha yomon bo'lardi.
 *
 * Ya'ni bu yerda faqat GLIF chizish usuli almashadi, matn
 * joylashuvi emas.
 */
private fun DrawScope.drawTextAsPaths(
    layer: TextLayer,
    result: TextLayoutResult,
    origin: Offset,
    geometry: CanvasGeometry
) {

    val text = layer.displayText

    if (text.isEmpty()) return

    val fontSizePx = geometry.mmToPx(layer.fontSizeMm)

    val paint = android.graphics.Paint().apply {

        isAntiAlias = true

        textSize = fontSizePx

        typeface = DesignFonts.typeface(
            font = layer.font,
            bold = layer.isBold,
            italic = layer.isItalic
        )

        // Android'da harflar oralig'i EM ulushida o'lchanadi,
        // bizda esa millimetrda. Shrift o'lchamiga bo'linadi.
        letterSpacing = if (fontSizePx > 0f) {
            geometry.mmToPx(layer.letterSpacingMm) / fontSizePx
        } else {
            0f
        }
    }

    val combined = android.graphics.Path()

    for (line in 0 until result.lineCount) {

        val start = result.getLineStart(line)

        val end = result.getLineEnd(line, visibleEnd = true)

        if (end <= start) continue

        val linePath = android.graphics.Path()

        paint.getTextPath(
            text,
            start,
            end,
            origin.x + result.getLineLeft(line),
            origin.y + result.getLineBaseline(line),
            linePath
        )

        combined.addPath(linePath)
    }

    drawPath(
        path = combined.asComposePath(),
        color = layer.color,
        alpha = layer.transform.opacity
    )

    if (!layer.isUnderline) return

    // Tagchiziq shriftga kirmaydi, uni alohida chizish kerak.
    // Qalinlik shrift o'lchamiga bog'lanadi, aks holda katta
    // matnda ip kabi ingichka bo'lib qolardi.
    val thickness = (fontSizePx * 0.06f).coerceAtLeast(0.4f)

    for (line in 0 until result.lineCount) {

        val left = origin.x + result.getLineLeft(line)

        val right = origin.x + result.getLineRight(line)

        val y = origin.y + result.getLineBaseline(line) + thickness * 2f

        drawRect(
            color = layer.color,
            topLeft = Offset(left, y),
            size = Size(right - left, thickness),
            alpha = layer.transform.opacity
        )
    }
}

/** Ekran va eksport uchun bir xil uslub. */
fun TextLayer.toTextStyle(
    density: androidx.compose.ui.unit.Density,
    geometry: CanvasGeometry
): TextStyle {

    val fontSizePx = geometry.mmToPx(fontSizeMm)

    return with(density) {

        TextStyle(
            color = color,
            fontSize = fontSizePx.toSp(),
            fontFamily = DesignFonts.family(font),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
            textDecoration = if (isUnderline) TextDecoration.Underline else null,
            lineHeight = (fontSizePx * lineHeightMultiplier).toSp(),
            letterSpacing = geometry.mmToPx(letterSpacingMm).toSp(),
            textAlign = when (align) {
                TextAlign.START -> ComposeTextAlign.Start
                TextAlign.CENTER -> ComposeTextAlign.Center
                TextAlign.END -> ComposeTextAlign.End
            }
        )
    }
}