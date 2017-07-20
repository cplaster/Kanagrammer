package com.xyphoid.kanagrammer

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import kotlin.collections.HashMap;
import kotlin.collections.HashSet;
import kotlin.collections.List;
import kotlin.collections.MutableSet;

/**
 * Created by Chad Plaster on 7/19/2017.
 */


class AnagramCore(var solverArgs: SolverArgs) {

    private val sortedDictionary: SortedWordDictionary
    private var progresstext = ""

    init {
        sortedDictionary = SortedWordDictionary(solverArgs)
    }

    fun repairDictionary() {
        val dictionary = sortedDictionary.getMainDictionary()
        val newdict = HashMap<String, String>()

        var count = 0

        for (key in dictionary.keys) {
            var key2 = key
            key2 = key2.replace("\\,".toRegex(), "")
            key2 = key2.replace("\\[".toRegex(), "")
            key2 = key2.replace("\\]".toRegex(), "")
            key2 = key2.replace("\\s".toRegex(), "")

            val dKey = dictionary.get(key);

            if(dKey != null) {
                newdict.put(key2, dKey)
            }

            count++
            if (count % 1000 == 0) {
                Log.d("Anagrammer2:", "Processed " + Integer.toString(count) + " of " + Integer.toString(dictionary.keys.size) + ".\n")
            }

        }

        val location = "/sdcard/enablenew.bin"

        try {
            val fileOut = FileOutputStream(location)
            val out = ObjectOutputStream(fileOut)
            out.writeObject(newdict)
            out.close()
            fileOut.close()
            Log.d("Anagrammer2:", "Write binary dictionary to " + location)
        } catch (i: IOException) {
            i.printStackTrace()
        }

    }

