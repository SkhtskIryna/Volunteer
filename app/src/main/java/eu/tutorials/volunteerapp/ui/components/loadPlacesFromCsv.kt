package eu.tutorials.volunteerapp.ui.components

import android.content.Context
import eu.tutorials.volunteerapp.data.Place

fun loadPlacesFromCsv(context: Context): List<Place> {
    val places = mutableListOf<Place>()
    val inputStream = context.assets.open("ua-name-places.csv")
    inputStream.bufferedReader().useLines { lines ->
        lines.drop(1).forEach { line ->
            val tokens = line.split(",")
            if (tokens.size >= 13) {
                val region = tokens[10].trim()
                val district = tokens[11].trim()
                val name = tokens[3].trim()
                val placeType = tokens[5].trim()
                places.add(Place(region, district, name, placeType))
            }
        }
    }
    return places
}
