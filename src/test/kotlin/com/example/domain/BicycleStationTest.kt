package com.example.domain

import com.example.services.GbfsStation
import com.example.services.GbfsStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BicycleStationTest {


    @Test
    fun testParsingOfStatusWithNegativeBikes() {

        val status = GbfsStatus("1", true, true, true, -2, 5)

        val result = StationStatus.parse(status, 10)
        assertEquals(result, null)
    }

    @Test
    fun testParsingOfStatusExceedingCapacity() {

        val status = GbfsStatus("1", true, true, true, 100, 5)

        val result = StationStatus.parse(status, 10)
        assertEquals(result, null)
    }

    @Test
    fun testIncludeAllStations() {

        val stations = listOf(
            BicycleStation.parse(GbfsStation("1", "a", 10), null),
            BicycleStation.parse(GbfsStation("2", "b", 10), GbfsStatus("2", false, true, true, 10, 0)),
            BicycleStation.parse(GbfsStation("3", "c", 10), GbfsStatus("3", true, false, true, 0, 10)),
        )

        val params = StationOverviewParams(false, false, false, false)
        val overview = StationsOverview.valueOf(stations, params)

        assertEquals(overview.stations.size, 3)
        assertTrue { overview.stations.contains(stations[1]) }
        assertTrue { overview.stations.contains(stations[2]) }
    }

    @Test
    fun testExcludingMissingStatus() {

        val stations = listOf(
            BicycleStation.parse(GbfsStation("1", "a", 10), null),
            BicycleStation.parse(GbfsStation("2", "b", 10), GbfsStatus("2", false, true, true, 10, 0)),
            BicycleStation.parse(GbfsStation("3", "c", 10), GbfsStatus("3", true, false, true, 0, 10)),
        )

        val params = StationOverviewParams(true, false, false, false)
        val overview = StationsOverview.valueOf(stations, params)

        assertEquals(overview.stations.size, 2)
        assertTrue { overview.stations.contains(stations[1]) }
        assertTrue { overview.stations.contains(stations[2]) }
    }

    @Test
    fun testExcludingNonInstalled() {

        val stations = listOf(
            BicycleStation.parse(GbfsStation("1", "a", 10), null),
            BicycleStation.parse(GbfsStation("2", "b", 10), GbfsStatus("2", false, true, true, 10, 0)),
            BicycleStation.parse(GbfsStation("3", "c", 10), GbfsStatus("3", true, false, true, 0, 10)),
        )

        val params = StationOverviewParams(false, true, false, false)
        val overview = StationsOverview.valueOf(stations, params)

        assertEquals(overview.stations.size, 2)
        assertTrue { overview.stations.contains(stations[0]) }
        assertTrue { overview.stations.contains(stations[2]) }
    }

    @Test
    fun testExcludingFull() {

        val stations = listOf(
            BicycleStation.parse(GbfsStation("1", "a", 10), null),
            BicycleStation.parse(GbfsStation("2", "b", 10), GbfsStatus("2", false, true, true, 10, 0)),
            BicycleStation.parse(GbfsStation("3", "c", 10), GbfsStatus("3", true, true, false, 0, 10)),
        )

        val params = StationOverviewParams(false, false, false, true)
        val overview = StationsOverview.valueOf(stations, params)

        assertEquals(overview.stations.size, 2)
        assertTrue { overview.stations.contains(stations[0]) }
        assertTrue { overview.stations.contains(stations[1]) }
    }

}
