package uz.myprint.feature.feature.product.data.repository

import uz.myprint.feature.feature.product.data.datasource.ProductDataSource
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.repository.ProductRepository
import uz.myprint.feature.feature.product.domain.model.ProductCategory
class ProductRepositoryImpl(
    private val dataSource: ProductDataSource
) : ProductRepository {

    override suspend fun getProducts(): List<Product> {
        return dataSource.getProducts()
    }

    override suspend fun getProductsByCategory(
        category: ProductCategory
    ): List<Product> {

        return dataSource
            .getProducts()
            .filter {
                it.category == category
            }

    }

    override suspend fun getProductById(id: String): Product? {
        return dataSource.getProductById(id)
    }
}