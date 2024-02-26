package com.greildev.erdmovee.ui.component

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.properties.Delegates

class RunningTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private lateinit var runningText: CharSequence
    private var index by Delegates.notNull<Int>()
    private var delayInMillis = 150L

    private val handler = Handler(Looper.getMainLooper())

    fun setRunningText(text: CharSequence?, delayInMillis: Long = 150L) {
        this.delayInMillis = delayInMillis
        if (text != null) {
            this.runningText = text
        }
        index = 0
        setText("")
        handler.removeCallbacks(characterAdders)
        handler.postDelayed(characterAdders, delayInMillis)
    }

    private val characterAdders: Runnable = object : Runnable {
        override fun run() {
            text = runningText.subSequence(0, index++)
            if (index <= runningText.length) {
                handler.postDelayed(this, delayInMillis)
            }
        }
    }

}