package edu.uark.ahnelson.assignment2solution2024.ToDoListActivity


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import edu.uark.ahnelson.assignment2solution2024.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ToDoListActivityTestAddAssignment {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(ToDoListActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.POST_NOTIFICATIONS"
        )

    @Test
    fun toDoListActivityTestAddAssignment() {
        val floatingActionButton = onView(
            allOf(
                withId(R.id.fab), withContentDescription("Add a ToDoItem"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.etToDoTitle),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("Assignment 4"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.editTextDate), withText("Dec 31, 1969 6:00 PM"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val materialRadioButton = onView(
            allOf(
                withClassName(`is`("com.google.android.material.radiobutton.MaterialRadioButton")),
                withText("PM"),
                childAtPosition(
                    allOf(
                        withClassName(`is`("android.widget.RadioGroup")),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            3
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialRadioButton.perform(click())

        val materialButton3 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton3.perform(scrollTo(), click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.etEditContent),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("Test"), closeSoftKeyboard())

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.fabSave), withContentDescription("Save To Do Item"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.tvTitle), withText("Assignment 4"),
                withParent(withParent(withId(R.id.recyclerview))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Assignment 4")))

        val textView2 = onView(
            allOf(
                withId(R.id.tvContent), withText("Test"),
                withParent(withParent(withId(R.id.recyclerview))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Test")))

        val materialCheckBox = onView(
            allOf(
                withId(R.id.cbCompleted), withText("Is Completed?"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recyclerview),
                        1
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialCheckBox.perform(click())
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
