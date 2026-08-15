package uz.myprint.feature.feature.product.domain.usecase

import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.repository.ProductRepository

class GetProductByIdUseCase(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(
        id: String
    ): Product? {
        return repository.getProductById(id)
    }

}