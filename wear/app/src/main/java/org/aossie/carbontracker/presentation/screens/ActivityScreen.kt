package org.aossie.carbontracker.presentation.screens

import androidx.compose.foundation.background
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.services.client.data.ExerciseType
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.OutlinedButton
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.ScrollIndicator
import androidx.wear.compose.material3.ScrollIndicatorDefaults
import androidx.wear.compose.material3.Text
import org.aossie.carbontracker.data.activityInfo
import org.aossie.carbontracker.presentation.ui.ActivityChipBg
import org.aossie.carbontracker.presentation.ui.ActivityChipBorder
import org.aossie.carbontracker.presentation.ui.ActivityIconBg
import org.aossie.carbontracker.presentation.ui.PrimaryGreen
import org.aossie.carbontracker.presentation.ui.SecondaryText
import org.aossie.carbontracker.services.ExerciseManager
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import org.aossie.carbontracker.presentation.ui.Cream

@Composable
fun ActivityScreen(navController: NavController) {

    var activities by remember {
        mutableStateOf<List<ExerciseType>>(emptyList())
    }
    var isLoading by remember { mutableStateOf(true) }

    var retryCount by remember { mutableIntStateOf(0) }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    val context = LocalContext.current

    LaunchedEffect(retryCount) {
        isLoading = true
        activities = ExerciseManager(context).checkAvailableExercises()
        isLoading = false
    }

    ScreenScaffold(
        scrollState = listState,
        scrollIndicator = {
            ScrollIndicator(
                state = listState,
                colors = ScrollIndicatorDefaults.colors(
                    indicatorColor = Cream,
                    trackColor = Cream.copy(alpha = 0.2f)
                )
            )
        }
    ) { scaffoldPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = scaffoldPadding,
        ) {
            item {
                ListHeader(
                    modifier = Modifier.transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pick an activity",
                            fontSize = 12.sp,
                            color = Cream,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(1.dp)
                                .background(PrimaryGreen)
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                if (activities.isEmpty()) {
                    item {
                        Text(
                            text = "No supported activities found",
                            color = SecondaryText,
                            fontSize = 10.sp
                        )
                    }
                    item {
                        OutlinedButton(
                            onClick = { retryCount++ },
                            modifier = Modifier
                                .height(32.dp)
                                .padding(vertical = 2.dp)
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ActivityChipBg
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(
                                borderColor = ActivityChipBorder,
                                borderWidth = 1.dp,
                                enabled = true
                            ),
                            label = {
                                Text(
                                    text = "Retry",
                                    color = SecondaryText,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                    }

                } else {
                    items(activities.size) { i ->

                        val exercise = activities[i]

                        OutlinedButton(
                            onClick = { navController.navigate("stopwatch/${exercise.name}") },
                            icon = {
                                Icon(
                                    imageVector = activityInfo[exercise]?.icon
                                        ?: Icons.Default.FitnessCenter,
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
                            label = {
                                Text(
                                    activityInfo[exercise]?.name ?: "Unknown",
                                    fontSize = 12.sp,
                                    color = Cream
                                )
                            },
                            secondaryLabel = {
                                Text(
                                    text = activityInfo[exercise]?.intensity ?: "Unknown",
                                    fontSize = 10.sp,
                                    color = SecondaryText
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                ActivityChipBg
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
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
    }
}
