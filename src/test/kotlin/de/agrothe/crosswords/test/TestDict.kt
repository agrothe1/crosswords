package de.agrothe.crosswords.test

import de.agrothe.crosswords.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFalse

private val logger = KotlinLogging.logger{}

private val config = readConfig()
private val entries by lazy {Dict(config.dict).entries}

class TestDict{
    private val numEntries = 69_500 ..69_800

    @Test
    fun numKeysUniDir(){
        assertTrue(Dict(config.dict.apply{BIDIRECTIONAL=false})
            .entries.count() in numEntries)
    }

    @Test
    fun numKeysBiDir(){
        assertTrue(Dict(config.dict.apply{BIDIRECTIONAL=true})
            .entries.count() in numEntries.map{it.times(2)})
    }

    @Test
    fun noEmptyKey() =
        entries.forEach{assertFalse(it.key.isBlank())}

    @Test
    fun legalKeys(){
        val illegalChars = Regex("[^\\p{Alpha}]+")
        entries.forEach{assertFalse(it.key.contains(illegalChars))}
    }

    @Test
    fun noEmptyValues() =
        entries.forEach{entry->assertFalse(
            entry.value.synms.let{
                val isEmpty = it.isEmpty()
                if(isEmpty)logger.error{"empty value: $entry"}
                isEmpty
             })}

    @Test
    fun noEmptyValue() =
        entries.forEach{it ->
            it.value.synms.forEach{
                assertFalse(it.isBlank())}}

    @Test
    fun noNegatives(){
        val negatives = config.dict.NEGATIVES_REGEXPR
        entries.forEach{entry->assertFalse(
            entry.key.let{
                val isNegative = negatives.matches(it)
                if(isNegative)logger.error{"should be excluded: $entry"}
                isNegative
             })
        }
    }

    @Test
    fun noDuplicateValues() =
        entries.values.forEach{
            assertTrue(it.synms.size == it.synms.distinct().size)
        }

    @Test
    fun writeRead(){
        File.createTempFile("crwds", "").let{file->
            generate(3)?.apply{save(file)}
                ?.zip(file.read())?.forEach{
                    (rowOut, rowIn)->
                        assertTrue(rowOut contentEquals rowIn)
            }
            file.delete()
        }
    }

    @Test
    fun permutations(){
        val testDict = listOf(
           "", ";", ";;", "x", "x;", "x;;", ";x;", ";;x",
           "#A;B;C",
           " a ; b;c#",
           "a; b ;c# ",
           "a;  b; c # C",
           "a;b;b;a;c;b;c",
           "ä; b; c",
           " a ; b;ô",
           " ä ; b;ô",
           "b;1;2",
           "1;b;2",
           "Ää;öxÖ;üyÜ;ßzß",
           "Äsen;Über;Öffi;mästen;grübeln;lösen;daß",
           " w/ Space;KEY;;B C;D#"
        ).asSequence()
        val dict =
            testDict.parseDict(config.dict.apply{BIDIRECTIONAL=false})
                .map{Pair(it.key, it.value.synms)}.toMap().toString()
        assertTrue(dict ==
"""{a=[b, c, ô], b=[a, c, ä, ô, 1, 2], c=[a, b, ä], ae=[b, c, ô], Aeae=[öxÖ, üyÜ, ßzß], oexOe=[Ää, üyÜ, ßzß], ueyUe=[Ää, öxÖ, ßzß], sszss=[Ää, öxÖ, üyÜ], Aesen=[Über, Öffi, mästen, grübeln, lösen, daß], Ueber=[Äsen, Öffi, mästen, grübeln, lösen, daß], Oeffi=[Äsen, Über, mästen, grübeln, lösen, daß], maesten=[Äsen, Über, Öffi, grübeln, lösen, daß], gruebeln=[Äsen, Über, Öffi, mästen, lösen, daß], loesen=[Äsen, Über, Öffi, mästen, grübeln, daß], dass=[Äsen, Über, Öffi, mästen, grübeln, lösen], KEY=[w/ Space, B C, D], D=[w/ Space, KEY, B C]}"""
        )
    }
}
