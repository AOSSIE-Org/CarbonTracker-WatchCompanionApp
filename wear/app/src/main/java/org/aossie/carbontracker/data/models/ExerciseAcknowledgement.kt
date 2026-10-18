package org.aossie.carbontracker.data.models

import kotlinx.serialization.Serializable

@Serializable

data class ExerciseAcknowledgement(

    val id: Long,

    val lastUpdated: Long

)
