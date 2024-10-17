package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.ParkLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkTest {

  @Test
  fun parkInitializationValidData() {
    val park =
        Park(
            pid = "123",
            name = "Sample Park",
            location = ParkLocation(0.0, 0.0, "321"),
            imageReference = "parks/sample.png",
            rating = 4.0f,
            nbrRating = 2,
            capacity = 10,
            occupancy = 5,
            events = listOf("event1", "event2"))

    assertEquals(park.pid, "123")
    assertEquals(park.name, "Sample Park")
    assertEquals(park.location, ParkLocation(0.0, 0.0, "321"))
    assertEquals(park.imageReference, "parks/sample.png")
    assertEquals(park.rating, 4.0f)
    assertEquals(park.nbrRating, 2)
    assertEquals(park.capacity, 10)
    assertEquals(park.occupancy, 5)
    assertEquals(park.events, listOf("event1", "event2"))
  }

  @Test
  fun parkInitializationEmptyEvents() {
    val park =
        Park(
            pid = "123",
            name = "Sample Park",
            location = ParkLocation(0.0, 0.0, "321"),
            imageReference = "parks/sample.png",
            rating = 4.0f,
            nbrRating = 2,
            capacity = 10,
            occupancy = 5,
            events = emptyList())

    assertEquals(park.pid, "123")
    assertEquals(park.name, "Sample Park")
    assertEquals(park.location, ParkLocation(0.0, 0.0, "321"))
    assertEquals(park.imageReference, "parks/sample.png")
    assertEquals(park.rating, 4.0f)
    assertEquals(park.nbrRating, 2)
    assertEquals(park.capacity, 10)
    assertEquals(park.occupancy, 5)
    assertTrue(park.events.isEmpty())
  }
}
