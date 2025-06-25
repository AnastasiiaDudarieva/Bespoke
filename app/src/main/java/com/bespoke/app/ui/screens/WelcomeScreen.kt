package com.bespoke.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.bespoke.app.R
import com.bespoke.app.ui.components.auth.BottomPanel
import com.bespoke.app.ui.components.auth.Greeting
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.models.BottomPanelContent
import com.bespoke.app.ui.theme.BespokeBlue

@Preview(showBackground = false)
@Composable
fun WelcomeScreen() {
    var panelState by remember { mutableStateOf<BottomPanelContent>(BottomPanelContent.Welcome) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(BespokeBlue)) {
        Image(
            painter = painterResource(id = R.drawable.line_variants),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                BespokeTopBar(
                    canNavigateBack = panelState != BottomPanelContent.Welcome,
                    onBackClick = { panelState = BottomPanelContent.Welcome }
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bespoke_wordmark),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            top = 128.dp
                        )
                )

                Spacer(modifier = Modifier.weight(1f))

                Greeting(
                    state = panelState,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                BottomPanel(
                    state = panelState,
                    onChangeState = { panelState = it }
                )
            }
        }
    }
}
