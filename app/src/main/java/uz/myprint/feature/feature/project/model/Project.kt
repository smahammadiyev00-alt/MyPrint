package uz.myprint.feature.feature.project.model

import androidx.annotation.DrawableRes

/**
 * Represents a user project in MyPrint.
 *
 * Contains only business data.
 * No UI logic should be placed here.
 */
data class Project(

    /**
     * Unique project identifier.
     */
    val id: String,

    /**
     * Project title.
     */
    val title: String,

    /**
     * Project category.
     */
    val category: ProjectCategory,

    /**
     * Project cover image.
     */
    @DrawableRes
    val imageRes: Int,

    /**
     * Example:
     * "2 soat oldin tahrirlangan"
     */
    val updatedAt: String
)