package com.android.streetworkapp.model.parks

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class OverpassParkLocationRepository(private val client: OkHttpClient) : ParkLocationRepository {
  /**
   * Find all nearby Street Workout parks, from OpenStreetMap using Overpass API
   *
   * @param lat : latitude (Double)
   * @param lon : longitude (Double)
   * @param onSuccess : to handle successful cases
   * @param onFailure : to handle unsuccessful cases
   */
  override fun search(
      lat: Double,
      lon: Double,
      onSuccess: (List<ParkLocation>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Based on the Overpass API. More information: https://wiki.openstreetmap.org/wiki/Overpass_API
    val request =
        Request.Builder()
            .url(
                "https://overpass-api.de/api/interpreter?data=[out:json];way[%22leisure%22=%22fitness_station%22](around:30000,$lat,$lon);%20out%20geom%20center;")
            .header("User-Agent", "test/5.0")
            .build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                  }
                  val listLocation = decodeJson(response.body!!.string())
                  onSuccess(listLocation)
                }
              }
            })
  }
}

private fun decodeJson(json: String): List<ParkLocation> {
  val jsonObject = JSONObject(json)
  val elementsArray = jsonObject.getJSONArray("elements")

  val listParkLocation = emptyList<ParkLocation>().toMutableList()

  for (i in 0 until elementsArray.length()) {
    val element = elementsArray.getJSONObject(i)
    val center = element.getJSONObject("center")

    val latitude = center.getDouble("lat")
    val longitude = center.getDouble("lon")
    val id = element.getString("id")

    val parkLocation = ParkLocation(latitude, longitude, id)

    listParkLocation += parkLocation
  }
  return listParkLocation
}
