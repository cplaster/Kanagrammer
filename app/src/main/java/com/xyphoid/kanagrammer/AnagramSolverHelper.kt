package com.xyphoid.kanagrammer

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import kotlin.collections.Map;
import kotlin.collections.List;
import kotlin.collections.Set;
import kotlin.collections.MutableSet;
import kotlin.collections.HashSet;

/**
 * Created by Chad Plaster on 7/19/2017.
 */

object AnagramSolverHelper {

    fun sortByComparator(unsortMap: Map<String, Int>, order: Boolean): Map<String, Int> {

        val list = LinkedList<Map.Entry<String, Int>>(unsortMap.entries)

        // Sorting the list based on values
        Collections.sort<Map.Entry<String, Int>>(list) { o1, o2 ->
            if (order) {
                o1.value.compareTo(o2.value)
            } else {
                o2.value.compareTo(o1.value)

            }
        }

        // Maintaining insertion order with the help of LinkedList
        val sortedMap = LinkedHashMap<String, Int>()
        for (entry in list) {
            sortedMap.put(entry.key, entry.value)
        }

        return sortedMap
    }


    fun getScrabbleValue(query: String): Int {

        val s_values = HashMap<String, Int>()
        s_values.put("e,a,i,o,n,r,t,l,s,u", 1)
        s_values.put("d,g", 2)
        s_values.put("b,c,m,p", 3)
        s_values.put("f,h,v,w,y", 4)
        s_values.put("k", 5)
        s_values.put("j,x", 8)
        s_values.put("q,z", 10)

        val letters = query.split("(?!^)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val keys = s_values.keys
        var score: Int = 0

        for (l in letters) {
            for (k in keys) {
                if (k.contains(l)) {
                    var addened = s_values[k];
                    if(addened != null) {
                        score += addened
                    }
                }
            }
        }

        return score
    }

    fun toBinaryString(i: Int, length: Int): String {
        var binary = Integer.toBinaryString(i)
        var len = binary.length
        while (len < length) {
            binary = "0" + binary
            len++
        }

        return binary
    }

    fun addToWordSet(wordSet: MutableSet<String>, words: String): Int {
        val r = words.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var count = 0

        for (word in r) {
            wordSet.add(word)
            count++
        }

        return count
    }

    /*
      * sort the characters in word string
      *
      * @param wordString - string to sort
      *
      * @return string with sorted characters
      */
    fun sortWord(wordString: String): String? {
        if (wordString.isEmpty()) {
            return null
        }
        val charBytes = wordString.toByteArray()
        Arrays.sort(charBytes)

        return String(charBytes)
    }

    /*
      * checks if the first character array is subset of second character array
      *
      * @param charArr1 - character array charArr1 to check for subset
      *
      * @param charArr2 - checking for subset against character array charArr2
      *
      * @return true is charArray1 is subset of charArray2, false otherwise
      */
    fun isSubset(charArr1: CharArray, charArr2: CharArray): Boolean {
        if (charArr1.size > charArr2.size) {
            return false
        }
        val charList1 = toList(charArr1)
        val charList2 = toList(charArr2)
        // cannot do containsAll as there can be duplicate characters
        for (charValue in charList1) {
            if (charList2.contains(charValue)) {
                charList2.remove(charValue)
            } else {
                return false
            }
        }
        return true
    }

    /*
      * converts character array to character list
      */
    private fun toList(charArr: CharArray?): MutableList<Char> {
        assert(charArr != null)
        val charList = ArrayList<Char>()
        for (ch in charArr!!) {
            charList.add(ch)
        }
        return charList
    }

    /*
      * converts character list to character array
      */
    private fun toCharArray(charList: List<Char>?): CharArray {
        if (charList == null || charList.isEmpty()) {
            return CharArray(0)
        }

        val charArr = CharArray(charList.size)
        for (index in charList.indices) {
            charArr[index] = charList.get(index)
        }
        return charArr
    }

    /*
      * checks if two character arrays are equivalent;
      * char arrays are equivalent if:
      * 1. the number of elements in them are equal, and
      * 2. all the elements are same (not necessarily in same order)
      * complexity should be O(n)
      *
      * @param charArr1 - first character array for equivalence check
      *
      * @param charArr2 - second character array for equivalence check
      *
      * @return true is charArr1 is equivalent to charArr2, false otherwise
      */
    fun isEquivalent(charArr1: CharArray, charArr2: CharArray): Boolean {
        if (charArr1.size != charArr2.size) {
            return false
        }
        var sum1 = 0
        var sum2 = 0
        for (index in charArr1.indices) {
            sum1 += charArr1[index].toInt()
            sum2 += charArr2[index].toInt()
        }
        // in most cases it would return from here
        if (sum1 != sum2) {
            return false
        }
        val charList1 = toList(charArr1)
        val charList2 = toList(charArr2)
        for (charValue in charList1) {
            charList2.remove(charValue)
        }
        return charList2.isEmpty()
    }

    /*
      * calculates set difference for 2 character arrays i.e. charArr1 - charArr2 removes all charArr2 elements from charArr1
      * complexity should be O(n)
      *
      * @param charArr1 - first character array for set difference
      *
      * @param charArr2 - second character array for set difference
      *
      * @return resultant character array of set difference between charArr1 and charArr2
      */
    fun setDifference(charArr1: CharArray, charArr2: CharArray): CharArray {
        val list1 = toList(charArr1)
        val list2 = toList(charArr2)
        for (charObj in list2) {
            list1.remove(charObj)
        }
        return toCharArray(list1)
    }

    /*
      * function to perform set multiplication of all the sets of strings passed
      *
      * @param setsArray - muliple sets to multiply (can be a set of strings array)
      *
      * @return returns set consisting of set of strings after cartesian product is applied
      */
    fun setMultiplication(vararg setsArray: MutableSet<String>): HashSet<MutableSet<String>> {

        return setMultiplication(0, *setsArray)
    }

    // recursive function to calculate the cartesian product of all the sets of strings passed
    fun setMultiplication(index: Int, vararg setsArray: MutableSet<String>): HashSet<MutableSet<String>> {
        val setsMultiplied = HashSet<MutableSet<String>>()
        if (index == setsArray.size) {
            setsMultiplied.add(HashSet<String>())
        } else {
            val objs = setsArray[index]
            if(objs != null) {
                for (obj in objs) {
                    for (set in setMultiplication(index + 1, *setsArray)) {
                        set.add(obj)
                        setsMultiplied.add(set)
                    }
                }
            }
        }

        return setsMultiplied
    }
}


