package uz.myprint.feature.feature.product.data.datasource

import uz.myprint.feature.feature.product.domain.model.Product

interface ProductDataSource {

    suspend fun getProducts(): List<Product>

    suspend fun getProductById(id: String): Product?
}