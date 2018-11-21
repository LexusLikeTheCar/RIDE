package edu.osu.ride;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UserSettingsEspressoTesting {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void ensureTextChangesWork() {
        // Type text and then press the button.
        onView(withId(R.id.login_text_view))
                .perform(typeText("uhlir.7@osu.edu"), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.login_text_view)).check(matches(withText("uhlir.7@osu.edu")));
    }

    @Test
    public void newActivity() {

        onView(withId(R.id.sign_up_button)).perform(click());

        // This view is in a different Activity, no need to tell Espresso.
        onView(withId(R.id.signup_text_view)).check(matches(withText("NewText")));
    }
    @Test
    public void newActivityDeletedUser() {

        onView(withId(R.id.delete_Button)).perform(click());

        // This view is in a different Activity, no need to tell Espresso.
        onView(withText("Yes")).perform(click());
        onView(withText("Account Deleted")).check(matches(isDisplayed()));
    }


}
