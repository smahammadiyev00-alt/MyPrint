package uz.myprint.feature.feature.project.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.project.model.Project
import uz.myprint.feature.feature.project.ui.badgeColor
import uz.myprint.feature.feature.project.ui.displayName

@Composable
fun ProjectCard(
    project: Project,
    modifier: Modifier = Modifier,
    onContinueClick: (Project) -> Unit = {},
    onMenuClick: (Project) -> Unit = {}
) {

    Card(
        modifier = modifier.width(285.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column {

            ProjectCover(
                imageRes = project.imageRes,
                coverPath = project.coverPath,
                badgeText = project.category.displayName(),
                badgeColor = project.category.badgeColor(),
                onMenuClick = {
                    onMenuClick(project)
                }
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ProjectInfo(
                    title = project.title,
                    updatedAt = project.updatedAt
                )

                ProjectActionButton(
                    onClick = {
                        onContinueClick(project)
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}