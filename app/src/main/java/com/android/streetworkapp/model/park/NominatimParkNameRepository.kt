package com.android.streetworkapp.model.park

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray

class NominatimParkNameRepository(val client: OkHttpClient) : ParkNameRepository {

  /**
   * Used to convert a LocationId into a human readable park name.
   *
   * @param onSuccess The callback to execute on success.
   * @param onFailure The callback to execute on failure.
   */
  override fun convertLocationIdToParkName(
      locationId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {

    // Based on the Nominatim API. More information: https://nominatim.org/

    val url =
        HttpUrl.Builder()
            .scheme("https")
            .host("nominatim.openstreetmap.org")
            .addPathSegment("lookup")
            .addQueryParameter("osm_ids", "W$locationId")
            .addQueryParameter("format", "json")
            .build()

    val request = Request.Builder().url(url).header("User-Agent", "testtest/5.0").build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {

                onSuccess(decodeRoadJson(response.body!!.string()))
              }
            })
  }

  fun decodeRoadJson(json: String): String {
    require(json.isNotEmpty())
    val jsonArray = JSONArray(json)
    val jsonObject = jsonArray.getJSONObject(0)
    val address = jsonObject.getJSONObject("address")
    val road = address.getString("road")
    return road
  }
}
