package com.android.streetworkapp.model.parks

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class OverpassParkLocationRepository(private val client: OkHttpClient) : ParkLocationRepository {
  /**
   * Find all nearby Street Workout parks, from OpenStreetMap using Overpass API
   *
   * @param lat : latitude (Double) must be between [-90,90]
   * @param lon : longitude (Double) must be between [-180,180]
   * @param onSuccess : to handle successful cases
   * @param onFailure : to handle unsuccessful cases
   */
  override fun search(
      lat: Double,
      lon: Double,
      onSuccess: (List<ParkLocation>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    require(lat < 90 && lat > -90)
    require(lon < 180 && lon > -180)

    // Based on the Overpass API. More information: https://wiki.openstreetmap.org/wiki/Overpass_API

    val url =
        HttpUrl.Builder()
            .scheme("https")
            .host("overpass-api.de")
            .addPathSegment("api")
            .addPathSegment("interpreter")
            .addEncodedQueryParameter(
                "data",
                "[out:json];way[\"leisure\"=\"fitness_station\"](around:30000,$lat,$lon);%20out%20geom%20center;")
            .build()

    val request = Request.Builder().url(url).header("User-Agent", "test/5.0").build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                onSuccess(decodeJson(response.body!!.string()))
              }
            })
  }
}

/**
 * Decodes JSON coming from the Overpass API.
 *
 * @param json : A string with the JSON format. It can not be empty.
 */
fun decodeJson(json: String): List<ParkLocation> {
  require(json.isNotEmpty())

  val jsonObject = JSONObject(json)
  val elementsArray = jsonObject.getJSONArray("elements")

  val listParkLocation = mutableListOf<ParkLocation>()

  for (i in 0 until elementsArray.length()) {

    val element = elementsArray.getJSONObject(i)
    val center = element.getJSONObject("center")

    val latitude = center.getDouble("lat")
    val longitude = center.getDouble("lon")
    val id = element.getInt("id")

    val parkLocation = ParkLocation(latitude, longitude, id.toString())

    listParkLocation += parkLocation
  }

  return listParkLocation
}
