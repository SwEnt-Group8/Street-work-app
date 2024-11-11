package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.decodeJson
import okhttp3.Call
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class NominatimParkNameRepositoryTest {



    private lateinit var okHttpClient: OkHttpClient
    private lateinit var call: Call

    @Before
    fun setUp() {
        okHttpClient = mock(OkHttpClient::class.java)
        call = mock(Call::class.java)

        `when`(okHttpClient.newCall(any())).thenReturn(call)
    }


    @Test
    fun decodeJSONRoadWorksOnKnownString() {
        val string ="[\n" +
                "  {\n" +
                "    \"place_id\": 84042284,\n" +
                "    \"licence\": \"Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright\",\n" +
                "    \"osm_type\": \"way\",\n" +
                "    \"osm_id\": 1061370894,\n" +
                "    \"lat\": \"46.50654355\",\n" +
                "    \"lon\": \"6.6606420020506345\",\n" +
                "    \"class\": \"leisure\",\n" +
                "    \"type\": \"fitness_station\",\n" +
                "    \"place_rank\": 30,\n" +
                "    \"importance\": 0.000074298466468253,\n" +
                "    \"addresstype\": \"leisure\",\n" +
                "    \"name\": \"\",\n" +
                "    \"display_name\": \"Avenue des Désertes, Pully, District de Lavaux-Oron, Vaud, 1009, Suisse\",\n" +
                "    \"address\": {\n" +
                "      \"road\": \"Avenue des Désertes\",\n" +
                "      \"town\": \"Pully\",\n" +
                "      \"county\": \"District de Lavaux-Oron\",\n" +
                "      \"state\": \"Vaud\",\n" +
                "      \"ISO3166-2-lvl4\": \"CH-VD\",\n" +
                "      \"postcode\": \"1009\",\n" +
                "      \"country\": \"Suisse\",\n" +
                "      \"country_code\": \"ch\"\n" +
                "    },\n" +
                "    \"extratags\": {\n" +
                "      \"sport\": \"fitness\",\n" +
                "      \"fitness_station\": \"fitness_station\"\n" +
                "    },\n" +
                "    \"boundingbox\": [\n" +
                "      \"46.5065123\",\n" +
                "      \"46.5065747\",\n" +
                "      \"6.6605110\",\n" +
                "      \"6.6607730\"\n" +
                "    ]\n" +
                "  }\n" +
                "]"
        val name = NominatimParkNameRepository(okHttpClient).decodeRoadJson(string)
        assert(name == "Avenue des Désertes")

    }



}