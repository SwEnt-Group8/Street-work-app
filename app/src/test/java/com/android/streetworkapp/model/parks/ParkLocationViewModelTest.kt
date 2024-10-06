package com.android.streetworkapp.model.parks

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class ParkLocationViewModelTest {
  private lateinit var parkLocationRepository: ParkLocationRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel

  @Before
  fun setUp() {
    parkLocationRepository = mock(ParkLocationRepository::class.java)
    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
  }

  @Test
  fun findNearbyParksCallsRepository() {
    parkLocationViewModel.findNearbyParks(0.0, 0.0)
    verify(parkLocationRepository).search(eq(0.0), eq(0.0), any(), any())
  }
}
