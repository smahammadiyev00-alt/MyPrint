package uz.myprint.feature.feature.printshop.domain.repository

import uz.myprint.feature.feature.printshop.domain.model.PrintShop

interface PrintShopRepository {

    suspend fun getPrintShops(): List<PrintShop>

    suspend fun getPrintShopById(id: String): PrintShop?
}
