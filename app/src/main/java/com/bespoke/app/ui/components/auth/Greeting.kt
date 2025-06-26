package com.bespoke.app.ui.components.auth

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bespoke.app.R
import com.bespoke.app.ui.models.BottomPanelContent
import com.bespoke.app.ui.theme.BeatriceFontFamily

@Composable
fun Greeting(state: BottomPanelContent, modifier: Modifier = Modifier) {
    val view = LocalView.current
    val imeVisible = rememberImeVisibility(view)

    if (!imeVisible) {
        val title: Int
        val subtitle: Int
        val titleStyle: TextStyle
        val subtitleStyle: TextStyle
        val space: Dp
        val bottomPadding: Dp

        when (state) {
            BottomPanelContent.Welcome -> {
                title = R.string.welcome_title
                subtitle = R.string.welcome_subtitle
                titleStyle = MaterialTheme.typography.headlineSmall
                subtitleStyle = MaterialTheme.typography.headlineLarge
                space = 28.dp
                bottomPadding = 32.dp
            }

            BottomPanelContent.Login,
            BottomPanelContent.RequestInvite,
            BottomPanelContent.ForgotPassword -> {
                title = when (state) {
                    BottomPanelContent.Login -> R.string.login_title
                    BottomPanelContent.RequestInvite -> R.string.invite_title
                    BottomPanelContent.ForgotPassword -> R.string.forgot_password_title
                    else -> R.string.login_title
                }
                subtitle = R.string.invite_subtitle
                titleStyle = MaterialTheme.typography.bodyLarge
                subtitleStyle = MaterialTheme.typography.headlineSmall
                space = 16.dp
                bottomPadding = 48.dp
            }
        }

        Column(modifier = modifier) {
            AnimatedContent(
                targetState = stringResource(id = title),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = stringResource(id = title)
            ) { animatedTitle ->
                Text(
                    text = animatedTitle,
                    style = titleStyle,
                    fontFamily = BeatriceFontFamily,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(space))
            AnimatedContent(
                targetState = stringResource(id = subtitle),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = stringResource(id = subtitle)
            ) { animatedSubtitle ->
                Text(
                    text = animatedSubtitle,
                    style = subtitleStyle,
                    fontFamily = BeatriceFontFamily,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(bottomPadding))
        }
    }
}
@Composable
fun rememberImeVisibility(view: android.view.View): Boolean {
    val imeVisibleState = remember { mutableStateOf(false) }

    LaunchedEffect(view) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val isVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            imeVisibleState.value = isVisible
            insets
        }
    }

    return imeVisibleState.value
}
