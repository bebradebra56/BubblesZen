package com.bubbzeniac.apssof.egrf.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BubblesZenEntity (
    @SerialName("ok")
    val bubblesZenOk: Boolean,
    @SerialName("url")
    val bubblesZenUrl: String,
    @SerialName("expires")
    val bubblesZenExpires: Long,
)