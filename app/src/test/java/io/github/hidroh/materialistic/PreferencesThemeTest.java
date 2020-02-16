package io.github.hidroh.materialistic;

import android.app.Activity;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import io.github.hidroh.materialistic.test.TestRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(TestRunner.class)
public class PreferencesThemeTest {

    private Activity activity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(Activity.class);
        activity.getTheme().setTo(activity.getResources().newTheme());
    }

    @Test
    public void testDefaultTheme() {
        Integer originalTheme = shadowOf(activity).callGetThemeResId();
        Preferences.Theme.apply(activity, false, false);
        assertThat(shadowOf(activity).callGetThemeResId()).isEqualTo(originalTheme);
    }

    @Test
    public void testDarkTheme() {
        PreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putString(activity.getString(R.string.pref_theme), "dark")
                .commit();
        Preferences.Theme.apply(activity, false, false);
        assertThat(shadowOf(activity).callGetThemeResId()).isEqualTo(R.style.AppTheme_Dark);
    }
}
