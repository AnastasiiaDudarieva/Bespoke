package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bespoke.app.BuildConfig
import com.bespoke.app.R
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.utils.privacyPolicy
import com.bespoke.app.utils.termsOfService

@Composable
fun SettingsScreen(
    navController: NavHostController,
) {
    SetStatusBarIconsDark(darkIcons = true)

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        BespokeTopBar(
            title = stringResource(R.string.settings),
            canNavigateBack = true,
            onBackClick = { navController.popBackStack() }
        )

        Column {

            SettingItem(title = stringResource(R.string.account)) {
                navController.navigate(Screen.ACCOUNT)
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)


            SettingItem(title = stringResource(R.string.notifications)) {
                context.startActivity(Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                })
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            SettingItem(title = stringResource(R.string.terms_and_conditions)) {
                navController.navigate(Screen.webViewWithUrl(Uri.encode(termsOfService)))
            }
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)


            SettingItem(title = stringResource(R.string.privacy_policy)) {
                navController.navigate(Screen.webViewWithUrl(Uri.encode(privacyPolicy)))

            }
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)


            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Version ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 24.dp, horizontal = 24.dp),
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun SettingItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 32.dp, horizontal = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = TextDark
        )
    }
}
