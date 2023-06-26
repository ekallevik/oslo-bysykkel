package com.example.domain

import com.example.services.GbfsStation
import com.example.services.GbfsStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class BicycleStation private constructor(
    val id: String,
    val name: String,
    val capacity: Int,
    val status: StationStatus?
): Comparable<BicycleStation> {

    fun isInstalledOrUnknown() = status?.isInstalled != false

    fun isNonEmptyOrUnknown() = status?.isRenting != false

    fun isNonFullOrUnknown() = status?.isReturning != false

    override fun compareTo(other: BicycleStation): Int = id.compareTo(other.id)

    companion object {

        fun parse(station: GbfsStation, status: GbfsStatus?): BicycleStation {

            val capacity = station.capacity

            val parsedStatus = if (status == null) {
                println("Missing status for station")
                null
            } else {
                StationStatus.parse(status, capacity)
            }

            return BicycleStation(station.station_id, station.name, capacity, parsedStatus)
        }

    }
}

@Serializable
data class StationStatus(
    val numBikes: Int,
    val numDocks: Int,
    val isInstalled: Boolean,
    val isRenting: Boolean,
    val isReturning: Boolean
) {

    companion object {

        fun parse(status: GbfsStatus, capacity: Int): StationStatus? {

            val numBikesAvailable = status.num_bikes_available
            val numDocksAvailable = status.num_docks_available

            return if (numBikesAvailable !in 0..capacity) {
                null
            } else if (numDocksAvailable !in 0..capacity) {
                null
            } else if (numBikesAvailable + numDocksAvailable != capacity) {
                null
            } else {
                StationStatus(numBikesAvailable, numDocksAvailable, status.is_installed, status.is_renting, status.is_returning)
            }
        }
    }
}

@Serializable
data class StationOverviewParams(
    val excludeMissingStatus: Boolean,
    val excludeNonInstalled: Boolean,
    val excludeEmpty: Boolean,
    val excludeFull: Boolean,
) {

    companion object {

        fun parse(excludeMissingStatus: Boolean?, excludeNonInstalled: Boolean?, excludeEmpty: Boolean?, excludeFull: Boolean?) =
            StationOverviewParams(
                excludeMissingStatus ?: false,
                excludeNonInstalled ?: false,
                excludeEmpty ?: false,
                excludeFull ?: false,
            )

    }
}

@Serializable
data class StationsOverview(
    // it would be better to use SortedSet<BicycleStation> here, but I did not have the time to write a custom serializer for TreeSet.
    @Contextual
    val stations: List<BicycleStation>,
    val params: StationOverviewParams,
) {
    companion object {

        fun valueOf(
            stations: List<BicycleStation>,
            params: StationOverviewParams,
            ): StationsOverview {

            val filtered = stations
                .asSequence()
                .filter { station -> !params.excludeMissingStatus || (station.status != null) }
                .filter { station -> !params.excludeNonInstalled || station.isInstalledOrUnknown() }
                .filter { station -> !params.excludeEmpty || station.isNonEmptyOrUnknown() }
                .filter { station -> !params.excludeFull || station.isNonFullOrUnknown() }
                .sorted()
                .toList()

            return StationsOverview(filtered, params)
        }
    }
}
