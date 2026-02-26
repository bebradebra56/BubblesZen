package com.bubbzeniac.apssof.egrf.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.bubbzeniac.apssof.egrf.presentation.di.bubblesZenModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


sealed interface BubblesZenAppsFlyerState {
    data object BubblesZenDefault : BubblesZenAppsFlyerState
    data class BubblesZenSuccess(val bubblesZenData: MutableMap<String, Any>?) :
        BubblesZenAppsFlyerState

    data object BubblesZenError : BubblesZenAppsFlyerState
}


private const val BUBBLES_ZEN_APP_DEV = "P8XDCutcrN9E5Vh22WCM9W"
private const val BUBBLES_ZEN_LIN = "com.bubbzeniac.apssof"

class BubblesZenApplication : Application() {

    private val bubblesZenKtorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
            requestTimeoutMillis = 30000
        }

    }


    private var bubblesZenIsResumed = false
//    private var bubblesZenConversionTimeoutJob: Job? = null
    private var bubblesZenDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        bubblesZenSetDebufLogger(appsflyer)
        bubblesZenMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        bubblesZenExtractDeepMap(p0.deepLink)
                        Log.d(BUBBLES_ZEN_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(BUBBLES_ZEN_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(BUBBLES_ZEN_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            BUBBLES_ZEN_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
//                    bubblesZenConversionTimeoutJob?.cancel()
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val response = bubblesZenKtorClient.get("https://gcdsdk.appsflyer.com/install_data/v4.0/$BUBBLES_ZEN_LIN") {
                                    parameter("devkey", BUBBLES_ZEN_APP_DEV)
                                    parameter("device_id", bubblesZenGetAppsflyerId())
                                }

                                val resp = response.body<MutableMap<String, JsonElement>?>()
                                val f = resp?.mapValues { (_, v) -> jsonElementToAny(v) }?.toMutableMap() ?: mutableMapOf()
                                Log.d(BUBBLES_ZEN_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status")?.jsonPrimitive?.content == "Organic" || resp?.get("af_status") == null) {
                                    bubblesZenResume(
                                        BubblesZenAppsFlyerState.BubblesZenError
                                    )
                                } else {
                                    bubblesZenResume(
                                        BubblesZenAppsFlyerState.BubblesZenSuccess(
                                            f
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(BUBBLES_ZEN_MAIN_TAG, "Error: ${d.message}")
                                bubblesZenResume(BubblesZenAppsFlyerState.BubblesZenError)
                            }
                        }
                    } else {
                        bubblesZenResume(BubblesZenAppsFlyerState.BubblesZenSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
//                    bubblesZenConversionTimeoutJob?.cancel()
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "onConversionDataFail: $p0")
                    bubblesZenResume(BubblesZenAppsFlyerState.BubblesZenError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, BUBBLES_ZEN_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(BUBBLES_ZEN_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(BUBBLES_ZEN_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
//        bubblesZenStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BubblesZenApplication)
            modules(
                listOf(
                    bubblesZenModule
                )
            )
        }
    }

    fun jsonElementToAny(element: JsonElement): Any {
        return when (element) {
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.booleanOrNull != null -> element.boolean
                    element.longOrNull != null -> element.long
                    element.doubleOrNull != null -> element.double
                    else -> element.content
                }
            }
            is JsonObject -> element.mapValues { (_, v) -> jsonElementToAny(v) }
            is JsonArray -> element.map { jsonElementToAny(it) }

        }
    }

    private fun bubblesZenExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(BUBBLES_ZEN_MAIN_TAG, "Extracted DeepLink data: $map")
        bubblesZenDeepLinkData = map
    }

//    private fun bubblesZenStartConversionTimeout() {
//        bubblesZenConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
//            delay(30000)
//            if (!bubblesZenIsResumed) {
//                Log.d(PLINK_ZEN_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
//                bubblesZenResume(PlinkZenAppsFlyerState.PlinkZenError)
//            }
//        }
//    }

    private fun bubblesZenResume(state: BubblesZenAppsFlyerState) {
//        bubblesZenConversionTimeoutJob?.cancel()
        if (state is BubblesZenAppsFlyerState.BubblesZenSuccess) {
            val convData = state.bubblesZenData ?: mutableMapOf()
            val deepData = bubblesZenDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!bubblesZenIsResumed) {
                bubblesZenIsResumed = true
                bubblesZenConversionFlow.value =
                    BubblesZenAppsFlyerState.BubblesZenSuccess(merged)
            }
        } else {
            if (!bubblesZenIsResumed) {
                bubblesZenIsResumed = true
                bubblesZenConversionFlow.value = state
            }
        }
    }

    private fun bubblesZenGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(BUBBLES_ZEN_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun bubblesZenSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun bubblesZenMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    companion object {

        var bubblesZenInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val bubblesZenConversionFlow: MutableStateFlow<BubblesZenAppsFlyerState> = MutableStateFlow(
            BubblesZenAppsFlyerState.BubblesZenDefault
        )
        var BUBBLES_ZEN_FB_LI: String? = null
        const val BUBBLES_ZEN_MAIN_TAG = "BubblesZenMainTag"
    }
}