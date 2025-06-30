package com.bespoke.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bespoke.app.R
import com.bespoke.app.data.viewmodel.HomeViewModel
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.components.base.ClickableUnderlinedText
import com.bespoke.app.ui.components.base.CustomAvatar
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.NavBarBackgroundColor
import com.bespoke.app.ui.theme.TextDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeViewModel:HomeViewModel = hiltViewModel()
    val member by homeViewModel.member.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top section
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "Profile",
                fontFamily = BeatriceFontFamily,
                fontSize = 28.sp,
                color = TextDark,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.padding(top = 32.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member?.firstName?:"",
                        fontFamily = BeatriceFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        color = TextDark
                    )
                    Text(
                        text = member?.lastName?:"",
                        fontFamily = BeatriceFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        color = TextDark
                    )
                    Text(
                        text = "Joined: ${member?.createdAt?.toFormattedDate()}",
                        fontFamily = BeatriceFontFamily,
                        fontSize = 14.sp,
                        color = TextDark.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    ClickableUnderlinedText(
                        text = "Logout",
                        onClick = onLogout,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }

                Box {
                    CustomAvatar(
                        url = member?.avatar,
                        firstName = member?.firstName,
                        lastName = member?.lastName,
                        size = 120.dp
                    )
                    IconButton (
                        onClick = onAvatarClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 12.dp, y = (-12).dp)
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

        Divider(modifier = Modifier.padding(top = 32.dp), thickness = 0.5.dp, color = Color.Gray)

        // Bottom section
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)) {
            Text(
                text = "My Stats",
                fontFamily = BeatriceFontFamily,
                fontSize = 24.sp,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatsCard(title = "Streak", value = "13 Days")
            StatsCard(title = "Programs", value = "44 Complete")
            StatsCard(title = "Calories", value = "13,288 Burned")
            StatsCard(title = "Workout Time", value = "1400 Min")

            Spacer(modifier = Modifier.height(16.dp))

            ClickableUnderlinedText (text = "Settings", onClick = onOpenSettings)
        }
    }
}

@Composable
fun StatsCard(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBarBackgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontFamily = BeatriceFontFamily,
            fontSize = 14.sp,
            color = TextDark
        )
        Text(
            text = value,
            fontFamily = BeatriceFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
        )
    }
}

fun Int.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return sdf.format(Date(this * 1000L))
}

