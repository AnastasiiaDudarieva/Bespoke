package com.bespoke.app.ui.components.auth

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.bespoke.app.R
import com.bespoke.app.ui.components.base.BespokeButton
import com.bespoke.app.ui.components.base.BespokeInput
import com.bespoke.app.ui.components.base.ClickableUnderlinedText
import com.bespoke.app.ui.models.AuthState
import com.bespoke.app.ui.models.BottomPanelContent
import com.bespoke.app.ui.theme.BespokeButtonCancelColors
import com.bespoke.app.viewmodel.AuthViewModel

@Composable
fun BottomPanel(
    state: BottomPanelContent,
    onChangeState: (BottomPanelContent) -> Unit,
    viewModel: AuthViewModel,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        var email by rememberSaveable { mutableStateOf("") }
        var emailRequest by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }

        when (state) {
            BottomPanelContent.Welcome -> {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .fillMaxWidth()
                ) {
                    BespokeButton(
                        onClick = { onChangeState(BottomPanelContent.Login) },
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.login_button)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ClickableUnderlinedText(
                        text = stringResource(R.string.request_invite),
                        onClick = { onChangeState(BottomPanelContent.RequestInvite) }
                    )
                }
            }

            BottomPanelContent.Login -> {

                Column(
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(64.dp))
                    BespokeInput(
                        value = email,
                        onValueChange = { email = it },
                        label = stringResource(R.string.email_hint),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    BespokeInput(
                        value = password,
                        onValueChange = { password = it },
                        label = stringResource(R.string.password_hint),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ClickableUnderlinedText(
                        text = stringResource(R.string.forgot_password),
                        onClick = { onChangeState(BottomPanelContent.ForgotPassword) }
                    )
                }

                BespokeButton(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    text = stringResource(R.string.submit)
                )
            }

            BottomPanelContent.RequestInvite,
            BottomPanelContent.ForgotPassword,
                -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f, fill = true),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BespokeInput(
                            value = emailRequest,
                            onValueChange = { emailRequest = it },
                            label = stringResource(R.string.email_hint),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            BespokeButton(
                                onClick = {
                                    if (state == BottomPanelContent.RequestInvite)
                                        onChangeState(BottomPanelContent.Welcome)
                                    else
                                        onChangeState(BottomPanelContent.Login)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .height(56.dp)
                                    .padding(end = 6.dp),
                                text = stringResource(android.R.string.cancel),
                                colors = BespokeButtonCancelColors
                            )

                            BespokeButton(
                                onClick = {
                                    if (state == BottomPanelContent.RequestInvite)
                                    //TODO: Add invite logic
                                    else
                                        viewModel.resetPassword(
                                            email = emailRequest
                                        )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .height(56.dp)
                                    .padding(start = 6.dp),
                                text = stringResource(R.string.submit)
                            )
                        }
                    }
                }
            }
        }

        val authState by viewModel.authState.collectAsState()

        LaunchedEffect(authState) {
            when (authState) {
                is AuthState.Message -> {
                    if (state == BottomPanelContent.ForgotPassword) {
                        onChangeState(BottomPanelContent.Login)
                    }
                }
                else -> {}
            }
        }
    }
}