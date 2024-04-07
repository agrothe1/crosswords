package de.agrothe.crosswords.test

import de.agrothe.crosswords.Dict
import de.agrothe.crosswords.readConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val logger = KotlinLogging.logger{}

private val config = readConfig()
private val dict: List<Pair<String, Collection<String>>> =
    Dict(readConfig().dict).dict

class TestDict{
    @Test
    fun numKeys() =
        assertEquals(true,
        dict.count() in 22_600..22_750)

    @Test
    fun numValues(){
        val numEntries = dict.fold(arrayOf(1)){acc, entry ->
            acc[0] = acc[0] + entry.second.count(); acc}[0]
        assertEquals(true,
            numEntries in 64_000..65_000)
    }

    @Test
    fun emptyKey() =
        dict.forEach{assertEquals(false, it.first.isBlank())}

    @Test
    fun illegalKey(){
        val illegalChars = Regex("[^\\p{Alpha}]+")
        dict.forEach{assertEquals(false,
            it.first.contains(illegalChars))}
    }

    @Test
    fun emptyValues() = dict.forEach{entry->assertEquals(false,
        entry.second.let{
            val isEmpty = it.isEmpty()
            if(isEmpty)logger.error{"empty value: $entry"}
            isEmpty
         })}

    @Test
    fun emptyValue() =
        dict.forEach{
            it.second.forEach{
                assertEquals(false, it.isBlank())}}

    @Test
    fun noNegatives() {
        val negatives = config.dict.NEGATIVES_LIST
        dict.forEach{
            assertEquals(false, negatives.contains(it.first))}
    }

    fun noDupicateKeys() {

    }

    @Test
    fun noDuplicateValues() =
        dict.forEach {
            assertEquals(true,
            it.second.size == it.second.distinct().size)
        }
}