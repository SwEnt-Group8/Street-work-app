package com.android.streetworkapp.model.parks

import okhttp3.Call
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class OverpassParkLocationRepositoryTest {
  private lateinit var okHttpClient: OkHttpClient
  private lateinit var call: Call

  @Before
  fun setUp() {
    okHttpClient = mock(OkHttpClient::class.java)
    call = mock(Call::class.java)

    `when`(okHttpClient.newCall(any())).thenReturn(call)
  }

  @Test
  fun decodeJSONWorksOnKnownString() {
    val string =
        "{\"version\":0.6,\"generator\":\"Overpass API 0.7.62.1 084b4234\",\"osm3s\":{\"timestamp_osm_base\":\"2024-10-06T17:27:57Z\",\"copyright\":\"The data included in this document is from www.openstreetmap.org. The data is made available under ODbL.\"},\"elements\":[{\"type\":\"way\",\"id\":682145783,\"center\":{\"lat\":46.5228885,\"lon\":6.6252872},\"nodes\":[6388088374,6388088373,6388088372,6388088371,6388088374],\"tags\":{\"image\":\"https://commons.wikimedia.org/wiki/Category:Kenguru.Pro#/media/File:Lausanne-KenguruPro.jpg\",\"layer\":\"-1\",\"leisure\":\"fitness_station\",\"sport\":\"exercise\"}}]}"
    val listParks = decodeJson(string)
    assert(listParks.isNotEmpty())
    assert(listParks[0].lat == 46.5228885)
    assert(listParks[0].lon == 6.6252872)
    assert(listParks[0].id == "682145783")
  }

  @Test
  fun decodeJSONWorksOnKnownStringWithNoElement() {
    val string =
        "{\"version\":0.6,\"generator\":\"Overpass API 0.7.62.1 084b4234\",\"osm3s\":{\"timestamp_osm_base\":\"2024-10-06T17:50:30Z\",\"copyright\":\"The data included in this document is from www.openstreetmap.org. The data is made available under ODbL.\"},\"elements\":[]}"
    val test = decodeJson(string)
    assert(test.isEmpty())
  }

  @Test(expected = IllegalArgumentException::class)
  fun decodeJSONThrowsOnEmptyString() {
    val string = ""
    val test = decodeJson(string)
  }

  @Test
  fun searchCallsNewCall() {
    val overpass = OverpassParkLocationRepository(okHttpClient)
    overpass.search(0.0, 0.0, {}, {})
    verify(okHttpClient).newCall(any())
  }

  @Test(expected = IllegalArgumentException::class)
  fun searchThrowsOnInvalidLatAndLon() {
    val overpass = OverpassParkLocationRepository(okHttpClient)
    overpass.search(-200.0, -200.0, {}, {})
  }
}
