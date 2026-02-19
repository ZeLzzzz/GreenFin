@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.financeaudit.presentation.view.auth

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.financeaudit.R
import com.example.financeaudit.presentation.navigation.Screen
import com.example.financeaudit.presentation.util.getGoogleSignInClient
import com.example.financeaudit.presentation.viewmodel.LoginState
import com.example.financeaudit.presentation.viewmodel.LoginViewModel
import com.example.financeaudit.presentation.ui.components.ButtonC
import com.example.financeaudit.presentation.ui.components.ButtonVariant
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(rootNavController: NavController, viewModel: LoginViewModel = hiltViewModel(), context: Context = LocalContext.current) {
    val loginState by viewModel.loginState.collectAsState()

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()
    ){ result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)

            account?.idToken?.let { token ->
                viewModel.handleGoogleLogin(token)
            }
        } catch (e: ApiException){
            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(loginState) {
        when(val state = loginState) {
            is LoginState.NavigateToDashboard -> {
                rootNavController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is LoginState.NavigateToInitialSetup -> {
                rootNavController.navigate(Screen.InitialSetup.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is LoginState.Error -> {
                val errorMsg = state.message
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Text(text = "Welcome to ", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Text(text = "GreenFin", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 30.sp)
        }

        Spacer(modifier = Modifier.height(13.dp))

        Text(text = "Securely manage and grow your wealth with our intuitive tools", color = Color.Gray, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(40.dp))

        ButtonC(
            onClick = {
                val googleClient = getGoogleSignInClient(context)
                launcher.launch(googleClient.signInIntent)
            },
            enabled = loginState !is LoginState.Loading,
            ) {
            if (loginState is LoginState.Loading){
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                )
            }
        }
    }
}