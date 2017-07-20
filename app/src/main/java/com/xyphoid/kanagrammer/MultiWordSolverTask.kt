package com.xyphoid.kanagrammer

import java.util.Arrays;
import kotlin.collections.MutableSet;

/**
 * Created by Chad Plaster on 7/19/2017.
 */

class MultiWordSolverTask : AsyncSolverTask<SolverArgs, String, HashSet<Set<String>>>() {

    internal lateinit var _solverArgs: SolverArgs

    override fun doInBackground(vararg solverArgs: SolverArgs): HashSet<Set<String>> {
        this._solverArgs = solverArgs[0]
        this._solverArgs.solverTask = this

        val anagramCore = AnagramCore(this._solverArgs)

        return anagramCore.findSpecificAnagrams()
    }

    override fun Publish(s: String) {
        publishProgress(s)
    }

    override fun onProgressUpdate(vararg progress: String) {
        this._solverArgs.mainActivity.PublishProgress(progress[0])
    }

    override fun onPostExecute(anagram: HashSet<Set<String>>) {

        this._solverArgs.mainActivity.PublishAppend("\n\n")

        if (!anagram.isEmpty()) {
            var match: Boolean = false
            for (combo in anagram) {
                val size = combo.size

                if (size == this._solverArgs.lengths.size) {
                    val clengths = IntArray(size)
                    var tempstring = ""
                    var iter2 = 0
                    for (s in combo) {
                        tempstring += s + " "
                        clengths[iter2] = s.length
                        iter2++
                    }
                    Arrays.sort(clengths)

                    if (Arrays.equals(this._solverArgs.lengths, clengths)) {
                        match = true
                        this._solverArgs.mainActivity.PublishAppend(tempstring + "\n")
                    }
                }
            }
            if (!match) {
                this._solverArgs.mainActivity.PublishAppend("No matches found.\n")
            }
        } else {
            this._solverArgs.mainActivity.PublishAppend("No matches found.\n")
        }
    }
}

