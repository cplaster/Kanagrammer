package com.xyphoid.kanagrammer

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import kotlin.collections.HashMap;
import kotlin.collections.List;
import kotlin.collections.Map;
import kotlin.collections.Set;
import kotlin.collections.MutableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Chad Plaster on 7/19/2017.
 */


class SortedWordDictionary(private val _solverArgs: SolverArgs) {


    private val sortedStringMap = HashMap<String, MutableSet<String>>()
    var isDictionaryLoaded = false
        private set
    var isMainDictionaryLoaded = false
    private var lastWordString = ""
    private var mainDictionary = HashMap<String, String>()

    init {
        mainDictionary = _solverArgs.dictionary
        isMainDictionaryLoaded = true
    }

    fun loadDictionaryWithSpecificSubsets(wordString: String?, lengths: IntArray) {

        if (isDictionaryLoaded && wordString === lastWordString) {
            return
        }

        val mapLengths = HashMap<Int, Int>()

        for (i in lengths) {
            if (mapLengths.containsKey(i)) {
                var value: Int? = mapLengths[i]
                if(value != null) {
                    value++
                    mapLengths.put(i, value)
                }
            } else {
                mapLengths.put(i, 0)
            }
        }

        Log.d("Anagrammer2:", "Pruning treemap for <$wordString>\n")

        var count = 0

        for (key in mainDictionary.keys) {
            if (key == null
                    || key.isEmpty()
                    || wordString != null && !wordString.isEmpty() && (!mapLengths.containsKey(key.length) || !AnagramSolverHelper
                    .isSubset(key.toCharArray(), wordString
                            .replace("\\s".toRegex(), "").toLowerCase()
                            .toCharArray()))) {
                continue
            }
            var wordSet: MutableSet<String>? = sortedStringMap[key]
            var mdKey = mainDictionary[key];
            if(mdKey != null) {
                if (wordSet != null) {
                    count += AnagramSolverHelper.addToWordSet(wordSet, mdKey)
                } else {
                    wordSet = TreeSet<String>()
                    count += AnagramSolverHelper.addToWordSet(wordSet, mdKey)
                    sortedStringMap.put(key, wordSet)
                }
            }
        }

        Log.d("Anagrammer2:", "Pruned wordlist contains " + Integer.toString(count) + " words in " + Integer.toString(sortedStringMap.keys.size) + " keys.\n")

        isDictionaryLoaded = true
        if(wordString != null) {
            lastWordString = wordString
        }
    }

    fun loadDictionaryWithSubsets(wordString: String?, minWordSize: Int) {


        if (isDictionaryLoaded && wordString === lastWordString) {
            return
        }

        Log.d("Anagrammer2:", "Pruning treemap for <$wordString>\n")

        var count = 0

        for (key in mainDictionary.keys) {
            if (key == null
                    || key.isEmpty()
                    || wordString != null && !wordString.isEmpty() && (key
                    .length < minWordSize || !AnagramSolverHelper
                    .isSubset(key.toCharArray(), wordString
                            .replace("\\s".toRegex(), "").toLowerCase()
                            .toCharArray()))) {
                continue
            }
            var wordSet: MutableSet<String>? = sortedStringMap[key]
            val mdKey = mainDictionary[key];
            if(mdKey != null) {
                if (wordSet != null) {
                    count += AnagramSolverHelper.addToWordSet(wordSet,mdKey)
                } else {
                    wordSet = TreeSet<String>()
                    count += AnagramSolverHelper.addToWordSet(wordSet, mdKey)
                    sortedStringMap.put(key, wordSet)
                }
            }
        }

        Log.d("Anagrammer2:", "Pruned wordlist contains " + Integer.toString(count) + " words in " + Integer.toString(sortedStringMap.keys.size) + " keys.\n")

        isDictionaryLoaded = true
        if(wordString != null) {
            lastWordString = wordString
        }
    }

    fun addWord(wordString: String): Boolean {

        if (wordString.isEmpty()) {
            return false
        }

        val sortedWord = AnagramSolverHelper.sortWord(wordString)
        var wordSet: MutableSet<String>? = sortedStringMap[sortedWord]
        if (wordSet != null) {
            wordSet.add(wordString)
        } else {
            wordSet = TreeSet<String>()
            wordSet.add(wordString)
            if(sortedWord != null) {
                sortedStringMap.put(sortedWord, wordSet)
            }
        }

        return true
    }

    fun findSingleWordAnagrams(wordString: String?): MutableSet<String>? {

        if (!isDictionaryLoaded) {
            throw IllegalStateException("dictionary not loaded.")
        } else {
            if (wordString == null || wordString.isEmpty()) {
                throw IllegalStateException("word string invalid")
            }

            return sortedStringMap[AnagramSolverHelper.sortWord(wordString)]
        }
    }

    val dictionaryKeyList: List<String>
        get() {
            assert(sortedStringMap != null)
            return ArrayList(sortedStringMap.keys)
        }

    fun getMainDictionary(): HashMap<String, String> {
        if (!isMainDictionaryLoaded) {
            throw IllegalStateException("main dictionary must be loaded first!")
        }
        return mainDictionary
    }

    override fun toString(): String {
        return "isDictionaryLoaded?: $isDictionaryLoaded\nDictionary: $sortedStringMap"
    }
}

