package uz.myprint.feature.feature.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.project.components.ProjectCard
import uz.myprint.feature.feature.project.model.Project

@Composable
fun ProjectSection(
    projects: List<Project>,
    modifier: Modifier = Modifier,
    onProjectClick: (Project) -> Unit = {},
    onMenuClick: (Project) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Rounded.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Loyihalaringiz",
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onSeeAllClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Barchasi")

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 18.dp,
                top = 8.dp,
                end = 18.dp,
                bottom = 4.dp
            )
        ){

            items(
                items = projects,
                key = { it.id }
            ) { project ->

                ProjectCard(
                    project = project,
                    onContinueClick = onProjectClick,
                    onMenuClick = onMenuClick
                )
            }
        }
    }
}