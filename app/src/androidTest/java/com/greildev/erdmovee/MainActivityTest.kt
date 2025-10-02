package com.greildev.erdmovee

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private val dummyEmailSuccess = "luolang@mail.com"
    private val dummyPasswordSuccess = "123123123"
    @Test
    fun `navigateToLoginTest`() {
        onView(withId(R.id.tv_skip))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tv_skip)).perform(click())
        onView(withId(R.id.tif_email))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tif_email))
            .perform(typeText(dummyEmailSuccess), closeSoftKeyboard())
        onView(withId(R.id.tif_email))
            .check(matches(withText(dummyEmailSuccess)))
        onView(withId(R.id.tif_password))
            .perform()
        onView(withId(R.id.tif_password))
            .perform(typeText(dummyPasswordSuccess), closeSoftKeyboard())
    }
}