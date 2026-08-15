package uz.myprint.feature.feature.product.data.datasource

import uz.myprint.feature.feature.product.data.dummy.ProductDummyData
import uz.myprint.feature.feature.product.domain.model.Product

class FakeProductDataSource : ProductDataSource {

    override suspend fun getProducts(): List<Product> {
        return ProductDummyData.products
    }

    override suspend fun getProductById(id: String): Product? {
        return ProductDummyData.products.find { it.id == id }
    }
}