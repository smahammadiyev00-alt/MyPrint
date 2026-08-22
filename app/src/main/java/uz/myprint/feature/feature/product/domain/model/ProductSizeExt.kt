package uz.myprint.feature.feature.product.domain.model

import kotlin.math.min

/** Erkin kiritilgan o'lchamlar shu prefiks bilan belgilanadi. */
const val CUSTOM_SIZE_PREFIX = "custom-"

val ProductSize.isCustom: Boolean
    get() = id.startsWith(CUSTOM_SIZE_PREFIX)

/** Berilgan birlikni metrga o'tkazish koeffitsienti. */
private val SizeUnit.toMeters: Float
    get() = when (this) {
        SizeUnit.MM -> 0.001f
        SizeUnit.CM -> 0.01f
        SizeUnit.M -> 1f
    }

val ProductSize.widthMeters: Float
    get() = width * unit.toMeters

val ProductSize.heightMeters: Float
    get() = height * unit.toMeters

/** Maydon, kvadrat metrda. */
val ProductSize.areaSquareMeters: Double
    get() = (widthMeters.toDouble()) * (heightMeters.toDouble())

/**
 * Pogon metr: rulonning eni qat'iy bo'lganda to'lanadigan uzunlik.
 *
 * Dizayn rulonga ikki yo'nalishda joylashishi mumkin. Qaysi biri
 * kamroq uzunlik talab qilsa, o'sha tanlanadi. Ikkalasi ham
 * sig'masa null qaytadi — bunday buyurtma bo'laklab bosiladi va
 * narxi qo'lda kelishiladi.
 */
fun ProductSize.linearMeters(rollWidthMeters: Float): Float? {

    val w = widthMeters
    val h = heightMeters

    val optionA = if (w <= rollWidthMeters) h else null
    val optionB = if (h <= rollWidthMeters) w else null

    return when {
        optionA != null && optionB != null -> min(optionA, optionB)
        optionA != null -> optionA
        optionB != null -> optionB
        else -> null
    }
}

/**
 * Foydalanuvchi kiritgan o'lcham uchun sun'iy ProductSize.
 * Katalogda yo'q, lekin qolgan kod uchun oddiy o'lchamdek ishlaydi.
 */
fun customProductSize(
    width: Float,
    height: Float,
    unit: SizeUnit
): ProductSize {

    val label = when (unit) {
        SizeUnit.M -> "${width.trimZero()} × ${height.trimZero()} m"
        SizeUnit.CM -> "${width.toInt()} × ${height.toInt()} cm"
        SizeUnit.MM -> "${width.toInt()} × ${height.toInt()} mm"
    }

    val code = when (unit) {
        SizeUnit.M -> "m"
        SizeUnit.CM -> "cm"
        SizeUnit.MM -> "mm"
    }

    return ProductSize(
        id = "$CUSTOM_SIZE_PREFIX${width.trimZero()}x${height.trimZero()}$code",
        title = label,
        width = width,
        height = height,
        unit = unit
    )
}

/**
 * "custom-1.5x3m" yoki "custom-15x20cm" -> ProductSize.
 * Format noto'g'ri bo'lsa null.
 */
fun parseCustomSizeId(id: String): ProductSize? {

    if (!id.startsWith(CUSTOM_SIZE_PREFIX)) return null

    val body = id.removePrefix(CUSTOM_SIZE_PREFIX)

    val unit = when {
        body.endsWith("mm") -> SizeUnit.MM
        body.endsWith("cm") -> SizeUnit.CM
        body.endsWith("m") -> SizeUnit.M
        else -> return null
    }

    val code = when (unit) {
        SizeUnit.MM -> "mm"
        SizeUnit.CM -> "cm"
        SizeUnit.M -> "m"
    }

    val pieces = body.removeSuffix(code).split("x")

    if (pieces.size != 2) return null

    val width = pieces[0].toFloatOrNull() ?: return null
    val height = pieces[1].toFloatOrNull() ?: return null

    if (width <= 0f || height <= 0f) return null

    return customProductSize(width, height, unit)
}

/** 3.0 -> "3",  1.5 -> "1.5" */
private fun Float.trimZero(): String =
    if (this == this.toInt().toFloat()) {
        this.toInt().toString()
    } else {
        this.toString()
    }