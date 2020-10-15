package id.itborneo.wisatasamarinda.utils

import android.os.Handler

object DelayUtils {
    // Delay mechanism


    fun delay(milisec: Long, delayCallback: () -> Unit) {
        val handler = Handler()
        handler.postDelayed(

            { delayCallback() },
            milisec
        ) // afterDelay will be executed after (secs*1000) milliseconds.
    }
}