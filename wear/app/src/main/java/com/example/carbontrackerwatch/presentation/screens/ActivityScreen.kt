package com.example.carbontrackerwatch.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.OutlinedButton
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.example.carbontrackerwatch.data.activities
import com.example.carbontrackerwatch.presentation.ui.ActivityChipBg
import com.example.carbontrackerwatch.presentation.ui.ActivityChipBorder
import com.example.carbontrackerwatch.presentation.ui.ActivityIconBg
import com.example.carbontrackerwatch.presentation.ui.PrimaryGreen
import com.example.carbontrackerwatch.presentation.ui.SecondaryText

@Composable
fun ActivityScreen(navController: NavController) {

    ScreenScaffold { contentPadding ->
        TransformingLazyColumn(
            contentPadding = contentPadding
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pick an activity",
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .height(1.dp)
                            .background(PrimaryGreen)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(activities.size) { i ->
                OutlinedButton(
                    onClick = { navController.navigate("stopwatch") },
                    icon = {
                        Icon(
                            imageVector = activities[i].icon,
                            contentDescription = "Activity Icon",
                            tint = PrimaryGreen,
                            modifier = Modifier
                                .background(
                                    ActivityIconBg,
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(5.dp)

                        )
                    },
                    label = { Text(activities[i].name, fontSize = 12.sp, color = Color.Black) },
                    secondaryLabel = {
                        Text(
                            text = activities[i].intensity,
                            fontSize = 10.sp,
                            color = SecondaryText
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        ActivityChipBg
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    border = ButtonDefaults.outlinedButtonBorder(
                        borderColor = ActivityChipBorder,
                        borderWidth = 1.dp,
                        enabled = true
                    )
                )
            }
        }
    }
}
