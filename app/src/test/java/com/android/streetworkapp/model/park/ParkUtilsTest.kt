package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.ParkLocation
import org.junit.Assert.assertEquals
import org.junit.Test

class ParkUtilsTest {

  @Test
  fun testCreateDefaultPark() {
    val pid = "123"
    val parkLocation = ParkLocation(0.0, 0.0, "321")

    val park = createDefaultPark(pid, parkLocation)

    assertEquals(pid, park.pid)
    assertEquals("Default Park ${parkLocation.id}", park.name)
    assertEquals(parkLocation, park.location)
    assertEquals("", park.imageReference)
    assertEquals(1f, park.rating)
    assertEquals(0, park.nbrRating)
    assertEquals(1, park.capacity)
    assertEquals(0, park.occupancy)
    assertEquals(emptyList<String>(), park.events)
  }
}
