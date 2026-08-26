package uz.myprint.feature.feature.design.studio.domain

import androidx.compose.ui.graphics.Color
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.ProductSize

/** Bosma sifati. Poligrafiya standarti — 300 nuqta/dyuym. */
const val PRINT_DPI = 300f

/** 1 dyuym = 25.4 mm. */
const val MM_PER_INCH = 25.4f

/** 300 DPI da bir millimetrga necha piksel to'g'ri keladi. */
const val PRINT_PX_PER_MM = PRINT_DPI / MM_PER_INCH

/**
 * Tahrirlanayotgan maket.
 *
 * Uch xil chegara bor va ular chalkashmasligi kerak:
 *
 *  trim  — tayyor mahsulot o'lchami, kesish shu yerdan o'tadi
 *  bleed — kesimdan tashqariga chiqadigan zaxira
 *  safe  — kesimdan ichkaridagi xavfsiz maydon
 */
data class DesignDocument(

    val id: String,

    val widthMm: Float,

    val heightMm: Float,

    val bleedMm: Float = 2f,

    val safeMarginMm: Float = 3f,

    val background: Color = Color.White,

    val layers: List<DesignLayer> = emptyList(),

    /** Studio sarlavhasida ko'rsatiladigan izoh. */
    val note: String? = null

) {

    /** Bleed bilan birga to'liq maydon eni. */
    val fullWidthMm: Float get() = widthMm + bleedMm * 2f

    val fullHeightMm: Float get() = heightMm + bleedMm * 2f

    /** 300 DPI da eksport o'lchami, piksel. */
    val exportWidthPx: Int
        get() = Math.round(fullWidthMm * PRINT_PX_PER_MM)

    val exportHeightPx: Int
        get() = Math.round(fullHeightMm * PRINT_PX_PER_MM)

    fun layerById(id: String): DesignLayer? =
        layers.firstOrNull { it.id == id }

    fun replaceLayer(layer: DesignLayer): DesignDocument =
        copy(layers = layers.map { if (it.id == layer.id) layer else it })

    fun addLayer(layer: DesignLayer): DesignDocument =
        copy(layers = layers + layer)

    /**
     * Qatlamni o'chirish.
     *
     * Uning ichiga qirqilgan qatlamlar ham ozod qilinadi — aks
     * holda ular mavjud bo'lmagan nishonga havola qilib qoladi va
     * ekrandan butunlay yo'qoladi. Foydalanuvchi uchun bu "element
     * o'chib ketdi" degan tushunarsiz holat bo'lardi.
     */
    fun removeLayer(id: String): DesignDocument {

        val next = layers
            .filterNot { it.id == id }
            .map { if (it.clipToId == id) it.withClip(null) else it }

        // Guruhda yolg'iz qolgan qatlam guruh emas. Uni bog'liq
        // holda qoldirsak, foydalanuvchi keyin ajratolmay qoladi.
        val lonely = next
            .mapNotNull { it.groupId }
            .groupingBy { it }
            .eachCount()
            .filterValues { it < 2 }
            .keys

        return copy(
            layers = next.map {
                if (it.groupId in lonely) it.withGroup(null) else it
            }
        )
    }

    /** Qatlamni bir pog'ona yuqoriga — ro'yxatda keyinga. */
    fun bringForward(id: String): DesignDocument {

        val index = layers.indexOfFirst { it.id == id }

        if (index == -1 || index == layers.lastIndex) return this

        val next = layers.toMutableList()

        next.add(index + 1, next.removeAt(index))

        return copy(layers = next).revalidateClips()
    }

    fun sendBackward(id: String): DesignDocument {

        val index = layers.indexOfFirst { it.id == id }

        if (index <= 0) return this

        val next = layers.toMutableList()

        next.add(index - 1, next.removeAt(index))

        return copy(layers = next).revalidateClips()
    }

    // =================================================================
    //  ICHIGA JOYLASH (clipping)
    // =================================================================

    /**
     * Berilgan qatlamni qaysi qatlamlar ichiga solish mumkin.
     *
     * Faqat PASTDA turganlar. Yuqoridagi qatlam ichiga solish
     * mumkin emas, chunki u allaqachon ustiga chizilgan bo'ladi va
     * natija foydalanuvchi kutgan narsaga o'xshamaydi.
     *
     * Matn ichiga ham qirqish mumkin — bu poligrafiyada tez-tez
     * kerak bo'ladigan effekt: harflar ichida rasm yoki gradient.
     */
    fun clipTargetsFor(layerId: String): List<DesignLayer> {

        val index = layers.indexOfFirst { it.id == layerId }

        if (index <= 0) return emptyList()

        return layers
            .take(index)
            .filter { it.isVisible && it.clipToId == null }
            .reversed()
    }

    fun clipLayer(layerId: String, targetId: String?): DesignDocument {

        val layer = layerById(layerId) ?: return this

        if (targetId == null) return replaceLayer(layer.withClip(null))

        // O'zini o'ziga solib bo'lmaydi.
        if (targetId == layerId) return this

        val targetIndex = layers.indexOfFirst { it.id == targetId }

        val layerIndex = layers.indexOfFirst { it.id == layerId }

        if (targetIndex == -1 || targetIndex >= layerIndex) return this

        return replaceLayer(layer.withClip(targetId))
    }

    /** Berilgan qatlam ichiga qirqilgan qatlamlar. */
    fun clippedChildren(targetId: String): List<DesignLayer> =
        layers.filter { it.clipToId == targetId }

    // =================================================================
    //  GURUHLASH
    // =================================================================

    fun groupMembers(groupId: String): List<DesignLayer> =
        layers.filter { it.groupId == groupId }

    /**
     * Tanlash birligi: qatlamning o'zi va guruhdoshlari.
     *
     * Guruhdagi bitta elementni bosgan foydalanuvchi butun guruhni
     * tanlagan bo'ladi — aks holda guruhning ma'nosi qolmaydi.
     */
    fun selectionUnit(layerId: String): Set<String> {

        val layer = layerById(layerId) ?: return emptySet()

        val group = layer.groupId ?: return setOf(layerId)

        return groupMembers(group).map { it.id }.toSet()
    }

    /**
     * Bir nechta qatlamni qamrab oluvchi to'rtburchak.
     *
     * Burilish 0 deb olinadi: har xil burchakka burilgan
     * elementlarni qamragan "burilgan ramka" degan narsa yo'q,
     * bunday holatda faqat o'qlarga parallel quti mantiqiy.
     */
    fun boundsOf(ids: Set<String>): LayerTransform? {

        val members = layers.filter { it.id in ids }

        if (members.isEmpty()) return null

        if (members.size == 1) return members.first().transform

        var left = Float.MAX_VALUE
        var top = Float.MAX_VALUE
        var right = -Float.MAX_VALUE
        var bottom = -Float.MAX_VALUE

        members.forEach { member ->

            val t = member.transform

            left = minOf(left, t.xMm)
            top = minOf(top, t.yMm)
            right = maxOf(right, t.xMm + t.widthMm)
            bottom = maxOf(bottom, t.yMm + t.heightMm)
        }

        return LayerTransform(
            xMm = left,
            yMm = top,
            widthMm = right - left,
            heightMm = bottom - top
        )
    }

    /**
     * Photoshop'dagi Ctrl+E ning bu muharrirdagi ekvivalenti.
     *
     * Photoshop tanlangan qatlamni PASTDAGISI bilan qo'shadi —
     * shuning uchun bu yerda ham ikkita qatlamni tanlash shart
     * emas, faqat ustidagisini bosish kifoya.
     *
     * Farqi: Photoshop natijani rasmga aylantiradi va matn qayta
     * tahrirlanmaydigan bo'lib qoladi. Bu yerda esa ikkalasi
     * o'zicha qolib, faqat bitta birlikka bog'lanadi. Amalda
     * foydalanuvchi kutgan natija aynan shu — ikkalasi birga
     * surilsin — lekin matni ham, rangi ham saqlanadi.
     */
    fun mergeDown(layerId: String): DesignDocument {

        val index = layers.indexOfFirst { it.id == layerId }

        if (index <= 0) return this

        val layer = layers[index]

        val below = layers[index - 1]

        // Pastdagisi allaqachon guruhda bo'lsa, o'shanga qo'shiladi.
        val groupId = below.groupId
            ?: layer.groupId
            ?: "grp-${below.id.take(8)}"

        val ids = buildSet {

            add(layer.id)
            add(below.id)

            layer.groupId?.let { g ->
                groupMembers(g).forEach { add(it.id) }
            }

            below.groupId?.let { g ->
                groupMembers(g).forEach { add(it.id) }
            }
        }

        return copy(
            layers = layers.map {
                if (it.id in ids) it.withGroup(groupId) else it
            }
        )
    }

    /** Guruhni ajratadi. Qatlamlar o'z joyida qoladi. */
    fun ungroup(layerId: String): DesignDocument {

        val group = layerById(layerId)?.groupId ?: return this

        return copy(
            layers = layers.map {
                if (it.groupId == group) it.withGroup(null) else it
            }
        )
    }

    // =================================================================
    //  O'ZGARISHNI TARQATISH
    // =================================================================

    /**
     * before → after o'zgarishini berilgan qatlamlarga qo'llaydi.
     *
     * Uch joyda kerak bo'ladi va uchalasida mantiq bir xil:
     *   guruh    — hamma a'zo birga qimirlaydi
     *   clip     — nishon surilsa ichidagilar ergashadi
     *   yakka    — qatlamning o'zi (matematik jihatdan xuddi shu)
     *
     * Har bir qatlam markazi `before` markaziga NISBATAN olinadi,
     * shuning uchun burilish va masshtab birlik markazi atrofida
     * to'g'ri qo'llanadi.
     */
    fun transformLayers(
        ids: Set<String>,
        before: LayerTransform,
        after: LayerTransform
    ): DesignDocument {

        if (ids.isEmpty()) return this

        // Guruh a'zolarining ichiga qirqilganlari ham ergashadi.
        val affected = ids + ids.flatMap { id ->
            clippedChildren(id).map { it.id }
        }

        val deltaRotation = after.rotationDeg - before.rotationDeg

        val scaleX = if (before.widthMm > 0.01f) {
            after.widthMm / before.widthMm
        } else {
            1f
        }

        val scaleY = if (before.heightMm > 0.01f) {
            after.heightMm / before.heightMm
        } else {
            1f
        }

        val moved = before.centerXMm != after.centerXMm ||
                before.centerYMm != after.centerYMm

        if (!moved && deltaRotation == 0f &&
            scaleX == 1f && scaleY == 1f
        ) {
            return this
        }

        val rad = Math.toRadians(deltaRotation.toDouble())

        val cos = kotlin.math.cos(rad).toFloat()

        val sin = kotlin.math.sin(rad).toFloat()

        return copy(
            layers = layers.map { layer ->

                if (layer.id !in affected) return@map layer

                val t = layer.transform

                var vx = (t.centerXMm - before.centerXMm) * scaleX
                var vy = (t.centerYMm - before.centerYMm) * scaleY

                if (deltaRotation != 0f) {
                    val rx = vx * cos - vy * sin
                    val ry = vx * sin + vy * cos
                    vx = rx
                    vy = ry
                }

                val newWidth = (t.widthMm * scaleX).coerceAtLeast(2f)

                val newHeight = (t.heightMm * scaleY).coerceAtLeast(2f)

                layer.withTransform(
                    t.copy(
                        xMm = after.centerXMm + vx - newWidth / 2f,
                        yMm = after.centerYMm + vy - newHeight / 2f,
                        widthMm = newWidth,
                        heightMm = newHeight,
                        rotationDeg = t.rotationDeg + deltaRotation
                    )
                )
            }
        )
    }

    /**
     * Tartib o'zgargandan keyin buzilgan bog'lanishlarni tozalaydi.
     *
     * Qatlam nishonidan pastga tushib qolsa, "ichida" degan holat
     * ma'nosini yo'qotadi — nishon allaqachon uning ustiga
     * chizilgan bo'ladi. Bunday bog'lanishni jimgina uzib qo'yish
     * ekranda tushunarsiz natija chiqishidan yaxshiroq.
     */
    private fun revalidateClips(): DesignDocument = copy(
        layers = layers.mapIndexed { index, layer ->

            val targetId = layer.clipToId ?: return@mapIndexed layer

            val targetIndex = layers.indexOfFirst { it.id == targetId }

            if (targetIndex == -1 || targetIndex >= index) {
                layer.withClip(null)
            } else {
                layer
            }
        }
    )

    /**
     * Xavfsiz maydondan chiqib ketgan qatlamlar.
     *
     * Ichiga qirqilgan qatlamlar tekshirilmaydi: ular baribir
     * nishon shakli ichida qoladi, tashqarisi ko'rinmaydi.
     */
    fun layersOutsideSafeArea(): List<DesignLayer> =
        layers.filter { layer ->

            val t = layer.transform

            layer.isVisible && layer.clipToId == null && (
                    t.xMm < safeMarginMm ||
                            t.yMm < safeMarginMm ||
                            t.xMm + t.widthMm > widthMm - safeMarginMm ||
                            t.yMm + t.heightMm > heightMm - safeMarginMm
                    )
        }

    companion object {

        /**
         * Mahsulot va o'lchamdan bo'sh maket yasaydi.
         *
         * Endi o'lcham to'g'ridan-to'g'ri olinmaydi — PrintSurface
         * uni mahsulot turiga qarab bosma maydoniga aylantiradi.
         * Bakal 82 mm emas, 210 mm bo'lib ochilishi shundan.
         */
        fun forProduct(
            id: String,
            category: ProductCategory,
            size: ProductSize
        ): DesignDocument {

            val surface = PrintSurface.forProduct(category, size)

            return DesignDocument(
                id = "$id-${size.id}",
                widthMm = surface.widthMm,
                heightMm = surface.heightMm,
                bleedMm = surface.bleedMm,
                safeMarginMm = surface.safeMarginMm,
                note = surface.note
            )
        }

        /** Eski chaqiruvlar buzilmasligi uchun. */
        @Deprecated(
            "Mahsulot turi hisobga olinmaydi",
            ReplaceWith("forProduct(id, category, size)")
        )
        fun forProductSize(
            id: String,
            size: ProductSize,
            bleedMm: Float = 2f,
            safeMarginMm: Float = 3f
        ): DesignDocument = forProduct(
            id = id,
            category = ProductCategory.OTHER,
            size = size
        ).copy(bleedMm = bleedMm, safeMarginMm = safeMarginMm)
    }
}