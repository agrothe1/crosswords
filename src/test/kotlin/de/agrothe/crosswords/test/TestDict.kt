package de.agrothe.crosswords.test

import de.agrothe.crosswords.Dict
import de.agrothe.crosswords.readConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val logger = KotlinLogging.logger{}

private val config = readConfig()
private val dict: List<Pair<String, List<String>>> =
    Dict(readConfig().dict).dict

class TestDict {
    @Test
    fun numKeys() {
        assertEquals(true,
        dict.size in 22601..22749)
    }

    @Test
    fun numValues() {
        val numEntries = dict.fold(arrayOf(1)){acc, entry ->
            acc[0] = acc[0] + entry.second.size; acc}[0]

        assertEquals(true,
            numEntries in 64001..64999)
    }

    @Test
    fun emptyKey() {
        dict.forEach{assertEquals(false, it.first.isBlank())}
    }

    @Test
    fun illegalKey() {
        val illegalChars = Regex("[^\\p{Alpha}]+")
        dict.forEach{assertEquals(false,
            it.first.contains(illegalChars))}
    }

    @Test
    fun emptyValues() {
        dict.forEach{assertEquals(false, it.second.isEmpty())}
    }

    @Test
    fun emptyValue() {
        dict.forEach{
            it.second.forEach{
                assertEquals(false, it.isBlank())}}
    }

    @Test
    fun noNegatives() {
        val negatives = File(config.dict.NEGATIVE_LIST_FILE_NAME).readLines()
            .toSet()
        dict.forEach{
            assertEquals(false, negatives.contains(it.first))}
    }
}