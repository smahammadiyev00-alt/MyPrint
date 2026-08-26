package uz.myprint.feature.feature.design.studio.data

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.DesignFont
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.ImageLayer
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import uz.myprint.feature.feature.design.studio.domain.ShapeKind
import uz.myprint.feature.feature.design.studio.domain.ShapeLayer
import uz.myprint.feature.feature.design.studio.domain.TextAlign
import uz.myprint.feature.feature.design.studio.domain.TextCase
import uz.myprint.feature.feature.design.studio.domain.TextLayer

/**
 * MAKETNI JSON KO'RINISHIDA SAQLASH.
 *
 * Nega rasm emas, maketning o'zi saqlanadi: mijoz ertaga "telefon
 * raqamimni o'zgartiring" desa, PNG'dan qaytishning iloji yo'q.
 * JSON'dan esa maket ochiladi, bitta qator tuzatiladi va qayta
 * bosmaga yuboriladi.
 *
 * Nega kotlinx.serialization emas: u KSP va plagin talab qiladi,
 * loyihada esa hozir ular yo'q. org.json Android'ning ichida bor
 * va bu vazifa uchun yetarli. Sxema kattalashsa, keyinchalik
 * kutubxonaga o'tish qiyin bo'lmaydi — format bir xil qoladi.
 *
 * VERSIYA muhim: sxema o'zgarganda eski fayllarni o'qish uchun
 * kerak bo'ladi. Uni boshidan yozib qo'yish, keyin qo'shishdan
 * ancha oson.
 */
object DesignDocumentJson {

    private const val VERSION = 1

    // ---------------------------------------------------------------
    //  YOZISH
    // ---------------------------------------------------------------

    fun encode(document: DesignDocument): String =
        JSONObject().apply {

            put("version", VERSION)
            put("id", document.id)
            put("widthMm", document.widthMm.toDouble())
            put("heightMm", document.heightMm.toDouble())
            put("bleedMm", document.bleedMm.toDouble())
            put("safeMarginMm", document.safeMarginMm.toDouble())
            put("background", document.background.toHex())

            document.note?.let { put("note", it) }

            put(
                "layers",
                JSONArray().apply {
                    document.layers.forEach { put(encodeLayer(it)) }
                }
            )

        }.toString()

    private fun encodeLayer(layer: DesignLayer): JSONObject {

        val json = JSONObject().apply {

            put("id", layer.id)
            put("name", layer.name)
            put("locked", layer.isLocked)
            put("visible", layer.isVisible)
            put("transform", encodeTransform(layer.transform))

            layer.clipToId?.let { put("clipToId", it) }
            layer.groupId?.let { put("groupId", it) }
        }

        when (layer) {

            is TextLayer -> json.apply {
                put("type", "text")
                put("text", layer.text)
                put("fontSizeMm", layer.fontSizeMm.toDouble())
                put("font", layer.font.name)
                put("color", layer.color.toHex())
                put("bold", layer.isBold)
                put("italic", layer.isItalic)
                put("underline", layer.isUnderline)
                put("textCase", layer.textCase.name)
                put("align", layer.align.name)
                put("lineHeight", layer.lineHeightMultiplier.toDouble())
                put("letterSpacingMm", layer.letterSpacingMm.toDouble())
            }

            is ShapeLayer -> json.apply {
                put("type", "shape")
                put("kind", layer.kind.name)
                layer.fill?.let { put("fill", it.toHex()) }
                layer.strokeColor?.let { put("strokeColor", it.toHex()) }
                put("strokeWidthMm", layer.strokeWidthMm.toDouble())
                put("cornerRadiusMm", layer.cornerRadiusMm.toDouble())
            }

            is ImageLayer -> json.apply {
                put("type", "image")
                put("sourceUri", layer.sourceUri)
                put("cropLeft", layer.cropLeft.toDouble())
                put("cropTop", layer.cropTop.toDouble())
                put("cropRight", layer.cropRight.toDouble())
                put("cropBottom", layer.cropBottom.toDouble())
            }
        }

        return json
    }

    private fun encodeTransform(t: LayerTransform): JSONObject =
        JSONObject().apply {
            put("x", t.xMm.toDouble())
            put("y", t.yMm.toDouble())
            put("w", t.widthMm.toDouble())
            put("h", t.heightMm.toDouble())
            put("rot", t.rotationDeg.toDouble())
            put("opacity", t.opacity.toDouble())
        }

    // ---------------------------------------------------------------
    //  O'QISH
    // ---------------------------------------------------------------

