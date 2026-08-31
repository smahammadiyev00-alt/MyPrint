package uz.myprint.feature.feature.promotion.model

import androidx.annotation.DrawableRes

data class Partner(
    val id: String,
    val name: String,
    @param:DrawableRes
    val coverImage: Int
)