package uz.myprint.core.di

import uz.myprint.feature.feature.product.data.datasource.ProductDataSourceImpl
import uz.myprint.feature.feature.product.data.repository.ProductRepositoryImpl
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase
import uz.myprint.feature.feature.product.domain.usecase.GetProductsUseCase

object AppContainer {

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
}