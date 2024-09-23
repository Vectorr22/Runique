package com.plcoding.runique

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.plcoding.auth.presentation.intro.IntroScreenRoot
import com.plcoding.auth.presentation.login.LoginScreenRoot
import com.plcoding.auth.presentation.register.RegisterScreenRoot
import com.vectorr22.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination =  if(isLoggedIn) "run" else "auth"
    ){
        authGraph(navController)
        runGraph(navController)
    }

}

private fun NavGraphBuilder.authGraph(navController: NavHostController){
    navigation(
        startDestination = "intro",
        route = "auth"
    ){
        composable(route = "intro"){
            IntroScreenRoot(
                onSignUpClicked = {
                    navController.navigate("register")
                },
                onSignInClicked = {
                    navController.navigate("login")
                }
            )
        }

        composable(route = "register"){
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate("login"){
                        popUpTo("register"){
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate("login")
                }
            )
        }

        composable(route = "login"){
            LoginScreenRoot(
                onLogInSuccess = {
                    navController.navigate("run"){
                        popUpTo("auth"){
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate("register"){
                        popUpTo("login"){
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(navController: NavHostController){
    navigation(
        startDestination = "run_overview",
        route = "run"
    ){
        composable("run_overview"){
            RunOverviewScreenRoot()
        }
    }
}