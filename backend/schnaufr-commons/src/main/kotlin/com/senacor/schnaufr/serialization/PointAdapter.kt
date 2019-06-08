package com.senacor.schnaufr.serialization

import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import com.squareup.moshi.*

internal object PointAdapter {
    @ToJson
    fun toJson(point: Point): List<Double> {
        return point.position.values
    }

    @FromJson
    fun fromJson(location: List<Double>): Point {
        return Point(Position(location))
    }
}