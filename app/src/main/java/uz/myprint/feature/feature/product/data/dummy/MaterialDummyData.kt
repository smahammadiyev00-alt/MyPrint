package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.ProductMaterial

object MaterialDummyData {

    // Business Card
    val art300 = ProductMaterial(
        id = "art300",
        name = "Art Paper",
        thickness = "300g",
        description = "Premium coated paper",
        isDefault = true
    )

    val art350 = ProductMaterial(
        id = "art350",
        name = "Art Paper",
        thickness = "350g",
        description = "Extra thick coated paper"
    )

    val kraft = ProductMaterial(
        id = "kraft",
        name = "Kraft Paper",
        thickness = "300g",
        description = "Natural kraft paper"
    )

    val laminated = ProductMaterial(
        id = "laminated",
        name = "Laminated Paper",
        thickness = "350g",
        description = "Matte or glossy laminated"
    )

    // Banner
    val banner440 = ProductMaterial(
        id = "banner440",
        name = "Banner",
        thickness = "440g",
        description = "Standard outdoor banner",
        isDefault = true
    )

    val banner510 = ProductMaterial(
        id = "banner510",
        name = "Banner",
        thickness = "510g",
        description = "Heavy duty banner"
    )

    val blockout = ProductMaterial(
        id = "blockout",
        name = "Blockout Banner",
        thickness = "610g",
        description = "Double sided banner"
    )

    // T-Shirt
    val cotton100 = ProductMaterial(
        id = "cotton100",
        name = "Cotton",
        thickness = "100%",
        description = "Premium cotton",
        isDefault = true
    )

    val polyester = ProductMaterial(
        id = "polyester",
        name = "Polyester",
        thickness = "100%",
        description = "Sports fabric"
    )

}