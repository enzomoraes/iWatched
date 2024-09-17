package com.iwatched.api.factories

import com.iwatched.api.domain.models.Episode
import com.iwatched.api.domain.models.Season
import com.iwatched.api.domain.models.TVShow
import com.opencsv.CSVReader
import java.io.File
import java.io.FileReader
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

class TvShowFactory {

    companion object {

        fun createTVShow(): TVShow {
            val tvShow = TVShow(
                UUID.randomUUID(),
                "Breaking Bad",
                "O professor de química Walter White não acredita que sua vida possa piorar ainda mais. Quando descobre que tem câncer terminal, Walter decide arriscar tudo para ganhar dinheiro enquanto pode, transformando sua van em um laboratório de metanfetamina.",
                LocalDate.of(2008, 1, 20),
                LocalDate.of(2013, 8, 29),
                mutableSetOf(
                    Season(
                        UUID.randomUUID(),
                        "Season 1",
                        LocalDate.of(2008, 1, 20),
                        "thumbnail1.png",
                        mutableSetOf()
                    ),
                    Season(
                        UUID.randomUUID(),
                        "Season 2",
                        LocalDate.of(2009, 1, 8),
                        "thumbnail2.png",
                        mutableSetOf()
                    ), Season(
                        UUID.randomUUID(),
                        "Season 3",
                        LocalDate.of(2010, 3, 21),
                        "thumbnail3.png",
                        mutableSetOf()
                    ),
                    Season(
                        UUID.randomUUID(),
                        "Season 4",
                        LocalDate.of(2011, 7, 17),
                        "thumbnail4.png",
                        mutableSetOf()
                    ), Season(
                        UUID.randomUUID(),
                        "Season 5",
                        LocalDate.of(2012, 7, 15),
                        "thumbnail5.png",
                        mutableSetOf()
                    )
                )
            )
            val episodes = readEpisodesFromCsv(File("../scrapper/data/csv.csv").absolutePath)
            var season = 0
            for (ep in episodes) {
                if (ep.number == 1) season++
                tvShow.seasons.find { s -> s.label == "Season $season" }?.episodes?.add(ep)
            }

            return tvShow
        }


        private fun readEpisodesFromCsv(filePath: String): List<Episode> {
            val episodes = mutableListOf<Episode>()
            val reader = CSVReader(FileReader(filePath))
            reader.use {
                val rows = reader.readAll()
                for (row in rows.drop(1)) {
                    val number = row[0].toInt()
                    val title = row[1]
                    val description = row[2]
                    val duration = parseDuration(row[3])
                    val thumbnail = row[4]

                    val episode = Episode(
                        number = number,
                        title = title,
                        duration = duration,
                        description = description,
                        thumbnail = thumbnail
                    )
                    episodes.add(episode)
                }
            }
            return episodes
        }

        private fun parseDuration(durationStr: String): Long {
            var hours = Duration.ZERO
            var minutes = Duration.ZERO

            // Expressões regulares para encontrar horas e minutos na string
            val hoursMatch = Regex("(\\d+)h").find(durationStr)
            val minutesMatch = Regex("(\\d+)min").find(durationStr)

            // Se horas forem encontradas, convertê-las para Duration
            if (hoursMatch != null) {
                hours = hoursMatch.groupValues[1].toLong().hours
            }

            // Se minutos forem encontrados, convertê-los para Duration
            if (minutesMatch != null) {
                minutes = minutesMatch.groupValues[1].toLong().minutes
            }

            // Retornar a soma de horas e minutos como Duration
            return (hours + minutes).toLong(DurationUnit.SECONDS)
        }
    }

}