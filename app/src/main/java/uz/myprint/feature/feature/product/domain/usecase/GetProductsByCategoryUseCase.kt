package uz.myprint.feature.feature.product.domain.usecase

import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.repository.ProductRepository

class GetProductsByCategoryUseCase(
    private val repository: ProductRepository
) {

    suspend operator fun invoke(
        category: ProductCategory
    ): List<Product> {

        return repository.getProductsByCategory(category)

    }

}