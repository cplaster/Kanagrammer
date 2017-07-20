package com.xyphoid.kanagrammer

import android.os.AsyncTask;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Chad Plaster on 7/19/2017.
 */

class SolverArgs(dictionary: HashMap<String, String>, mainActivity: MainActivity, letters: String, lengths: IntArray?) {

    var dictionary: HashMap<String, String>
    var mainActivity: MainActivity;
    var solverTask: AsyncSolverTask<*, *, *>? = null
    var letters: String
    var lengths: IntArray = kotlin.IntArray(1);
    var minWordLength: Int = 0
        private set


    init {

        this.dictionary = dictionary
        this.mainActivity = mainActivity
        this.letters = letters

        if (lengths != null) { // this might not work correctly...
            this.lengths = lengths
            Arrays.sort(lengths)
            this.minWordLength = lengths[0]
        } else {
            this.minWordLength = 0
        }
    }
}
