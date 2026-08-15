package uz.myprint.feature.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintElevation
import uz.myprint.core.designsystem.theme.components.MyPrintLogo

@Composable
fun HomeHeader(

    notificationCount: Int = 3,

    onMenuClick: () -> Unit = {},

    onNotificationClick: () -> Unit = {}

) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(
                start = 20.dp,
                end = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            modifier = Modifier.size(44.dp),
            onClick = onMenuClick
        ) {

            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(26.dp)
            )

        }

        Spacer(modifier = Modifier.width(4.dp))

        MyPrintLogo(
            modifier = Modifier.weight(1f),
            showSlogan = true
        )

        BadgedBox(
            badge = {

                if (notificationCount > 0) {

                    Badge {
                        Text(notificationCount.toString())
                    }

                }

            }
        ) {

            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = MyPrintElevation.md,
                shadowElevation = MyPrintElevation.lg
            ) {

                IconButton(
                    onClick = onNotificationClick
                ) {

                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(22.dp)
                    )

                }

            }

        }

    }

}