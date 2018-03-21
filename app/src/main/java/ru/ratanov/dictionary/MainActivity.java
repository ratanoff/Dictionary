package ru.ratanov.dictionary;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ru.ratanov.dictionary.adapter.WordAdapter;
import ru.ratanov.dictionary.dialog.SortDialog;
import ru.ratanov.dictionary.model.Word;
import ru.ratanov.dictionary.model.WordFactory;

public class MainActivity extends AppCompatActivity {

    private static final String SORT_DIALOG = "sort_dialog";

    private RecyclerView mRecyclerView;
    private WordAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        WordFactory factory = WordFactory.get(this);
        List<Word> words = factory.getWords();

        if (mAdapter == null) {
            mAdapter = new WordAdapter(this, words);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setWords(words);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_word) {
            Intent intent = DetailActivity.newIntent(this, null);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_filter) {
            SortDialog dialog = new SortDialog();
            dialog.show(getSupportFragmentManager(), SORT_DIALOG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
