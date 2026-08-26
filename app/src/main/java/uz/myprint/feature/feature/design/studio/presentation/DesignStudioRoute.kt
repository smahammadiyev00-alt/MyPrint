package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.core.di.AppContainer
import uz.myprint.core.navigation.Screen
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.parseCustomSizeId

/** Yuklab bo'lingan maket va uning mahsuloti. */
private data class StudioArgs(
    val productName: String,
    val productId: String,
    val sizeId: String,
    val category: ProductCategory,
    val document: DesignDocument
)

@Composable
fun DesignStudioRoute(
    productId: String,
    sizeId: String,
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {

    val store = AppContainer.projectStore

    val scope = rememberCoroutineScope()

    val argsState = produceState<StudioArgs?>(
        initialValue = null,
        key1 = productId,
        key2 = sizeId
    ) {

        val product = AppContainer.getProductByIdUseCase(productId)

        // ==== TUZATISH ====
        //
        // Ilgari bu yerda faqat product.sizes ichidan qidirilardi.
        // Erkin kiritilgan o'lcham esa katalogda YO'Q — u foydalanuvchi
        // tomonidan yaratiladi va faqat id ichida yashaydi
        // ("custom-200x300cm" ko'rinishida). Topilmagani uchun kod
        // isDefault ga tushib ketardi va banner qanday o'lcham
        // kiritilsa ham 100 × 200 cm bo'lib ochilaverardi.
        //
        // Endi avval id'ning o'zidan o'qiladi.
        val size = parseCustomSizeId(sizeId)
            ?: product?.sizes?.firstOrNull { it.id == sizeId }
            ?: product?.sizes?.firstOrNull { it.isDefault }
            ?: product?.sizes?.firstOrNull()

        value = if (product != null && size != null) {

            // Mahsulot turi ham uzatiladi: bakalning bosma maydoni
            // uning diametridan hisoblanadi, banner qayrilmasi esa
            // 50 mm bo'ladi.
            val fresh = DesignDocument.forProduct(
                id = product.id,
                category = product.category,
                size = size
            )

            // Saqlangan qoralama bo'lsa o'sha ochiladi.
            //
            // Bu shunchaki qulaylik emas: studiodan chiqib qaytgan
            // foydalanuvchi ishini joyida ko'rmasa, 20 daqiqalik
            // mehnat yo'qolgan bo'ladi.
            val saved = store.load(fresh.id)

            StudioArgs(
                productName = product.name,
                productId = product.id,
                sizeId = size.id,
                category = product.category,
                document = saved ?: fresh
            )

        } else {
            null
        }
    }

    val args = argsState.value

    if (args == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (sizeId == Screen.DesignStudio.NO_SIZE && productId.isBlank()) {

                Text(
                    text = "Mahsulot topilmadi",
                    color = MyPrintColors.Error
                )

            } else {
                CircularProgressIndicator(color = MyPrintColors.Primary)
            }
        }

        return
    }

    val viewModel: DesignEditorViewModel = viewModel(
        key = args.document.id,
        factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                return DesignEditorViewModel(
                    initialDocument = args.document,
                    store = store,
                    projectTitle = args.productName,
                    productId = args.productId,
                    sizeId = args.sizeId,
                    category = args.category
                ) as T
            }
        }
    )

    DesignStudioScreen(

        viewModel = viewModel,

        productName = args.productName,

        // Orqaga chiqishda ham saqlanadi. Foydalanuvchini
        // "saqlash" tugmasini izlashga majbur qilmaslik kerak —
        // u dizayn qilyapti, fayl boshqaruvi bilan emas.
        onBackClick = {
            scope.launch { viewModel.saveNow() }
            onBackClick()
        },

        // ✓ tugmasi: saqlash TUGAGUNCHA kutiladi, keyin chiqiladi.
        // Kutish shart, chunki bosh sahifa ro'yxatni darhol
        // o'qiydi — saqlanmagan bo'lsa loyiha ko'rinmay qolardi
        // yoki eski muqova bilan chiqardi.
        onDoneClick = {
            scope.launch {
                viewModel.saveNow()
                onDoneClick()
            }
        }
    )
}