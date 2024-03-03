package com.greildev.erdmovee.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.LayoutStatedViewBinding

class StatedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutStatedViewBinding

    init {
        binding = LayoutStatedViewBinding.inflate(LayoutInflater.from(context), this, true)
        binding.btnRetry.isVisible = false
    }

    fun setMessage(
        title: String,
        description: String,
        btnTitle: String? = null,
        state: StatedViewState,
        action: (() -> (Unit))? = null
    ) = with(binding) {
        tvErrorTitle.text = title
        tvErrorDescription.text = description
        btnRetry.isVisible = btnTitle != null
        btnRetry.text = btnTitle
        when (state) {
            StatedViewState.ERROR -> {
                binding.ivIllustState.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.illust_error
                    )
                )
            }

            StatedViewState.EMPTY -> {
                binding.ivIllustState.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.illust_empty
                    )
                )
            }
        }
        btnRetry.setOnClickListener {
            action?.invoke()
        }
    }
}

enum class StatedViewState {
    ERROR,
    EMPTY,
}