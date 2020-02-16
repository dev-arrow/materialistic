package io.github.hidroh.materialistic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import io.github.hidroh.materialistic.test.TestListActivity;

import static junit.framework.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class BaseListActivityTest {
    private ActivityController<TestListActivity> controller;
    private TestListActivity activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(TestListActivity.class);
        activity = controller.create().start().resume().visible().get();
    }

    @Test
    public void testCreate() {
        assertNull(shadowOf(activity).getOptionsMenu().findItem(R.id.menu_comment));
        assertNull(shadowOf(activity).getOptionsMenu().findItem(R.id.menu_story));
        assertNull(shadowOf(activity).getOptionsMenu().findItem(R.id.menu_share));
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }
}
