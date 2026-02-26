package com.bubbzeniac.apssof.egrf.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private const val BUBBLES_ZEN_A = "com.bubbzeniac.apssof"
private const val BUBBLES_ZEN_B = "bubbleszen-5d2b8"
@Serializable
data class BubblesZenParam (
    @SerialName("af_id")
    val bubblesZenAfId: String,
    @SerialName("bundle_id")
    val bubblesZenBundleId: String = BUBBLES_ZEN_A,
    @SerialName("os")
    val bubblesZenOs: String = "Android",
    @SerialName("store_id")
    val bubblesZenStoreId: String = BUBBLES_ZEN_A,
    @SerialName("locale")
    val bubblesZenLocale: String,
    @SerialName("push_token")
    val bubblesZenPushToken: String,
    @SerialName("firebase_project_id")
    val bubblesZenFirebaseProjectId: String = BUBBLES_ZEN_B,
    )