package UI;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.opendeliver.oppia.R;
import org.digitalcampus.oppia.activity.AboutActivity;
import org.digitalcampus.oppia.activity.WelcomeActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import TestRules.DisableAnimationsRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AboutUITest {

    @Rule
    public ActivityTestRule<AboutActivity> aboutActivityTestRule =
            new ActivityTestRule<>(AboutActivity.class);

    @Rule
    public DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();


    @Test
    public void clickPrivacyTab() throws Exception {
        onView(withText(R.string.tab_title_privacy))
                .perform(click());
    }

    @Test
    public void clickHelpTab() throws Exception {
        onView(withText(R.string.tab_title_help))
                .perform(click());
    }

    @Test
    public void clickAboutTab() throws Exception {
        onView(withText(R.string.tab_title_about))
                .perform(click());
    }
}
