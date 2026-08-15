package uz.myprint.core.designsystem.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.myprint.R
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.core.designsystem.theme.MyPrintDimensions

@Composable
fun MyPrintLogo(
    modifier: Modifier = Modifier,
    showSlogan: Boolean = true
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "MyPrint Logo",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(2.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "MyPrint",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary,
                maxLines = 1
            )

            if (showSlogan) {

                Text(
                    text = "Sifat va qulaylik garovi!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MyPrintColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

            }

        }

    }

}