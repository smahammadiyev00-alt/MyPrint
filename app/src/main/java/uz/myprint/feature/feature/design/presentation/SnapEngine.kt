package uz.myprint.feature.feature.design.studio.presentation

import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import kotlin.math.abs

/**
 * MAGNIT.
 *
 * Ostona MILLIMETRDA emas, EKRAN nuqtasida o'lchanadi va keyin
 * millimetrga aylantiriladi. Sabab: agar ostona mm'da qat'iy
 * bo'lsa, banner (3000 mm) da magnit sezilmay qoladi, vizitka
 * (90 mm) da esa element yopishib qolib, qo'yishning iloji
 * bo'lmaydi. Barmoq esa ikkalasida ham bir xil yo'g'onlikda.
 */
private const val DEFAULT_SNAP_PX = 9f

enum class SnapAxis { VERTICAL, HORIZONTAL }

/** Ekranda chiziladigan pushti yo'riqchi. */
data class SnapLine(
    val axis: SnapAxis,
    /** Vertikal uchun x, gorizontal uchun y — mm. */
    val positionMm: Float,
    val isCenter: Boolean = false
)

data class SnapResult(
    val transform: LayerTransform,
    val lines: List<SnapLine>
) {
    val snapped: Boolean get() = lines.isNotEmpty()
}

/**
 * Bitta o'q bo'yicha nomzod: elementning qaysi qirrasi qayerga
 * yopishishi mumkin.
 */
private data class Candidate(
    val edgeValue: Float,
    val targetValue: Float,
    val isCenter: Boolean
) {
    val delta: Float get() = targetValue - edgeValue
    val distance: Float get() = abs(delta)
}

