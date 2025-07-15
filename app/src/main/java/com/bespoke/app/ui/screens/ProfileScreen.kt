package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.R
import com.bespoke.app.ui.viewmodel.HomeViewModel
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.components.base.ClickableUnderlinedText
import com.bespoke.app.ui.components.base.CustomAvatar
import com.bespoke.app.ui.components.profile.StatsCard
import com.bespoke.app.ui.models.profile.ProfileStatsType
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.TextDark
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    SetStatusBarIconsDark(darkIcons = true)

    val homeViewModel: HomeViewModel = hiltViewModel()
    val member by homeViewModel.member.collectAsState()
    val isUploadingAvatar by homeViewModel.isUploadingAvatar.collectAsState()


    val context = LocalContext.current
    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, croppedUri)
            homeViewModel.uploadProfileImage(bitmap)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            cropImageLauncher.launch(
                CropImageContractOptions(
                    uri,
                    CropImageOptions().apply {
                        aspectRatioX = 1
                        aspectRatioY = 1
                        fixAspectRatio = true
                        guidelines = CropImageView.Guidelines.ON
                    }
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            BespokeTopBar(
                title = stringResource(R.string.profile_title),
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() }
            )
            Column(modifier = Modifier.padding(24.dp, 8.dp, 24.dp, 32.dp)) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member?.fullName ?: "",
                            fontFamily = BeatriceFontFamily,
                            color = TextDark,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = stringResource(
                                R.string.joined_at,
                                "${member?.createdAt?.toFormattedDate()}"
                            ),
                            fontFamily = BeatriceFontFamily,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
                        )
                        Text(
                            text = stringResource(R.string.logout),
                            modifier = modifier
                                .clickable { homeViewModel.logout() },
                            style = TextStyle(
                                color = BespokeBlue,
                                fontFamily = BeatriceFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.Underline
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Box {
                        CustomAvatar(
                            url = member?.avatar,
                            firstName = member?.firstName,
                            lastName = member?.lastName,
                            size = 120.dp
                        )
                        IconButton(
                            onClick = {
                                pickImageLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(BespokeBlue, CircleShape)
                                .size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pencil),
                                contentDescription = "Edit Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            // Bottom section
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)) {
                Text(
                    text = stringResource(R.string.my_stats),
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = BeatriceFontFamily,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatsCard(ProfileStatsType.Streak, value = "13 Days")
                Spacer(modifier = Modifier.height(8.dp))
                StatsCard(ProfileStatsType.Programs, value = "44 Complete")
                Spacer(modifier = Modifier.height(8.dp))
                StatsCard(ProfileStatsType.Calories, value = "13,288 Burned")
                Spacer(modifier = Modifier.height(8.dp))
                StatsCard(ProfileStatsType.WorkoutTime, value = "1400 Min")

                Spacer(modifier = Modifier.height(32.dp))

                ClickableUnderlinedText(
                    text = stringResource(R.string.settings),
                    onClick = { navController.navigate(Screen.SETTINGS) })
            }
        }

        if (isUploadingAvatar) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

fun Int.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return sdf.format(Date(this * 1000L))
}

