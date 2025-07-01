package com.bespoke.app.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.R
import com.bespoke.app.data.viewmodel.HomeViewModel
import com.bespoke.app.ui.components.base.CustomAvatar
import com.bespoke.app.ui.theme.AfternoonGradient
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.EveningGradient
import com.bespoke.app.ui.theme.MorningGradient
import com.bespoke.app.ui.theme.TextDark
import java.util.Calendar

internal val imageBitmapCache = mutableStateMapOf<String, Bitmap?>()

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
) {
    val salutation = Salutation.current
    val gradient = Brush.verticalGradient(colors = salutation.gradientColors)
    val homeViewModel: HomeViewModel = hiltViewModel()
    val member by homeViewModel.member.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo",
                modifier = Modifier.height(24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            member?.let {
                CustomAvatar(
                    url = it.avatar ?: "",
                    firstName = it.firstName ?: "",
                    lastName = it.lastName ?: "",
                    size = 48.dp,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("profile")
                        }
                )
            }

        }

        Text(
            text = stringResource(salutation.greetingResId, "Anastasiia"),
            style = MaterialTheme.typography.headlineLarge,
            color = TextDark,
            fontFamily = BeatriceFontFamily
        )
    }
}

enum class Salutation(val greetingResId: Int, val gradientColors: List<Color>) {
    Morning(
        greetingResId = R.string.good_morning,
        gradientColors = MorningGradient
    ),
    Afternoon(
        greetingResId = R.string.good_afternoon,
        gradientColors = AfternoonGradient
    ),
    Evening(
        greetingResId = R.string.good_evening,
        gradientColors = EveningGradient
    );

    companion object {
        val current: Salutation
            get() {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                return when (hour) {
                    in 6..11 -> Morning
                    in 12..17 -> Afternoon
                    else -> Evening
                }
            }
    }
}
