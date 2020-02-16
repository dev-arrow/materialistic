package io.github.hidroh.materialistic.test;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import io.github.hidroh.materialistic.InjectableActivity;
import io.github.hidroh.materialistic.MultiPaneListener;
import io.github.hidroh.materialistic.R;
import io.github.hidroh.materialistic.data.ItemManager;

import static org.mockito.Mockito.mock;

public class ListActivity extends InjectableActivity implements MultiPaneListener {
    public MultiPaneListener multiPaneListener = mock(MultiPaneListener.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    public void onItemSelected(ItemManager.WebItem story) {
        multiPaneListener.onItemSelected(story);
    }

    @Override
    public ItemManager.WebItem getSelectedItem() {
        return multiPaneListener.getSelectedItem();
    }

    @Override
    public boolean isMultiPane() {
        return getResources().getBoolean(R.bool.multi_pane);
    }
}
