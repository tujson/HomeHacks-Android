package dev.synople.homehacks.auditor


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun loginActivityTest() {
        val supportVectorDrawablesButton = onView(
            allOf(
                withId(R.id.email_button), withText("Sign in with email"),
                childAtPosition(
                    allOf(
                        withId(R.id.btn_holder),
                        childAtPosition(
                            withId(R.id.container),
                            0
                        )
                    ),
                    0
                )
            )
        )
        supportVectorDrawablesButton.perform(scrollTo(), click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.email),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.email_layout),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(
            scrollTo(),
            replaceText("auditor@gmail.com"),
            closeSoftKeyboard()
        )

        val appCompatButton = onView(
            allOf(
                withId(R.id.button_next), withText("Next"),
                childAtPosition(
                    allOf(
                        withId(R.id.email_top_layout),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.password),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.password_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText5.perform(scrollTo(), replaceText("password"), closeSoftKeyboard())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.button_done), withText("Sign in"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        Thread.sleep(2000)

        val button = onView(
            allOf(
                withId(R.id.fab),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))
    }

    @Test
    fun loginActivityFailTest() {
        val supportVectorDrawablesButton = onView(
            allOf(
                withId(R.id.email_button), withText("Sign in with email"),
                childAtPosition(
                    allOf(
                        withId(R.id.btn_holder),
                        childAtPosition(
                            withId(R.id.container),
                            0
                        )
                    ),
                    0
                )
            )
        )
        supportVectorDrawablesButton.perform(scrollTo(), click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.email),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.email_layout),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(
            scrollTo(),
            replaceText("homeowner@gmail.com"),
            closeSoftKeyboard()
        )

        val appCompatButton = onView(
            allOf(
                withId(R.id.button_next), withText("Next"),
                childAtPosition(
                    allOf(
                        withId(R.id.email_top_layout),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.password),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.password_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText5.perform(scrollTo(), replaceText("password"), closeSoftKeyboard())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.button_done), withText("Sign in"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        Thread.sleep(2000)

        onView(withText(R.string.auditor_login_not_found)).inRoot(
            withDecorView(
                not(
                    mActivityTestRule.activity.window.decorView
                )
            )
        ).check(matches(isDisplayed()))
    }

    @After
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
