package uz.myprint.feature.feature.product.domain.usecase

import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.repository.ProductRepository

class GetProductsUseCase(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(): List<Product> {
        return repository.getProducts()
    }
}