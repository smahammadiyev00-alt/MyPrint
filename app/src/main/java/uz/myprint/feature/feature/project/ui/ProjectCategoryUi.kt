package uz.myprint.feature.feature.project.ui

import androidx.compose.ui.graphics.Color
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.project.model.ProjectCategory

/**
 * UI extensions for ProjectCategory.
 *
 * Keeps business models free from UI-related logic.
 */

fun ProjectCategory.displayName(): String = when (this) {
    ProjectCategory.VIZITKA -> "Vizitka"
    ProjectCategory.BANNER -> "Banner"
    ProjectCategory.FUTBOLKA -> "Futbolka"
    ProjectCategory.STICKER -> "Sticker"
    ProjectCategory.FLYER -> "Flyer"
    ProjectCategory.BOOKLET -> "Booklet"
    ProjectCategory.ROLLUP -> "Roll Up"
    ProjectCategory.MUG -> "Mug"
}

fun ProjectCategory.badgeColor(): Color = when (this) {
    ProjectCategory.VIZITKA -> MyPrintColors.Primary
    ProjectCategory.BANNER -> Color(0xFF43A047)
    ProjectCategory.FUTBOLKA -> Color(0xFFFF9800)
    ProjectCategory.STICKER -> Color(0xFFE91E63)
    ProjectCategory.FLYER -> Color(0xFF8E24AA)
    ProjectCategory.BOOKLET -> Color(0xFF00897B)
    ProjectCategory.ROLLUP -> Color(0xFFF57C00)
    ProjectCategory.MUG -> Color(0xFF1E88E5)
}

fun ProjectCategory.progressColor(): Color = badgeColor()