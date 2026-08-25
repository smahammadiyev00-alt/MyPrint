package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import java.util.UUID

/** Tarixda saqlanadigan eng ko'p qadam. */
private const val HISTORY_LIMIT = 60

/**
 * Undo/redo butun hujjatning nusxasini saqlash orqali ishlaydi.
 *
 * Har bir amal uchun alohida teskari amal yozish (Command pattern)
 * xotirani tejaydi, lekin bitta unutilgan teskari amal tarixni
 * jimgina buzadi va buni topish qiyin. Hujjat yengil — faqat
 * sonlar va matn, bitmap yo'q — shuning uchun nusxa arzon.
 */
class DesignEditorViewModel(
    initialDocument: DesignDocument
) : ViewModel() {

    var document by mutableStateOf(initialDocument)
        private set

    var selectedLayerId by mutableStateOf<String?>(null)
        private set

    /** Kanvas masshtabi, foydalanuvchi barmoq bilan o'zgartiradi. */
    var zoom by mutableStateOf(1f)
        private set

    var pan by mutableStateOf(androidx.compose.ui.geometry.Offset.Zero)
        private set

    private val undoStack = ArrayDeque<DesignDocument>()

    private val redoStack = ArrayDeque<DesignDocument>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()

    val canRedo: Boolean get() = redoStack.isNotEmpty()

    val selectedLayer: DesignLayer?
        get() = selectedLayerId?.let { document.layerById(it) }

    // ----- tanlash -----

    fun select(id: String?) {
        selectedLayerId = id
    }

    // ----- kanvas -----

    fun onCanvasTransform(
        panChange: androidx.compose.ui.geometry.Offset,
        zoomChange: Float
    ) {
        zoom = (zoom * zoomChange).coerceIn(0.25f, 8f)
        pan += panChange
    }

    fun resetView() {
        zoom = 1f
        pan = androidx.compose.ui.geometry.Offset.Zero
    }

    // ----- qatlam o'zgarishlari -----

    /**
     * Uzluksiz harakat (surish, cho'zish) uchun. Tarixga yozmaydi —
     * aks holda bitta surishdan o'nlab qadam paydo bo'lardi.
     * Harakat tugagach commit() chaqiriladi.
     */
    fun transformSelectedLive(block: (LayerTransform) -> LayerTransform) {

        val layer = selectedLayer ?: return

        if (layer.isLocked) return

        document = document.replaceLayer(
            layer.withTransform(block(layer.transform))
        )
    }

    /** Harakat boshlanishidan oldingi holatni tarixga yozadi. */
    fun beginGesture() {
        pushHistory()
    }

    fun addLayer(layer: DesignLayer) {
        pushHistory()
        document = document.addLayer(layer)
        selectedLayerId = layer.id
    }

    fun updateLayer(layer: DesignLayer) {
        pushHistory()
        document = document.replaceLayer(layer)
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

        val copy = layer.withTransform(
            layer.transform.copy(
                xMm = layer.transform.xMm + 3f,
                yMm = layer.transform.yMm + 3f
            )
        ).let { shifted ->
            when (shifted) {
                is uz.myprint.feature.feature.design.studio.domain.TextLayer ->
                    shifted.copy(id = newId())

                is uz.myprint.feature.feature.design.studio.domain.ShapeLayer ->
                    shifted.copy(id = newId())

                is uz.myprint.feature.feature.design.studio.domain.ImageLayer ->
                    shifted.copy(id = newId())
            }
        }

        document = document.addLayer(copy)

        selectedLayerId = copy.id
    }

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

    // ----- tarix -----

    private fun pushHistory() {

        undoStack.addLast(document)

        if (undoStack.size > HISTORY_LIMIT) {
            undoStack.removeFirst()
        }

        redoStack.clear()
    }

    fun undo() {

        val previous = undoStack.removeLastOrNull() ?: return

        redoStack.addLast(document)

        document = previous

        // Tanlangan qatlam o'chirilgan bo'lishi mumkin.
        if (selectedLayerId != null && document.layerById(selectedLayerId!!) == null) {
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
