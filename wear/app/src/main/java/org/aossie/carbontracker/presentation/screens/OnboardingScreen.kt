package org.aossie.carbontracker.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.navigation.NavController
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import org.aossie.carbontracker.presentation.ui.OnPrimary
import org.aossie.carbontracker.presentation.ui.PrimaryGreen

@Composable
fun OnboardingScreen(
    navController: NavController
) {
    ScreenScaffold(timeText = {
        TimeText(
        )
    }) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = "Leaf",
                tint = PrimaryGreen,
                modifier = Modifier
                    .size(50.dp)
                    .padding(bottom = 8.dp)
            )


            Text(
                text = "CarbonTracker",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Ready to make your move for the planet?",
                textAlign = TextAlign.Center,
                color = PrimaryGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Thin,
                modifier = Modifier.padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
            )
            Button(
                onClick = { navController.navigate("activity") },
                modifier = Modifier
                    .height(40.dp),
            ) {
                Text(text = "RECORD ACTIVITY", fontSize = 10.sp, color = OnPrimary)
            }
        }
    }
}
