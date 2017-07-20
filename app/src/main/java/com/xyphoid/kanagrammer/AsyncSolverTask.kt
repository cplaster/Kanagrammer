package com.xyphoid.kanagrammer

import android.os.AsyncTask;

/**
 * Created by Chad Plaster on 7/19/2017.
 */


abstract class AsyncSolverTask<Params, Progress, Result> : AsyncTask<Params, Progress, Result>() {

    public open fun Publish(s: Progress): Any {
        publishProgress(s)
        return 0;
    }

    public open override fun onPostExecute(result: Result) {
        super.onPostExecute(result)
    }
}

