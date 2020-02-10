package io.github.hidroh.materialistic;

import android.app.SearchManager;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import io.github.hidroh.materialistic.data.AlgoliaClient;
import io.github.hidroh.materialistic.data.HackerNewsClient;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.SearchRecentSuggestionsProvider;

public class SearchActivity extends BaseListActivity {

    private static final int MAX_RECENT_SUGGESTIONS = 10;
    private String mQuery;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().hasExtra(SearchManager.QUERY)) {
            mQuery = getIntent().getStringExtra(SearchManager.QUERY);
        }
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(mQuery)) {
            mTitle = getString(R.string.title_activity_search_query, mQuery);
            setTitle(mTitle);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchRecentSuggestionsProvider.PROVIDER_AUTHORITY,
                    SearchRecentSuggestionsProvider.MODE) {
                @Override
                public void saveRecentQuery(String queryString, String line2) {
                    truncateHistory(getContentResolver(), MAX_RECENT_SUGGESTIONS - 1);
                    super.saveRecentQuery(queryString, line2);
                }
            };
            suggestions.saveRecentQuery(mQuery, null);
        }
    }

    @Override
    protected String getDefaultTitle() {
        if (TextUtils.isEmpty(mTitle)) {
            return getString(R.string.title_activity_search);
        }

        return mTitle;
    }

    @Override
    protected Fragment instantiateListFragment() {
        if (TextUtils.isEmpty(mQuery)) {
            return ListFragment.instantiate(this, HackerNewsClient.getInstance(this),
                    ItemManager.FetchMode.top.name());
        } else {
            return ListFragment.instantiate(this, AlgoliaClient.getInstance(this), mQuery);
        }
    }

    @Override
    protected boolean isItemOptionsMenuVisible() {
        return mSelectedItem != null;
    }
}
