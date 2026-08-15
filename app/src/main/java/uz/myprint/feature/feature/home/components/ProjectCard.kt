package uz.myprint.feature.project.components

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

/**
 * Represents a single user project.
 */
data class Project(
    val id: String,
    val title: String,
    val category: ProjectCategory,

    @DrawableRes
    val imageRes: Int,

    /**
     * Value from 0f..1f
     */
    val progress: Float,

    /**
     * Example:
     * "2 soat oldin tahrirlangan"
     */
    val updatedAt: String
)

/**
 * Project categories used inside MyPrint.
 */
enum class ProjectCategory(
    val title: String,
    val badgeColor: Color,
    val progressColor: Color
) {

    VIZITKA(
        title = "VIZITKA",
        badgeColor = Color(0xFF635BFF),
        progressColor = Color(0xFF635BFF)
    ),

    BANNER(
        title = "BANNER",
        badgeColor = Color(0xFF43A047),
        progressColor = Color(0xFF43A047)
    ),

    FUTBOLKA(
        title = "FUTBOLKA",
        badgeColor = Color(0xFFFF9800),
        progressColor = Color(0xFFFF9800)
    ),

    STICKER(
        title = "STICKER",
        badgeColor = Color(0xFFE91E63),
        progressColor = Color(0xFFE91E63)
    ),

    FLYER(
        title = "FLYER",
        badgeColor = Color(0xFF8E24AA),
        progressColor = Color(0xFF8E24AA)
    ),

    BOOKLET(
        title = "BOOKLET",
        badgeColor = Color(0xFF00897B),
        progressColor = Color(0xFF00897B)
    ),

    ROLLUP(
        title = "ROLL UP",
        badgeColor = Color(0xFFF57C00),
        progressColor = Color(0xFFF57C00)
    ),

    MUG(
        title = "MUG",
        badgeColor = Color(0xFF1E88E5),
        progressColor = Color(0xFF1E88E5)
    )
}

/**
 * Converts project progress to percentage.
 */
val Project.progressPercent: Int
    get() = (progress * 100)
        .toInt()
        .coerceIn(0, 100)