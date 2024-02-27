package com.greildev.erdmovee.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.greildev.erdmovee.R
import com.greildev.erdmovee.databinding.SnackbarMoveeBinding

object MoveeSnackbar {
    @SuppressLint("RestrictedApi")
    fun showSnackbarCustom(
        context: Context,
        root: View,
        text: String,
        state: StateSnackbar,
        action: () -> Unit
    ) {
        val typedValueBackground = TypedValue()
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = SnackbarMoveeBinding.inflate(inflater)
        val snackbar = Snackbar.make(root, "", Snackbar.LENGTH_INDEFINITE)

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        snackbarLayout.apply {
            (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.TOP
            layoutParams = (layoutParams as FrameLayout.LayoutParams)
            addView(binding.root, 0)
        }

        context.theme.resolveAttribute(android.R.attr.windowBackground, typedValueBackground, true)

        binding.textSnackbar.text = text
        binding.iconSnackbar.setColorFilter(context.getColor(R.color.md_theme_light_surface))
        when (state) {
            StateSnackbar.SUCCESS -> {
                binding.textSnackbar.setTextColor(context.getColor(R.color.md_theme_light_surface))
                binding.iconSnackbar.setImageResource(R.drawable.ic_check_circle_24)
                binding.cardSnackbar.setCardBackgroundColor(context.getColor(R.color.green_success))
            }

            StateSnackbar.ERROR -> {
                binding.textSnackbar.setTextColor(context.getColor(R.color.md_theme_light_surface))
                binding.iconSnackbar.setImageResource(R.drawable.ic_block_24)
                binding.cardSnackbar.setCardBackgroundColor(context.getColor(R.color.md_theme_light_error))
            }

            StateSnackbar.INFO -> {
                binding.textSnackbar.setTextColor(context.getColor(R.color.md_theme_light_surface))
                binding.iconSnackbar.setImageResource(R.drawable.ic_info_24)
                binding.cardSnackbar.setCardBackgroundColor(context.getColor(R.color.yellow_info))
            }

            StateSnackbar.COMMON -> {
                binding.iconSnackbar.visibility = View.GONE
                binding.textSnackbar.textAlignment = View.TEXT_ALIGNMENT_CENTER
                binding.cardSnackbar.setCardBackgroundColor(typedValueBackground.data)
            }
        }
        snackbar.apply {
            view.setBackgroundColor(Color.TRANSPARENT)
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            val slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)
            val slideUpAnim = AnimationUtils.loadAnimation(context, R.anim.slide_up)

            view.startAnimation(slideDownAnim)

            slideDownAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    view.startAnimation(slideUpAnim)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })

            slideUpAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    action.invoke()
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            show()
        }
    }
}

enum class StateSnackbar {
    SUCCESS,
    ERROR,
    INFO,
    COMMON
}