package uz.myprint.core.di

import android.content.Context
import uz.myprint.feature.feature.cart.data.repository.InMemoryCartRepository
import uz.myprint.feature.feature.cart.domain.repository.CartRepository
import uz.myprint.feature.feature.design.studio.data.DesignProjectStore
import uz.myprint.feature.feature.printshop.data.repository.PrintShopRepositoryImpl
import uz.myprint.feature.feature.printshop.domain.usecase.GetPrintShopOffersUseCase
import uz.myprint.feature.feature.product.data.datasource.ProductDataSourceImpl
import uz.myprint.feature.feature.product.data.repository.ProductRepositoryImpl
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase
import uz.myprint.feature.feature.product.domain.usecase.GetProductsByCategoryUseCase
import uz.myprint.feature.feature.product.domain.usecase.GetProductsUseCase

object AppContainer {

    /**
     * Ilova konteksti.
     *
     * Activity konteksti EMAS: ombor ViewModel umrida yashaydi va
     * Activity qayta yaratilganda (masalan ekran burilganda)
     * eskisiga havola qolib ketsa, xotira sizib chiqadi.
     */
    private lateinit var appContext: Context

    /** Application.onCreate ichida bir marta chaqiriladi. */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Product
    private val productDataSource by lazy {
        ProductDataSourceImpl()
    }

    private val productRepository by lazy {
        ProductRepositoryImpl(productDataSource)
    }

    val getProductsUseCase by lazy {
        GetProductsUseCase(productRepository)
    }

    val getProductByIdUseCase by lazy {
        GetProductByIdUseCase(productRepository)
    }

    val getProductsByCategoryUseCase by lazy {
        GetProductsByCategoryUseCase(productRepository)
    }

    // PrintShop
    private val printShopRepository by lazy {
        PrintShopRepositoryImpl()
    }

    val getPrintShopOffersUseCase by lazy {
        GetPrintShopOffersUseCase(printShopRepository)
    }

    // Cart
    //
    // Bitta nusxada bo'lishi shart: har ekran o'z savatini yaratsa,
    // qo'shilgan mahsulot boshqa ekranda ko'rinmaydi.
    val cartRepository: CartRepository by lazy {
        InMemoryCartRepository()
    }

    // Loyihalar ombori
    //
    // Bu ham bitta nusxada: studio saqlagan narsa bosh sahifada
    // darhol ko'rinishi kerak.
    val projectStore: DesignProjectStore by lazy {
        DesignProjectStore(appContext)
    }
}