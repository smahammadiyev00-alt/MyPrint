package uz.myprint.feature.feature.printshop.data.repository

import kotlinx.coroutines.delay
import uz.myprint.feature.feature.printshop.data.dummy.PrintShopDummyData
import uz.myprint.feature.feature.printshop.domain.model.PrintShop
import uz.myprint.feature.feature.printshop.domain.repository.PrintShopRepository

/**
 * Vaqtinchalik implementatsiya. Supabase ulanganda faqat shu klass
 * almashadi — use case, ViewModel va UI o'zgarmaydi.
 */
class PrintShopRepositoryImpl : PrintShopRepository {

    override suspend fun getPrintShops(): List<PrintShop> {

        // Tarmoq kechikishini taqlid qilamiz, shunda yuklanish
        // holati UI'da haqiqiy ko'rinadi.
        delay(300)

        return PrintShopDummyData.shops
    }

    override suspend fun getPrintShopById(id: String): PrintShop? {

        return PrintShopDummyData.shops.firstOrNull { it.id == id }
    }
}
