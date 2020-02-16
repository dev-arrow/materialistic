package io.github.hidroh.materialistic;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import javax.inject.Inject;

import io.github.hidroh.materialistic.data.ReadabilityClient;
import io.github.hidroh.materialistic.data.WebItem;
import io.github.hidroh.materialistic.test.RobolectricGradleTestRunner;
import io.github.hidroh.materialistic.test.TestReadabilityActivity;
import io.github.hidroh.materialistic.test.TestWebItem;
import io.github.hidroh.materialistic.test.shadow.ShadowSupportPreferenceManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@Config(shadows = {ShadowSupportPreferenceManager.class})
@RunWith(RobolectricGradleTestRunner.class)
public class ReadabilityFragmentLazyLoadTest {
    private TestReadabilityActivity activity;
    private ActivityController<TestReadabilityActivity> controller;
    @Inject ReadabilityClient readabilityClient;
    private WebFragment fragment;

    @Before
    public void setUp() {
        TestApplication.applicationGraph.inject(this);
        reset(readabilityClient);
        controller = Robolectric.buildActivity(TestReadabilityActivity.class);
        activity = controller.create().start().resume().visible().get();
        ShadowSupportPreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putString(activity.getString(R.string.pref_story_display),
                        activity.getString(R.string.pref_story_display_value_readability))
                .apply();
        Bundle args = new Bundle();
        WebItem item = new TestWebItem() {
            @Override
            public String getId() {
                return "1";
            }

            @Override
            public String getUrl() {
                return "http://example.com/article.html";
            }
        };
        args.putParcelable(WebFragment.EXTRA_ITEM, item);
        fragment = (WebFragment) Fragment.instantiate(activity, WebFragment.class.getName(), args);
    }

    @Test
    public void testLazyLoadByDefault() {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, "tag")
                .commit();
        verify(readabilityClient, never()).parse(any(), any(),
                any(ReadabilityClient.Callback.class));
        reset(readabilityClient);
        fragment.loadNow();
        verify(readabilityClient).parse(any(), any(),
                any(ReadabilityClient.Callback.class));
    }

    @Test
    public void testVisible() {
        ShadowSupportPreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putBoolean(activity.getString(R.string.pref_lazy_load), false)
                .commit();
        fragment.setUserVisibleHint(true);
        verify(readabilityClient, never()).parse(any(), any(),
                any(ReadabilityClient.Callback.class));
        reset(readabilityClient);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, "tag")
                .commit();
        verify(readabilityClient).parse(any(), any(),
                any(ReadabilityClient.Callback.class));
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }
}