class SnapEngine(
    private val document: DesignDocument,
    pxPerMm: Float,
    private val enabled: Boolean = true,

    /**
     * Yopishish masofasi EKRAN pikselida.
     *
     * Ilgari bu qiymat ichkarida qattiq yozilgan va ~17 px ga teng
     * edi — juda katta. Element deyarli har doim biror chiziqning
     * ta'sir doirasida bo'lib, magnitdan qutulib bo'lmasdi.
     * 9 px barmoq uchun sezilarli, lekin bo'g'ib qo'ymaydi.
     */
    thresholdPx: Float = DEFAULT_SNAP_PX
) {

    private val thresholdMm: Float = if (pxPerMm > 0f) {
        thresholdPx / pxPerMm
    } else {
        1f
    }

    /**
     * Surilayotgan elementni yaqin chiziqlarga yopishtiradi.
     *
     * Burilgan element uchun qirralar emas, faqat MARKAZ hisobga
     * olinadi: burilgan to'rtburchakning "chap qirrasi" degan narsa
     * yo'q, uni yopishtirish sakrashga olib keladi.
     */
    /**
     * @param excludeIds o'ziga yopishmasligi uchun chiqarib
     *        tashlanadigan qatlamlar. Guruh surilganda uning
     *        HAMMA a'zosi chiqariladi — aks holda guruh o'z
     *        a'zosining qirrasiga yopishib, joyidan qimirlamay
     *        qolardi.
     */
    fun snapMove(
        layerId: String,
        excludeIds: Set<String>,
        proposed: LayerTransform
    ): SnapResult {

        if (!enabled) return SnapResult(proposed, emptyList())

        val rotated = abs(proposed.rotationDeg % 360f) > 0.5f

        val vertical = mutableListOf<Candidate>()
        val horizontal = mutableListOf<Candidate>()

        // Guruh surilganda uning HAMMA a'zosi chiqarib
        // tashlanadi. Aks holda guruh o'z a'zosining qirrasiga
        // yopishib, joyidan qimirlamay qolardi.
        val exclude = excludeIds + layerId

        val targetsX = verticalTargets(exclude)
        val targetsY = horizontalTargets(exclude)

        val edgesX = if (rotated) {
            listOf(proposed.centerXMm to true)
        } else {
            listOf(
                proposed.xMm to false,
                proposed.centerXMm to true,
                (proposed.xMm + proposed.widthMm) to false
            )
        }

        val edgesY = if (rotated) {
            listOf(proposed.centerYMm to true)
        } else {
            listOf(
                proposed.yMm to false,
                proposed.centerYMm to true,
                (proposed.yMm + proposed.heightMm) to false
            )
        }

        edgesX.forEach { (edge, isCenter) ->
            targetsX.forEach { (target, targetIsCenter) ->
                if (abs(target - edge) <= thresholdMm) {
                    vertical += Candidate(edge, target, isCenter && targetIsCenter)
                }
            }
        }

        edgesY.forEach { (edge, isCenter) ->
            targetsY.forEach { (target, targetIsCenter) ->
                if (abs(target - edge) <= thresholdMm) {
                    horizontal += Candidate(edge, target, isCenter && targetIsCenter)
                }
            }
        }

        val bestX = vertical.minByOrNull { it.distance }
        val bestY = horizontal.minByOrNull { it.distance }

        val lines = mutableListOf<SnapLine>()

        bestX?.let {
            lines += SnapLine(SnapAxis.VERTICAL, it.targetValue, it.isCenter)
        }

        bestY?.let {
            lines += SnapLine(SnapAxis.HORIZONTAL, it.targetValue, it.isCenter)
        }

        return SnapResult(
            transform = proposed.copy(
                xMm = proposed.xMm + (bestX?.delta ?: 0f),
                yMm = proposed.yMm + (bestY?.delta ?: 0f)
            ),
            lines = lines
        )
    }

    /**
     * Cho'zishda yopishtirish.
     *
     * Farqi: bu yerda faqat SURILAYOTGAN qirra yopishadi, qarama-
     * qarshisi joyida qoladi. Shuning uchun x/y bilan birga
     * kenglik/balandlik ham to'g'rilanadi.
     */
    fun snapResize(
        layerId: String,
        handle: ResizeHandle,
        proposed: LayerTransform,
        minSizeMm: Float = 2f
    ): SnapResult {

        if (!enabled) return SnapResult(proposed, emptyList())

        if (abs(proposed.rotationDeg % 360f) > 0.5f) {
            return SnapResult(proposed, emptyList())
        }

        var result = proposed

        val lines = mutableListOf<SnapLine>()

        if (handle.dirX != 0) {

            val edge = if (handle.dirX < 0) proposed.xMm
            else proposed.xMm + proposed.widthMm

            nearest(verticalTargets(setOf(layerId)), edge)?.let { target ->

                val delta = target - edge

                if (handle.dirX < 0) {
                    val w = (proposed.widthMm - delta).coerceAtLeast(minSizeMm)
                    result = result.copy(xMm = proposed.xMm + delta, widthMm = w)
                } else {
                    val w = (proposed.widthMm + delta).coerceAtLeast(minSizeMm)
                    result = result.copy(widthMm = w)
                }

                lines += SnapLine(SnapAxis.VERTICAL, target)
            }
        }

        if (handle.dirY != 0) {

            val edge = if (handle.dirY < 0) proposed.yMm
            else proposed.yMm + proposed.heightMm

            nearest(horizontalTargets(setOf(layerId)), edge)?.let { target ->

                val delta = target - edge

                if (handle.dirY < 0) {
                    val h = (proposed.heightMm - delta).coerceAtLeast(minSizeMm)
                    result = result.copy(yMm = proposed.yMm + delta, heightMm = h)
                } else {
                    val h = (proposed.heightMm + delta).coerceAtLeast(minSizeMm)
                    result = result.copy(heightMm = h)
                }

                lines += SnapLine(SnapAxis.HORIZONTAL, target)
            }
        }

        return SnapResult(result, lines)
    }

    private fun nearest(
        targets: List<Pair<Float, Boolean>>,
        edge: Float
    ): Float? = targets
        .map { it.first }
        .filter { abs(it - edge) <= thresholdMm }
        .minByOrNull { abs(it - edge) }

    /** Ikkinchi qiymat — bu markaz chizig'imi. */
    private fun verticalTargets(
        excludeIds: Set<String>
    ): List<Pair<Float, Boolean>> {

        val list = mutableListOf<Pair<Float, Boolean>>()

        val margin = document.safeMarginMm

        list += 0f to false
        list += document.widthMm to false
        list += document.widthMm / 2f to true
        list += margin to false
        list += (document.widthMm - margin) to false

        document.layers
            .filter { it.id !in excludeIds && it.isVisible }
            .forEach { other ->

                val t = other.transform

                if (abs(t.rotationDeg % 360f) > 0.5f) return@forEach

                list += t.xMm to false
                list += t.centerXMm to true
                list += (t.xMm + t.widthMm) to false
            }

        return list
    }

    private fun horizontalTargets(
        excludeIds: Set<String>
    ): List<Pair<Float, Boolean>> {

        val list = mutableListOf<Pair<Float, Boolean>>()

        val margin = document.safeMarginMm

        list += 0f to false
        list += document.heightMm to false
        list += document.heightMm / 2f to true
        list += margin to false
        list += (document.heightMm - margin) to false

        document.layers
            .filter { it.id !in excludeIds && it.isVisible }
            .forEach { other ->

                val t = other.transform

                if (abs(t.rotationDeg % 360f) > 0.5f) return@forEach

                list += t.yMm to false
                list += t.centerYMm to true
                list += (t.yMm + t.heightMm) to false
            }

        return list
    }
}