    fun findExactSubsets(): HashMap<String, Int>? {
        var query: String = this.solverArgs.letters
        var results: String? = ""
        query = query.replace(" ", "")
        results = query

        val dict = sortedDictionary.getMainDictionary()

        val n = query.length

        val total = Math.pow(2.0, n.toDouble())

        val combos = HashMap<String, Int>()

        for (i in 1..total.toInt() - 1) {
            val s = AnagramSolverHelper.toBinaryString(i, n)
            var cat = ""
            val a = s.split("(?!^)".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            for (j in a.indices) {
                val sub = s.substring(j, j + 1)
                if (sub == "1") {
                    cat += query.substring(j, j + 1)
                }
            }

            if (cat.length > 1) {
                val key = AnagramSolverHelper.sortWord(cat)
                if (dict.containsKey(key)) {
                    val value = dict.get(key)

                    if(value != null) {
                        if (value.contains(",")) {
                            val temp = value.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                            for (t in temp) {
                                combos.put(t, AnagramSolverHelper.getScrabbleValue(t))
                            }
                        } else {
                            combos.put(value, AnagramSolverHelper.getScrabbleValue(value))
                        }
                    }
                }
            }
        }

        return if (combos.isEmpty()) null else combos
    }


    fun findExactAnagrams(): HashSet<String>? {
        val wordString = this.solverArgs.letters
        val dictionary = sortedDictionary.getMainDictionary()

        val anagrams = HashSet<String>()
        val sortedKey = AnagramSolverHelper.sortWord(wordString)

        if (dictionary.containsKey(sortedKey)) {
            val wordlist = dictionary.get(sortedKey)
            if(wordlist != null) {
                val words = wordlist.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

                for (word in words) {
                    anagrams.add(word)
                }
            }
        }

        return if (anagrams.isEmpty()) null else anagrams
    }

    fun findSpecificAnagrams(): HashSet<Set<String>> {
        val wordString = this.solverArgs.letters.replace("\\s".toRegex(), "")
        val anagramsSet = HashSet<Set<String>>()
        sortedDictionary.loadDictionaryWithSpecificSubsets(wordString, this.solverArgs.lengths)
        val keyList = sortedDictionary.dictionaryKeyList

        val count = 0

        for (index in keyList.indices) {
            progresstext = "Processing " + Integer.toString(index + 1) +
                    " of " + Integer.toString(keyList.size)
            //Log.d("anagrammer2:", progresstext);
            val solverTask =  this.solverArgs.solverTask;
            //this.solverArgs.solverTask!!.Publish(progresstext)
            if(solverTask is MultiWordSolverTask) {
                solverTask.Publish(progresstext);
            }
            val charInventory = wordString.toCharArray()
            val dictWordAnagramsSet = findAnagrams(index, charInventory, keyList)
            val tempAnagramSet = HashSet<Set<String>>()
            if (dictWordAnagramsSet != null && !dictWordAnagramsSet.isEmpty()) {
                var mergeResult: Set<Set<String>>? = null
                for (anagramSet in dictWordAnagramsSet) {
                    mergeResult = mergeAnagramKeyWords(anagramSet)
                    tempAnagramSet.addAll(mergeResult)
                }
                // print stuff to stdout if ya want;

                anagramsSet.addAll(tempAnagramSet)
            }
        }

        return anagramsSet
    }

    fun findAllAnagrams(): Set<Set<String>> {

        val wordString = this.solverArgs.letters!!.replace("\\s".toRegex(), "")
        val anagramsSet = HashSet<Set<String>>()

        progresstext = "Pruning dictionary entries..."
        Log.d("anagrammer2:", progresstext)

        sortedDictionary.loadDictionaryWithSubsets(wordString, this.solverArgs.minWordLength)
        val keyList = sortedDictionary.dictionaryKeyList

        val count = 0

        for (index in keyList.indices) {
            progresstext = "Processing " + Integer.toString(index) +
                    " of " + Integer.toString(keyList.size)
            Log.d("anagrammer2:", progresstext)
            val charInventory = wordString.toCharArray()
            val dictWordAnagramsSet = findAnagrams(index, charInventory, keyList)
            val tempAnagramSet = HashSet<Set<String>>()
            if (dictWordAnagramsSet != null && !dictWordAnagramsSet.isEmpty()) {
                var mergeResult: Set<Set<String>>? = null
                for (anagramSet in dictWordAnagramsSet) {
                    mergeResult = mergeAnagramKeyWords(anagramSet)
                    tempAnagramSet.addAll(mergeResult)
                }
                // print stuff to stdout if ya want;

                anagramsSet.addAll(tempAnagramSet)
            }
        }

        return anagramsSet
    }

    //recursive function to find all the anagrams for charInventory characters
    //starting with the word at dictionaryIndex in dictionary keyList

    private fun findAnagrams(dictionaryIndex: Int, charInventory: CharArray, keyList: List<String>): HashSet<MutableSet<String>>? {

        if (dictionaryIndex >= keyList.size || charInventory.size < this.solverArgs.minWordLength) {
            return null
        }

        val searchWord = keyList.get(dictionaryIndex)
        val searchWordChars = searchWord.toCharArray()
        if (AnagramSolverHelper.isEquivalent(searchWordChars, charInventory)) {
            val anagramsSet = HashSet<MutableSet<String>>()
            val anagramSet = HashSet<String>()
            anagramSet.add(searchWord)
            anagramsSet.add(anagramSet)

            return anagramsSet
        }

        if (AnagramSolverHelper.isSubset(searchWordChars, charInventory)) {
            val newCharInventory = AnagramSolverHelper.setDifference(charInventory, searchWordChars)
            if (newCharInventory.size >= this.solverArgs.minWordLength) {
                val anagramsSet = HashSet<MutableSet<String>>()
                for (index in dictionaryIndex + 1..keyList.size - 1) {
                    val searchWordAnagramsKeySet = findAnagrams(index, newCharInventory, keyList)
                    if (searchWordAnagramsKeySet != null) {
                        val mergedSets = mergeWordToSets(searchWord, searchWordAnagramsKeySet)
                        anagramsSet.addAll(mergedSets!!)
                    }
                }
                return if (anagramsSet.isEmpty()) null else anagramsSet
            }
        }

        return null
    }

    private fun mergeAnagramKeyWords(anagramKeySet: MutableSet<String>?): MutableSet<MutableSet<String>> {
        if (anagramKeySet == null) {
            throw IllegalStateException("anagram keys cannot be null")
        }

        val anagramsSet = HashSet<MutableSet<String>>()

        for (word in anagramKeySet) {
            val anagramWordSet = sortedDictionary.findSingleWordAnagrams(word)
            if(anagramWordSet != null) {
                anagramsSet.add(anagramWordSet)
            }
        }
        val anagramsSetArray: Array<MutableSet<String>> = anagramsSet.toTypedArray<MutableSet<String>>()

        return AnagramSolverHelper.setMultiplication(*anagramsSetArray)
    }

    private fun mergeWordToSets(word: String, sets: MutableSet<MutableSet<String>>?): HashSet<MutableSet<String>>? {
        assert(!word.isEmpty())
        if (sets == null) {
            return null
        }

        val mergedSets = HashSet<MutableSet<String>>()
        for (set in sets) {
            if (set == null) {
                throw IllegalStateException("anagram keys set cannot be null")
            }
            set.add(word)
            mergedSets.add(set)
        }

        return mergedSets
    }

}

