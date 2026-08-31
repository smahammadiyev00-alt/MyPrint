package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.myprint.core.di.AppContainer
import uz.myprint.feature.feature.design.studio.data.DesignProjectStore
import uz.myprint.feature.feature.design.studio.data.ImportedImage
import uz.myprint.feature.feature.design.studio.domain.DesignTemplate
import uz.myprint.feature.feature.design.studio.domain.ImageLayer
import uz.myprint.feature.feature.design.studio.data.SavedProject
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import uz.myprint.feature.feature.design.studio.domain.ShapeLayer
import uz.myprint.feature.feature.design.studio.domain.TextLayer
import java.util.UUID

private const val HISTORY_LIMIT = 60

/** Avtosaqlashdan oldingi jimlik. */
private const val AUTO_SAVE_DELAY_MS = 2_000L

class DesignEditorViewModel(
    initialDocument: DesignDocument,

    /**
     * null bo'lishi mumkin — Preview va testlar uchun. Shunda
     * saqlash amallari jimgina o'tkazib yuboriladi.
     */
    private val store: DesignProjectStore? = null,

    private val projectTitle: String = "Nomsiz loyiha",

    private val productId: String = "",

    private val sizeId: String = "",

    private val category: ProductCategory = ProductCategory.OTHER

) : ViewModel() {

    var document by mutableStateOf(initialDocument)
        private set

    var selectedLayerId by mutableStateOf<String?>(null)
        private set

    var zoom by mutableStateOf(1f)
        private set

    var pan by mutableStateOf(Offset.Zero)
        private set

    // ----- YANGI: cho'zish rejimi -----

    /**
     * Proporsiya qulfi.
     *
     * Default'da O'CHIQ. Ilgari burchak nuqtasi doim proporsional
     * ishlardi va buni o'chirishning iloji yo'q edi — asosiy shikoyat
     * shundan kelib chiqqan. Endi bu foydalanuvchi qarori.
     *
     * Yodda tuting: rasm qatlami uchun qulfni yoqib qo'ygan ma'qul,
     * aks holda foto cho'zilib ketadi. Buni UI o'zi taklif qiladi.
     */
    var aspectLocked by mutableStateOf(false)
        private set

    /** Magnit — yo'riqchilarga va boshqa elementlarga yopishish. */
    var snapEnabled by mutableStateOf(true)
        private set

    /** Hozir ko'rsatilayotgan pushti chiziqlar. Faqat harakat vaqtida. */
    var snapLines by mutableStateOf<List<SnapLine>>(emptyList())
        private set

    /** Sloylar paneli ochiqmi — endi bu doimiy holat, dialog emas. */
    var layersPanelOpen by mutableStateOf(false)
        private set

    /**
     * Uzun bosishda ochiladigan menyu qaysi qatlam uchun.
     *
     * null — menyu yopiq.
     */
    var contextMenuLayerId by mutableStateOf<String?>(null)
        private set

    /** Oxirgi muvaffaqiyatli saqlash vaqti. */
    var savedAtMillis by mutableStateOf<Long?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    private var autoSaveJob: Job? = null

    private val undoStack = ArrayDeque<DesignDocument>()

    private val redoStack = ArrayDeque<DesignDocument>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()

    val canRedo: Boolean get() = redoStack.isNotEmpty()

    val selectedLayer: DesignLayer?
        get() = selectedLayerId?.let { document.layerById(it) }

    /**
     * Tanlangan BIRLIK — bitta qatlam yoki butun guruh.
     *
     * Guruhdagi elementni bosgan foydalanuvchi butun guruhni
     * tanlagan bo'ladi, shuning uchun ramka ham, cho'zish
     * nuqtalari ham guruh chegarasi bo'yicha chiziladi.
     */
    val selectionIds: Set<String>
        get() = selectedLayerId
            ?.let { document.selectionUnit(it) }
            ?: emptySet()

    /** Ramka va nuqtalar shu to'rtburchak bo'yicha chiziladi. */
    val selectionTransform: LayerTransform?
        get() = document.boundsOf(selectionIds)

    /** Guruh tanlanganmi. */
    val isGroupSelected: Boolean
        get() = selectionIds.size > 1

    /** Guruh qulflangan bo'lsa hech biri qimirlamaydi. */
    val isSelectionLocked: Boolean
        get() = selectionIds.any { document.layerById(it)?.isLocked == true }

    /** Tanlovda rasm bormi. */
    val selectionHasImage: Boolean
        get() = selectionIds.any { document.layerById(it) is ImageLayer }

    /**
     * Cho'zishda proporsiya saqlanishi kerakmi.
     *
     * RASM uchun burchak nuqtasi DOIM nisbatni saqlaydi, qulf
     * o'chiq bo'lsa ham. Sabab amaliy: logotip cho'zilib buzilsa,
     * mijoz buni ko'pincha sezmaydi va buzilgan logotip bosilib
     * ketadi — bosmaxona esa aybdor bo'lib qoladi. Matn va shakl
     * uchun bunday xavf yo'q, ular ataylab cho'ziladi.
     *
     * Rasmni ataylab cho'zish kerak bo'lsa — masalan fon fotosini
     * butun maketga yoyish — YON nuqta ishlatiladi, u hech qachon
     * cheklanmaydi.
     */
    fun keepAspectFor(handle: ResizeHandle): Boolean =
        aspectLocked || (handle.isCorner && selectionHasImage)

    val selectedText: TextLayer?
        get() = selectedLayer as? TextLayer

    val selectedShape: ShapeLayer?
        get() = selectedLayer as? ShapeLayer

    // ----- tanlash va kanvas -----

    fun select(id: String?) {
        selectedLayerId = id
    }

    fun toggleAspectLock() {
        aspectLocked = !aspectLocked
    }

    fun toggleSnap() {
        snapEnabled = !snapEnabled
        if (!snapEnabled) snapLines = emptyList()
    }

    fun showLayersPanel(open: Boolean) {
        layersPanelOpen = open
    }

    fun openContextMenu(layerId: String) {
        contextMenuLayerId = layerId
    }

    fun closeContextMenu() {
        contextMenuLayerId = null
    }

    // ----- ichiga joylash -----

    /**
     * Qatlamni boshqa qatlam ichiga soladi.
     *
     * targetId null bo'lsa — ichidan chiqaradi.
     */
    fun clipTo(layerId: String, targetId: String?) {

        pushHistory()

        document = document.clipLayer(layerId, targetId)

        selectedLayerId = layerId
    }

    /** Berilgan qatlamni qaysi qatlamlar ichiga solish mumkin. */
    fun clipTargetsFor(layerId: String) = document.clipTargetsFor(layerId)

    fun onCanvasTransform(panChange: Offset, zoomChange: Float) {
        zoom = (zoom * zoomChange).coerceIn(0.25f, 8f)
        pan += panChange
    }

    fun resetView() {
        zoom = 1f
        pan = Offset.Zero
    }

    // ----- uzluksiz harakat -----

    /**
     * Barmoq harakati davomida chaqiriladi — tarixga yozmaydi.
     *
     * O'zgarish TANLANGAN BIRLIKKA qo'llanadi: guruh bo'lsa hamma
     * a'zoga, nishon bo'lsa ichiga qirqilganlarga ham. Shuning
     * uchun bu yerda alohida shart-sharoit yo'q — hammasini
     * document.transformLayers hal qiladi.
     */
    fun transformSelectedLive(block: (LayerTransform) -> LayerTransform) {

        if (isSelectionLocked) return

        val ids = selectionIds

        val before = document.boundsOf(ids) ?: return

        document = document.transformLayers(ids, before, block(before))
    }

    fun updateSnapLines(lines: List<SnapLine>) {
        snapLines = lines
    }

    fun beginGesture() {
        pushHistory()
    }

    /** Barmoq ko'tarilganda chiziqlar yo'qoladi. */
    fun endGesture() {
        snapLines = emptyList()
    }

    /**
     * Panelda son kiritib o'lcham berish.
     *
     * Barmoq bilan 61.0 mm ni aniq qo'yib bo'lmaydi — poligrafiyada
     * esa aniq o'lcham kerak bo'ladi. Qulf yoqilgan bo'lsa ikkinchi
     * o'lcham o'zi hisoblanadi.
     */
    fun setSelectedSize(
        widthMm: Float? = null,
        heightMm: Float? = null
    ) {

        if (isSelectionLocked) return

        val ids = selectionIds

        val t = document.boundsOf(ids) ?: return

        pushHistory()

        var w = widthMm ?: t.widthMm
        var h = heightMm ?: t.heightMm

        // Raqamli panelda ham rasm nisbati saqlanadi — cho'zish
        // bilan bir xil qoida bo'lishi kerak, aks holda bir yo'l
        // himoyalangan, ikkinchisi yo'q degan chalkashlik chiqadi.
        val keepRatio = aspectLocked || selectionHasImage

        if (keepRatio && t.widthMm > 0f && t.heightMm > 0f) {

            val ratio = t.heightMm / t.widthMm

            if (widthMm != null) h = w * ratio else w = h / ratio
        }

        document = document.transformLayers(
            ids = ids,
            before = t,
            after = t.resizedAroundCenter(w, h)
        )
    }

    /** Maket ichida tekislash: chapga, markazga, o'ngga va h.k. */
    fun alignSelected(alignment: LayerAlignment) {

        if (isSelectionLocked) return

        val ids = selectionIds

        val t = document.boundsOf(ids) ?: return

        pushHistory()

        val next = when (alignment) {

            LayerAlignment.LEFT -> t.copy(xMm = 0f)

            LayerAlignment.CENTER_X -> t.copy(
                xMm = (document.widthMm - t.widthMm) / 2f
            )

            LayerAlignment.RIGHT -> t.copy(
                xMm = document.widthMm - t.widthMm
            )

            LayerAlignment.TOP -> t.copy(yMm = 0f)

            LayerAlignment.CENTER_Y -> t.copy(
                yMm = (document.heightMm - t.heightMm) / 2f
            )

            LayerAlignment.BOTTOM -> t.copy(
                yMm = document.heightMm - t.heightMm
            )
        }

        document = document.transformLayers(ids, t, next)
    }

    // ----- qatlamlar -----

    fun addLayer(layer: DesignLayer) {
        pushHistory()
        document = document.addLayer(layer)
        selectedLayerId = layer.id
    }

    fun updateSelected(block: (DesignLayer) -> DesignLayer) {

        val layer = selectedLayer ?: return

        if (layer.isLocked) return

        pushHistory()

        val updated = block(layer)

        document = document.replaceLayer(updated)

        // Uslub o'zgargan bo'lsa hech narsa tarqalmaydi; joylashuv
        // o'zgargan bo'lsa (masalan burilish slayderi) — tarqaladi.
        if (updated.transform != layer.transform) {

            document = document.transformLayers(
                ids = selectionIds,
                before = layer.transform,
                after = updated.transform
            )
        }
    }

    fun updateText(block: (TextLayer) -> TextLayer) {

        val layer = selectedText ?: return

        pushHistory()

        document = document.replaceLayer(block(layer))
    }

    fun updateShape(block: (ShapeLayer) -> ShapeLayer) {

        val layer = selectedShape ?: return

        pushHistory()

        document = document.replaceLayer(block(layer))
    }

    fun applyColor(color: Color) {

        when (val layer = selectedLayer) {

            is TextLayer -> updateText { it.copy(color = color) }

            is ShapeLayer -> updateShape { it.copy(fill = color) }

            null -> setBackground(color)

            else -> Unit
        }
    }

    fun setBackground(color: Color) {
        pushHistory()
        document = document.copy(background = color)
    }

    fun setOpacity(value: Float) {
        updateSelected { layer ->
            layer.withTransform(
                layer.transform.copy(opacity = value.coerceIn(0.05f, 1f))
            )
        }
    }

    fun deleteSelected() {

        val id = selectedLayerId ?: return

        pushHistory()

        document = document.removeLayer(id)

        selectedLayerId = null
    }

    fun duplicateSelected() {

        val layer = selectedLayer ?: return

        pushHistory()

        val copy = layer
            .withTransform(
                layer.transform.copy(
                    xMm = layer.transform.xMm + 3f,
                    yMm = layer.transform.yMm + 3f
                )
            )
            .withId(newId())

        document = document.addLayer(copy)

        selectedLayerId = copy.id
    }

    // ----- shablon -----

    /**
     * Shablonni qo'llaydi.
     *
     * Mavjud qatlamlar butunlay ALMASHTIRILADI, ustiga qo'shilmaydi.
     * Sabab: shablon yaxlit kompozitsiya — fon, tasma, matnlar
     * bir-biriga moslangan. Ustiga qo'yilsa eski elementlar orasidan
     * chiqib turadi va natija tartibsiz bo'ladi.
     *
     * Tarixga yoziladi, ya'ni "orqaga" bosib eski ishga qaytish
     * mumkin. Bu muhim: foydalanuvchi shablonni ko'rish uchun
     * bosishi va fikridan qaytishi tabiiy.
     */
    fun applyTemplate(templateItem: DesignTemplate) {

        pushHistory()

        document = document.copy(layers = templateItem.build(document))

        selectedLayerId = null
    }

    // ----- rasm -----

    /**
     * Galereyadan tanlangan rasmni maketga qo'shadi.
     *
     * O'lcham rasmning O'Z nisbatiga qarab hisoblanadi. Bu muhim:
     * agar hamma rasm bir xil to'rtburchakka joylashtirilsa,
     * kvadrat logotip ham, keng banner ham cho'zilib buziladi va
     * foydalanuvchi buni qo'lda to'g'rilashga majbur bo'ladi.
     *
     * Kenglik maket enining yarmicha olinadi — vizitkada logotip
     * uchun odatiy o'lcham, keyin bemalol o'zgartiriladi.
     */
    fun addImage(image: ImportedImage) {

        val targetWidth = document.widthMm * 0.5f

        val targetHeight = (targetWidth / image.aspectRatio)
            .coerceAtMost(document.heightMm * 0.8f)

        // Balandlik cheklangan bo'lsa, kenglik ham unga
        // moslashadi — aks holda nisbat baribir buzilardi.
        val width = targetHeight * image.aspectRatio

        addLayer(
            ImageLayer(
                id = newId(),
                transform = LayerTransform(
                    xMm = (document.widthMm - width) / 2f,
                    yMm = (document.heightMm - targetHeight) / 2f,
                    widthMm = width,
                    heightMm = targetHeight
                ),
                sourceUri = image.path
            )
        )
    }

    // ----- guruhlash -----

    /**
     * Photoshop'dagi Ctrl+E.
     *
     * Tanlangan qatlamni pastdagisi bilan bitta birlikka qo'shadi.
     */
    fun mergeDown(layerId: String) {
        pushHistory()
        document = document.mergeDown(layerId)
        selectedLayerId = layerId
    }

    fun ungroup(layerId: String) {
        pushHistory()
        document = document.ungroup(layerId)
        selectedLayerId = layerId
    }

    /** Pastda birlashtirish uchun qatlam bormi. */
    fun canMergeDown(layerId: String): Boolean =
        document.layers.indexOfFirst { it.id == layerId } > 0

    fun bringForward() {
        val id = selectedLayerId ?: return
        pushHistory()
        document = document.bringForward(id)
    }

    fun sendBackward() {
        val id = selectedLayerId ?: return
        pushHistory()
        document = document.sendBackward(id)
    }

    // ----- sloylar paneli -----

    fun selectFromPanel(id: String) {
        selectedLayerId = id
    }

    fun moveLayer(id: String, up: Boolean) {
        pushHistory()
        document = if (up) document.bringForward(id)
        else document.sendBackward(id)
    }

    fun toggleVisibility(id: String) {

        val layer = document.layerById(id) ?: return

        pushHistory()

        document = document.replaceLayer(
            layer.withVisibility(!layer.isVisible)
        )
    }

    fun toggleLock(id: String) {

        val layer = document.layerById(id) ?: return

        pushHistory()

        document = document.replaceLayer(layer.withLock(!layer.isLocked))
    }

    fun deleteLayer(id: String) {

        pushHistory()

        // Rasm qatlami o'chirilganda uning nusxasi ham o'chadi.
        // Aks holda ichki xotira asta-sekin ishlatilmaydigan
        // fayllar bilan to'lib borardi va foydalanuvchi buning
        // sababini hech qachon bilmasdi.
        (document.layerById(id) as? ImageLayer)?.let { image ->

            val stillUsed = document.layers.any {
                it.id != id &&
                        it is ImageLayer &&
                        it.sourceUri == image.sourceUri
            }

            if (!stillUsed) {
                AppContainer.imageStore.delete(image.sourceUri)
            }
        }

        document = document.removeLayer(id)

        if (selectedLayerId == id) {
            selectedLayerId = null
        }
    }

    fun duplicateLayer(id: String) {

        val layer = document.layerById(id) ?: return

        pushHistory()

        val copy = layer
            .withTransform(
                layer.transform.copy(
                    xMm = layer.transform.xMm + 3f,
                    yMm = layer.transform.yMm + 3f
                )
            )
            .withId(newId())

        document = document.addLayer(copy)

        selectedLayerId = copy.id
    }

    // ----- saqlash -----

    /**
     * Avtosaqlash.
     *
     * Har o'zgarishda emas, TO'XTAGANDAN keyin saqlanadi. Barmoq
     * bilan surayotganda soniyasiga o'nlab o'zgarish bo'ladi va
     * har birida faylga yozish qurilmani qizdiradi, batareyani
     * yeydi. Ikki soniya jimlikdan keyin bir marta yozish yetarli.
     *
     * Muqova bu yerda chizilmaydi — u qimmat amal (butun maketni
     * bitmapga chizish). Muqova faqat foydalanuvchi studiodan
     * chiqqanda yangilanadi.
     */
    private fun scheduleAutoSave() {

        val store = store ?: return

        autoSaveJob?.cancel()

        autoSaveJob = viewModelScope.launch {
            delay(AUTO_SAVE_DELAY_MS)
            persist(store, withCover = false)
        }
    }

    /**
     * Darhol saqlash — ✓ tugmasi va orqaga chiqish uchun.
     *
     * Muqova bilan birga, chunki bosh sahifadagi ro'yxat aynan
     * shu rasmni ko'rsatadi.
     */
    /**
     * YANGI BO'SH MAKET.
     *
     * Muammo shu edi: maket identifikatori "mahsulot-o'lcham"
     * ko'rinishida yasalardi, ya'ni bitta mahsulot uchun bittagina
     * maket bo'lishi mumkin edi. Studio har safar o'sha eski
     * qoralamani ochib berardi va ikkinchi variantni yasashning
     * iloji yo'q edi.
     *
     * Endi yangi maket vaqt tamg'asi bilan alohida id oladi —
     * eski ish o'z faylida qoladi, ro'yxatda ikkalasi ham
     * ko'rinadi.
     *
     * Joriy ish avval saqlanadi. Bo'sh maket saqlanmaydi:
     * aks holda "Loyihalarim" bo'sh kartalar bilan to'lib
     * ketardi.
     */
    fun startNewDocument() {

        viewModelScope.launch {

            if (document.layers.isNotEmpty()) {
                saveNow()
            }

            autoSaveJob?.cancel()

            // O'lcham, bleed va xavfsiz maydon o'zgarmaydi —
            // mahsulot o'sha, faqat mazmuni bo'shatiladi.
            document = document.copy(
                id = "$productId-$sizeId-${System.currentTimeMillis()}",
                layers = emptyList()
            )

            undoStack.clear()
            redoStack.clear()

            selectedLayerId = null
            contextMenuLayerId = null

            zoom = 1f
            pan = Offset.Zero

            savedAtMillis = null
        }
    }

    suspend fun saveNow(): SavedProject? {

        val store = store ?: return null

        autoSaveJob?.cancel()

        return persist(store, withCover = true)
    }

    private suspend fun persist(
        store: DesignProjectStore,
        withCover: Boolean
    ): SavedProject? {

        isSaving = true

        return try {

            store.save(
                document = document,
                title = projectTitle,
                productId = productId,
                sizeId = sizeId,
                category = category,
                withCover = withCover
            ).also { savedAtMillis = it.updatedAtMillis }

        } catch (error: Exception) {

            // Saqlash muvaffaqiyatsiz bo'lsa ish to'xtamasligi
            // kerak — foydalanuvchi tahrirlashda davom etsin,
            // keyingi avtosaqlash yana urinib ko'radi.
            null

        } finally {
            isSaving = false
        }
    }

    // ----- tarix -----

    private fun pushHistory() {

        undoStack.addLast(document)

        if (undoStack.size > HISTORY_LIMIT) {
            undoStack.removeFirst()
        }

        redoStack.clear()

        // Yagona ulanish nuqtasi: maketni o'zgartiradigan HAMMA
        // amal shu funksiyadan o'tadi, shuning uchun avtosaqlashni
        // boshqa joyga qo'shish shart emas.
        scheduleAutoSave()
    }

    fun undo() {

        val previous = undoStack.removeLastOrNull() ?: return

        redoStack.addLast(document)

        document = previous

        val id = selectedLayerId

        if (id != null && document.layerById(id) == null) {
            selectedLayerId = null
        }
    }

    fun redo() {

        val next = redoStack.removeLastOrNull() ?: return

        undoStack.addLast(document)

        document = next
    }

    companion object {

        fun newId(): String = UUID.randomUUID().toString()
    }
}

enum class LayerAlignment {
    LEFT, CENTER_X, RIGHT, TOP, CENTER_Y, BOTTOM
}