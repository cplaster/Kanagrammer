package com.xyphoid.kanagrammer

import kotlin.collections.HashSet;
import kotlin.collections.Set;
/**
 * Created by Chad Plaster on 7/19/2017.
 */


class ExactWordSolverTask : AsyncSolverTask<SolverArgs, String, Set<String>?>() {

    internal lateinit var _solverArgs: SolverArgs

    override fun doInBackground(vararg solverArgs: SolverArgs): HashSet<String>? {
        this._solverArgs = solverArgs[0]
        this._solverArgs.solverTask = this

        val anagramCore = AnagramCore(this._solverArgs)

        publishProgress("Finding all matches...\n")
        return anagramCore.findExactAnagrams()


    }

    override fun Publish(s: String) {
        publishProgress(s)
    }

    override fun onProgressUpdate(vararg progress: String) {
        this._solverArgs.mainActivity.PublishProgress(progress[0])
    }

    override fun onPostExecute(results: Set<String>?) {
        if (results != null) {
            for (s in results) {
                this._solverArgs.mainActivity.PublishAppend(s + "\n")
            }
        } else {
            this._solverArgs.mainActivity.PublishAppend("No matches.")
        }
    }
}