    /**
     * Buzilgan yoki eski fayl uchun null qaytaradi.
     *
     * Istisno tashlamaydi: bitta buzilgan qoralama tufayli
     * "Loyihalaringiz" ro'yxati butunlay ochilmay qolishi mumkin
     * emas. Yomon fayl shunchaki ro'yxatga tushmaydi.
     */
    fun decode(raw: String): DesignDocument? = runCatching {

        val json = JSONObject(raw)

        val layers = json.optJSONArray("layers")

        DesignDocument(
            id = json.getString("id"),
            widthMm = json.getDouble("widthMm").toFloat(),
            heightMm = json.getDouble("heightMm").toFloat(),
            bleedMm = json.optDouble("bleedMm", 2.0).toFloat(),
            safeMarginMm = json.optDouble("safeMarginMm", 3.0).toFloat(),
            background = json.optString("background")
                .takeIf { it.isNotBlank() }
                ?.toColor()
                ?: Color.White,
            note = json.optString("note").takeIf { it.isNotBlank() },
            layers = buildList {
                for (i in 0 until (layers?.length() ?: 0)) {
                    decodeLayer(layers!!.getJSONObject(i))?.let { add(it) }
                }
            }
        )

    }.getOrNull()

    private fun decodeLayer(json: JSONObject): DesignLayer? = runCatching {

        val id = json.getString("id")

        val name = json.optString("name")

        val transform = decodeTransform(json.getJSONObject("transform"))

        val locked = json.optBoolean("locked", false)

        val visible = json.optBoolean("visible", true)

        val clipToId = json.optString("clipToId").takeIf { it.isNotBlank() }

        val groupId = json.optString("groupId").takeIf { it.isNotBlank() }

        when (json.getString("type")) {

            "text" -> TextLayer(
                id = id,
                name = name.ifBlank { "Matn" },
                transform = transform,
                isLocked = locked,
                isVisible = visible,
                clipToId = clipToId,
                groupId = groupId,
                text = json.optString("text"),
                fontSizeMm = json.optDouble("fontSizeMm", 5.0).toFloat(),
                font = enumOr(json.optString("font"), DesignFont.SANS),
                color = json.optString("color").toColorOr(Color.Black),
                isBold = json.optBoolean("bold", false),
                isItalic = json.optBoolean("italic", false),
                isUnderline = json.optBoolean("underline", false),
                textCase = enumOr(json.optString("textCase"), TextCase.NORMAL),
                align = enumOr(json.optString("align"), TextAlign.START),
                lineHeightMultiplier = json
                    .optDouble("lineHeight", 1.2).toFloat(),
                letterSpacingMm = json
                    .optDouble("letterSpacingMm", 0.0).toFloat()
            )

            "shape" -> ShapeLayer(
                id = id,
                name = name.ifBlank { "Shakl" },
                transform = transform,
                isLocked = locked,
                isVisible = visible,
                clipToId = clipToId,
                groupId = groupId,
                kind = enumOr(json.optString("kind"), ShapeKind.RECTANGLE),
                fill = json.optString("fill")
                    .takeIf { it.isNotBlank() }?.toColor(),
                strokeColor = json.optString("strokeColor")
                    .takeIf { it.isNotBlank() }?.toColor(),
                strokeWidthMm = json
                    .optDouble("strokeWidthMm", 0.5).toFloat(),
                cornerRadiusMm = json
                    .optDouble("cornerRadiusMm", 0.0).toFloat()
            )

            "image" -> ImageLayer(
                id = id,
                name = name.ifBlank { "Rasm" },
                transform = transform,
                isLocked = locked,
                isVisible = visible,
                clipToId = clipToId,
                groupId = groupId,
                sourceUri = json.optString("sourceUri"),
                cropLeft = json.optDouble("cropLeft", 0.0).toFloat(),
                cropTop = json.optDouble("cropTop", 0.0).toFloat(),
                cropRight = json.optDouble("cropRight", 1.0).toFloat(),
                cropBottom = json.optDouble("cropBottom", 1.0).toFloat()
            )

            else -> null
        }

    }.getOrNull()

    private fun decodeTransform(json: JSONObject) = LayerTransform(
        xMm = json.optDouble("x", 0.0).toFloat(),
        yMm = json.optDouble("y", 0.0).toFloat(),
        widthMm = json.optDouble("w", 40.0).toFloat(),
        heightMm = json.optDouble("h", 20.0).toFloat(),
        rotationDeg = json.optDouble("rot", 0.0).toFloat(),
        opacity = json.optDouble("opacity", 1.0).toFloat()
    )

    // ---------------------------------------------------------------
    //  YORDAMCHILAR
    // ---------------------------------------------------------------

    /**
     * Rang "#AARRGGBB" ko'rinishida.
     *
     * Son emas, satr: JSON faylni qo'lda ochib ko'rganda rang
     * darhol o'qiladi va tuzatiladi. Nosozlikni izlashda bu
     * ancha vaqt tejaydi.
     */
    private fun Color.toHex(): String =
        "#%08X".format(value.toLong().ushr(32).toInt())

    private fun String.toColor(): Color? = runCatching {
        Color(removePrefix("#").toLong(16).toInt())
    }.getOrNull()

    private fun String.toColorOr(fallback: Color): Color =
        takeIf { it.isNotBlank() }?.toColor() ?: fallback

    private inline fun <reified T : Enum<T>> enumOr(
        name: String,
        fallback: T
    ): T = runCatching { enumValueOf<T>(name) }.getOrDefault(fallback)
}
