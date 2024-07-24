package com.karin.appcrashlytics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import com.karin.appcrashlytics.ui.theme.AppCrashlyticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCrashlyticsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController, modifier)
        }
        composable("pagina/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            PaginaScreen(userId)
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Prueba Crash 1",
            //Evento crash forzado
            modifier = modifier.clickable { throw RuntimeException("Prueba Crash en pagina principal") }
        )
        Button(onClick = {
            val userId = "2222"
            navController.navigate("pagina/$userId")
        }) {
            Text("Enviar userId")
        }
    }
}

@Composable
fun PaginaScreen(userId: String) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pagina 3")
        Text(
            text = "Hola $userId",
            modifier = Modifier.clickable {
                val crashlytics = FirebaseCrashlytics.getInstance()
                //Enviar eventos personalizados
                crashlytics.setUserId(userId)
                crashlytics.setCustomKey("texto", 50.5)
                crashlytics.setCustomKeys {
                    key("texto1", "texto")
                    key("texto2", 1)
                    key("texto3", 2.5)
                }
                //Enviar log de contexto
                crashlytics.log("Esto es un evento tipo log2")

                //Enviar un evento forzado
                //throw RuntimeException("Prueba Crash en pagina 2")
            })
        Text(
            text = "Hola2 $userId",
            modifier = Modifier.clickable {
                val crashlytics = FirebaseCrashlytics.getInstance()
                //Enviar eventos personalizados
                crashlytics.setUserId(userId)
                crashlytics.setCustomKey("sd", 10.5)
                crashlytics.setCustomKeys {
                    key("sd", "texto10")
                    key("sd", 10)
                    key("sd", 20.5)
                }
                //Enviar log de contexto
                crashlytics.log("Esto es un evento tipo log3")

                //Enviar un evento forzado
                throw RuntimeException("Prueba Crash en pagina 3")
            })
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppCrashlyticsTheme {
        App()
    }
}