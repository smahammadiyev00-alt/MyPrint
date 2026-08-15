package uz.myprint.feature.feature.product.domain.repository

import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory

interface ProductRepository {

    suspend fun getProducts(): List<Product>

    suspend fun getProductsByCategory(
        category: ProductCategory
    ): List<Product>

    suspend fun getProductById(
        id: String
    ): Product?

}