package com.greildev.erdmovee.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.greildev.erdmovee.R
import com.greildev.erdmovee.utils.Constant.IMAGE_BASE_URL
import com.greildev.erdmovee.utils.Constant.LANGUAGE_IN
import com.greildev.erdmovee.utils.Constant.POLICY_EN
import com.greildev.erdmovee.utils.Constant.POLICY_IN
import com.greildev.erdmovee.utils.Constant.TNC_EN
import com.greildev.erdmovee.utils.Constant.TNC_IN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(lifecycleState) {
        collect {
            action(it)
        }
    }
}

fun ChipGroup.chipGroupSetSelectedChip(): String {
    val selectedChip = mutableListOf<String>()
    for (i in 0 until this.childCount) {
        val chip: Chip = this.getChildAt(i) as Chip
        if (chip.isChecked) {
            selectedChip.add(chip.text.toString())
        }
    }
    return selectedChip.joinToString(", ")
}

fun doubleBackToExit(
    context: Context,
    activity: FragmentActivity?,
    viewLifecycleOwner: LifecycleOwner
) {
    var doubleBackPressed: Long = 0
    val toast = Toast.makeText(
        context,
        getString(context, R.string.tap_twice_to_exit), Toast.LENGTH_SHORT
    )
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
        if (doubleBackPressed + 2000 > System.currentTimeMillis()) {
            activity.finish()
            toast.cancel()
        } else {
            toast.show()
        }
        doubleBackPressed = System.currentTimeMillis()
    }
}

fun String.tncText(context: Context, locale: String): SpannableString {
    val spannableString = SpannableStringBuilder(this)
    locale.lowercase()
    val linkUrlTnc = "https://youtube.com"
    val linkUrlPolicy = "https://google.com"

    val openUrl = { url: String ->
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    val tncText = if (locale == LANGUAGE_IN) TNC_IN else TNC_EN
    val policyText = if (locale == LANGUAGE_IN) POLICY_IN else POLICY_EN

    val startTnc = this.indexOf(tncText)
    val endTnc = startTnc + tncText.length

    val startPolicy = this.indexOf(policyText)
    val endPolicy = startPolicy + policyText.length

    val tncClickableSpan = object : ClickableSpan() {
        override fun onClick(p0: View) {
            openUrl.invoke(linkUrlTnc)
        }
    }

    val policyClickableSpan = object : ClickableSpan() {
        override fun onClick(p0: View) {
            openUrl.invoke(linkUrlPolicy)
        }
    }
    spannableString.setSpan(tncClickableSpan, startTnc, endTnc, 0)
    spannableString.setSpan(policyClickableSpan, startPolicy, endPolicy, 0)

    spannableString.setSpan(
        ForegroundColorSpan(context.getColor(R.color.md_theme_light_primary)),
        startTnc,
        endTnc,
        0
    )
    spannableString.setSpan(
        ForegroundColorSpan(context.getColor(R.color.md_theme_light_primary)),
        startPolicy,
        endPolicy,
        0
    )

    return SpannableString(spannableString)
}

fun Double.formatDecimal(): String {
    val formatter = DecimalFormat("#.#")
    return formatter.format(this)
}

fun String.imgUrlFormatter(): String {
    return IMAGE_BASE_URL + this
}

enum class TransactionType {
    TOPUP,
    RENTAL
}

fun getCurrentDateTime(): String {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatter.format(currentDate.time)
    } else {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        currentDateTime.format(formatter)
    }
}

enum class TransactionMethod {
    BANK_TRANSFER,
    INSTANT,
    VIRTUAL_ACCOUNT,
}