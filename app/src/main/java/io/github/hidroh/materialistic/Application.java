package io.github.hidroh.materialistic;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (getResources().getBoolean(R.bool.debug)) {
            GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            GoogleAnalytics.getInstance(this).setDryRun(true);
        }
        GoogleAnalytics.getInstance(this).newTracker(R.xml.ga_config);
    }
}
