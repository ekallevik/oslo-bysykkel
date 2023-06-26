package com.example.services

import com.example.domain.BicycleStation
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

object GbfsService {

    suspend fun getBicycleStations(client: HttpClient, baseUrl: String): List<BicycleStation> {

        val stations = getStations(client, baseUrl)
        println("Got ${stations.size} stations from request")

        val statuses = getStationStatus(client, baseUrl).associateBy { status -> status.station_id }
        println("Got ${stations.size} associated statuses")

        return stations.map { station -> BicycleStation.parse(station, statuses[station.station_id]) }
    }

    suspend fun getStations(client: HttpClient, baseUrl: String): List<GbfsStation> {

        val response = client.get("$baseUrl/station_information.json")
        return response.body<GbfsStationResponse>().data.stations
    }

    suspend fun getStationStatus(client: HttpClient, baseUrl: String): List<GbfsStatus> {

        val response = client.get("$baseUrl/station_status.json")
        return response.body<GbfsStatusResponse>().data.stations
    }
}


@Serializable
data class GbfsStationResponse(
    val last_updated: String,
    val data: GbfsStationData
)

@Serializable
data class GbfsStationData(
    val stations: List<GbfsStation>
)

@Serializable
data class GbfsStation(
    val station_id: String,
    val name: String,
    val capacity: Int
)

@Serializable
data class GbfsStatusResponse(
    val last_updated: String,
    val data: GbfsStatusData
)

@Serializable
data class GbfsStatusData(
    val stations: List<GbfsStatus>
)

@Serializable
data class GbfsStatus(
    val station_id: String,
    val is_installed: Boolean,
    val is_renting: Boolean,
    val is_returning: Boolean,
    val num_bikes_available: Int,
    val num_docks_available: Int
)