/*
 * Copyright (c) 2016 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hidroh.materialistic;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.util.ActivityController;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@SuppressWarnings("ConstantConditions")
@RunWith(RobolectricGradleTestRunner.class)
public class OfflineWebActivityTest {
    private ActivityController<OfflineWebActivity> controller;
    private OfflineWebActivity activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(OfflineWebActivity.class);
    }

    @Test
    public void testNoUrl() {
        activity = controller.create().get();
        assertThat(activity).isFinishing();
    }

    @Test
    public void testLoadUrl() {
        activity = controller.withIntent(new Intent()
                .putExtra(OfflineWebActivity.EXTRA_URL, "http://example.com"))
                .create()
                .get();
        assertThat(activity.getTitle()).contains("http://example.com");
        WebView webView = (WebView) activity.findViewById(R.id.web_view);
        View progress = activity.findViewById(R.id.progress);
        ShadowWebView shadowWebView = shadowOf(webView);
        assertThat(shadowWebView.getLastLoadedUrl())
                .contains("http://example.com");
        shadowWebView.getWebViewClient().onPageFinished(webView, "http://example.com");
        assertThat(activity.getTitle()).isNullOrEmpty(); // web view title
        shadowWebView.getWebChromeClient().onProgressChanged(webView, 50);
        assertThat(progress).isVisible();
        shadowWebView.getWebChromeClient().onProgressChanged(webView, 100);
        assertThat(progress).isNotVisible();
    }

    @Test
    public void testHomeButton() {
        activity = controller.withIntent(new Intent()
                .putExtra(OfflineWebActivity.EXTRA_URL, "http://example.com"))
                .create()
                .start()
                .resume()
                .visible()
                .get();
        shadowOf(activity).clickMenuItem(android.R.id.home);
        assertThat(activity).isFinishing();
    }

    @After
    public void tearDown() {
        controller.destroy();
    }
}
