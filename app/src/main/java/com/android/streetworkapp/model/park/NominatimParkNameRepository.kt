package com.android.streetworkapp.model.park

import okhttp3.OkHttpClient

class NominatimParkNameRepository(val client: OkHttpClient): ParkNameRepository {
    override fun convertLocationIdToParkName(
        locationId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}