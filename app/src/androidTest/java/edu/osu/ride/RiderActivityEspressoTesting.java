package edu.osu.ride;
import android.net.Uri;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.openLinkWithText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RiderActivityEspressoTesting {

    @Rule
    public ActivityTestRule<RiderActivity> mActivityRule =
            new ActivityTestRule<>(RiderActivity.class);

    @Test
    public void ensureTextChangesWork() {
        // Type text and then press the button.
        onView(withId(R.id.settings_and_destination_search_bar))
                .perform(typeText("uhlir.7@osu.edu"), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.settings_and_destination_search_bar)).check(matches(withText("uhlir.7@osu.edu")));
    }

    @Test
    public void newModal() {

        onView(withId(R.id.all_rides_filter)).perform(click());

        onView(withId(R.id.optimal_uber)).check(matches(isDisplayed()));
        onView(withId(R.id.optimal_bird)).check(matches(isDisplayed()));
        onView(withId(R.id.optimal_lime)).check(matches(isDisplayed()));
        onView(withId(R.id.optimal_lyft)).check(matches(isDisplayed()));
    }

    @Test
    public void filteredLook() {

        onView(withId(R.id.all_rides_filter)).perform(click());
        onView(withId(R.id.show_birds)).perform(click());
        onView(withId(R.id.open_bird)).check(matches(isDisplayed()));
    }
    @Test
    public void deepLinking() {

        onView(withId(R.id.all_rides_filter)).perform(click());
        onView(withId(R.id.optimal_lyft)).perform(click());
        intended(toPackage("me.lyft.android"));
    }
}
