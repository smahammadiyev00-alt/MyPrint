package uz.myprint.feature.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import uz.myprint.R
import uz.myprint.core.di.AppContainer
import uz.myprint.feature.feature.design.studio.data.SavedProject
import uz.myprint.feature.feature.design.studio.data.updatedLabel
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.project.ProjectSection
import uz.myprint.feature.feature.project.components.ProjectActionsSheet
import uz.myprint.feature.feature.project.model.Project
import uz.myprint.feature.feature.project.model.ProjectCategory

@Composable
fun HomeProjectSection(
    onProjectClick: (Project) -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onCreateClick: () -> Unit = {}
) {

    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }

    // Uch nuqta bosilgan loyiha. null — menyu yopiq.
    var menuProject by remember { mutableStateOf<Project?>(null) }

    val scope = rememberCoroutineScope()

    val store = AppContainer.projectStore

    val lifecycleOwner = LocalLifecycleOwner.current

    // Ro'yxatni qayta o'qish. O'chirish yoki nom o'zgartirishdan
    // keyin chaqiriladi — ekran qayta yaratilishini kutmaydi.
    suspend fun reload() {
        projects = store.list().map { it.toProject() }
    }

    // Ro'yxat har safar ekran ko'ringanda qayta o'qiladi.
    //
    // LaunchedEffect(Unit) yetarli emas: foydalanuvchi studiodan
    // qaytganda bosh sahifa qayta yaratilmaydi, faqat RESUMED
    // holatiga qaytadi. Shunda yangi loyiha ro'yxatda ko'rinmay
    // qolardi va foydalanuvchi ishi yo'qolgan deb o'ylardi.
    LaunchedEffect(lifecycleOwner) {

        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            reload()
        }
    }

    ProjectActionsSheet(
        project = menuProject,
        onDismiss = { menuProject = null },
        onOpen = onProjectClick,
        onRename = { project, name ->
            scope.launch {
                store.rename(project.id, name)
                reload()
            }
        },
        onDuplicate = { project ->
            scope.launch {
                store.duplicate(project.id, "${project.title} (nusxa)")
                reload()
            }
        },
        onDelete = { project ->
            scope.launch {
                store.delete(project.id)
                reload()
            }
        }
    )

    if (projects.isEmpty()) {

        EmptyProjectsCard(onCreateClick = onCreateClick)

        return
    }

    ProjectSection(
        projects = projects,
        onProjectClick = onProjectClick,
        onMenuClick = { menuProject = it },
        onSeeAllClick = onSeeAllClick
    )
}

/**
 * Hali loyiha yo'q holati.
 *
 * Bo'sh joy qoldirish yomon: foydalanuvchi bo'lim buzilgan deb
 * o'ylaydi. Buning o'rniga nima qilish kerakligi aytiladi.
 */
@Composable
private fun EmptyProjectsCard(onCreateClick: () -> Unit) {

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        Text(
            text = "Loyihalaringiz",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF1EEFF))
                .clickable { onCreateClick() }
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Rounded.AddCircleOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "Birinchi maketingizni yarating",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Mahsulot tanlang va dizayn studiyasida ishlang",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Ombordagi yozuvni UI modeliga aylantiradi.
 *
 * Ikki xil kategoriya enum'i bor va ular ataylab ajratilgan:
 * ProductCategory katalogga tegishli, ProjectCategory esa
 * ko'rinishga. Ularni birlashtirsak, katalogga yangi tur
 * qo'shilganda UI ham buzilardi.
 */
private fun SavedProject.toProject() = Project(
    id = id,
    title = title,
    category = category.toProjectCategory(),
    imageRes = category.fallbackImage(),
    coverPath = coverPath,
    updatedAt = updatedLabel(),
    productId = productId,
    sizeId = sizeId
)

private fun ProductCategory.toProjectCategory(): ProjectCategory =
    when (this) {
        ProductCategory.BUSINESS_CARD -> ProjectCategory.VIZITKA
        ProductCategory.BANNER -> ProjectCategory.BANNER
        ProductCategory.T_SHIRT -> ProjectCategory.FUTBOLKA
        ProductCategory.STICKER -> ProjectCategory.STICKER
        ProductCategory.FLYER -> ProjectCategory.FLYER
        ProductCategory.BOOKLET -> ProjectCategory.BOOKLET
        ProductCategory.ROLL_UP -> ProjectCategory.ROLLUP
        ProductCategory.X_BANNER -> ProjectCategory.ROLLUP
        ProductCategory.MUG -> ProjectCategory.MUG
        else -> ProjectCategory.VIZITKA
    }

private fun ProductCategory.fallbackImage(): Int = when (this) {
    ProductCategory.BANNER,
    ProductCategory.ROLL_UP,
    ProductCategory.X_BANNER -> R.drawable.product_banner

    ProductCategory.MUG -> R.drawable.product_bakal

    else -> R.drawable.product_vizitka
}