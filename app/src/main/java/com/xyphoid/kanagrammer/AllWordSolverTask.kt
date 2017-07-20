package com.xyphoid.kanagrammer

import kotlin.collections.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * Created by Chad Plaster on 7/19/2017.
 */


class AllWordSolverTask : AsyncSolverTask<SolverArgs, String, HashMap<String, Int>>() {

    internal lateinit var _solverArgs: SolverArgs

    protected override fun doInBackground(vararg solverArgs: SolverArgs): HashMap<String, Int>? {
        this._solverArgs = solverArgs[0]
        this._solverArgs.solverTask = this;

        val anagramCore = AnagramCore(this._solverArgs)

        publishProgress("Finding all matches...\n")

        return anagramCore.findExactSubsets()
    }

    override fun Publish(s: String) {
        publishProgress(s)
    }

    override fun onProgressUpdate(vararg progress: String) {
        this._solverArgs.mainActivity?.PublishProgress(progress[0])
    }

    override fun onPostExecute(combos: HashMap<String, Int>) {

        this._solverArgs.mainActivity?.PublishAppend("\n\n")
        val results = AnagramSolverHelper.sortByComparator(combos, false)

        if (results != null) {
            val keys = results!!.keys
            for (key in keys) {
                this._solverArgs.mainActivity?.PublishAppend(key + " : " + results!!.get(key) + "\n")
            }
        } else {
            this._solverArgs.mainActivity?.PublishAppend("No matches.")
        }
    }
}
