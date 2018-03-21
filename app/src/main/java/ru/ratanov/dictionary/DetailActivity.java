package ru.ratanov.dictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.UUID;

import ru.ratanov.dictionary.model.Word;
import ru.ratanov.dictionary.model.WordFactory;
import ru.ratanov.dictionary.translate.Translate;
import ru.ratanov.dictionary.util.Utils;

public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_WORD_ID = "word_id";
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String ENDPOINT = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup";

    private RequestQueue mRequestQueue;
    private Gson mGson;
    private WordFactory mFactory;
    private static LruCache<String, List<String>> mCache = new LruCache<String, List<String>>(1024) {
        @Override
        protected int sizeOf(String key, List<String> value) {
            return super.sizeOf(key, value);
        }
    };

    private ActionBar mActionBar;
    private TextInputEditText mWordEditText;
    private TextInputEditText mTranslateEditText;
    private ListView mListView;
    private Button mButton;

    private boolean editMode = false;
    private UUID mId;

    public static Intent newIntent(Context context, UUID wordId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_WORD_ID, wordId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mFactory = WordFactory.get(this);
        mRequestQueue = Volley.newRequestQueue(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        mGson = gsonBuilder.create();

        mActionBar = getSupportActionBar();
        mWordEditText = findViewById(R.id.word_edit_text);
        mTranslateEditText = findViewById(R.id.translate_edit_text);
        mListView = findViewById(R.id.list_view);
        mButton = findViewById(R.id.button);

        setupListeners();

        mId = (UUID) getIntent().getSerializableExtra(EXTRA_WORD_ID);
        if (mId != null) {
            editMode = true;
            updateEditUI();
        } else {
            updateAddUI();
        }
    }

    private void updateEditUI() {
        if (mActionBar != null) {
            mActionBar.setTitle(R.string.edit_word);
        }

        final Word currentWord = mFactory.getWord(mId);
        mWordEditText.setText(currentWord.getTitle());
        mTranslateEditText.setText(currentWord.getTranslate());
        fetchTranslate();
        mButton.setText("Сохранить");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsAreNotEmpty()) {
                    currentWord.setTitle(Utils.capitalize(mWordEditText.getText().toString()));
                    currentWord.setTranslate(Utils.capitalize(mTranslateEditText.getText().toString()));
                    mFactory.updateWord(currentWord);
                    finish();
                } else {
                    Toast.makeText(DetailActivity.this, R.string.fill_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateAddUI() {
        if (mActionBar != null) {
            mActionBar.setTitle(R.string.add_word);
        }

        mButton.setText(R.string.add);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsAreNotEmpty()) {
                    Word word = new Word();
                    word.setTitle(Utils.capitalize(mWordEditText.getText().toString()));
                    word.setTranslate(Utils.capitalize(mTranslateEditText.getText().toString()));
                    mFactory.addWord(word);
                    finish();
                } else {
                    Toast.makeText(DetailActivity.this, R.string.fill_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.action_delete_word);
        if (!editMode) {
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_word) {
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_dialog_title)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Word word = mFactory.getWord(mId);
                            mFactory.deleteWord(word);
                            finish();
                        }
                    })
                    .create();

            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListeners() {
        mWordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    fetchTranslate();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String variant = (String) mListView.getItemAtPosition(position);
                mTranslateEditText.setText(Utils.capitalize(variant));
            }
        });
    }

    private boolean fieldsAreNotEmpty() {
        return !mWordEditText.getText().toString().isEmpty() &&
                !mTranslateEditText.getText().toString().isEmpty();
    }

    private void fetchTranslate() {
        String word = mWordEditText.getText().toString();
        if (word.isEmpty()) {
            return;
        }
        Log.i(TAG, "fetchTranslate: " + mCache.size());
        List<String> variantsFromCache = getVariantsFromCache(word.toLowerCase());
        if (variantsFromCache != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailActivity.this,
                    android.R.layout.simple_list_item_1, variantsFromCache);
            mListView.setAdapter(adapter);
        } else {
            Log.i(TAG, "fetchTranslate: NULL");
            String url = Uri.parse(ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("key", getString(R.string.api_key))
                    .appendQueryParameter("lang", "ru-en")
                    .appendQueryParameter("text", word)
                    .build()
                    .toString();
            StringRequest request = new StringRequest(Request.Method.GET, url, onWordsLoaded, onWordsError);
            mRequestQueue.add(request);
        }
    }

    private Response.Listener<String> onWordsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Translate translate = mGson.fromJson(response, Translate.class);
            List<String> variants = translate.getVariants();
            saveVariantsToCache(mWordEditText.getText().toString().toLowerCase(), variants);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(DetailActivity.this,
                    android.R.layout.simple_list_item_1, variants);
            mListView.setAdapter(adapter);
        }
    };

    private Response.ErrorListener onWordsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(DetailActivity.this, R.string.error_data_loading, Toast.LENGTH_SHORT).show();
        }
    };

    private void saveVariantsToCache(String word, List<String> variants) {
        Log.i(TAG, "saveVariantsToCache: ");
        if (getVariantsFromCache(word) == null) {
            mCache.put(word, variants);
            Log.i(TAG, "SAVED " + word);
            Log.i(TAG, "SIZE = " + mCache.size());
        }
    }

    private List<String> getVariantsFromCache(String word) {
        return mCache.get(word);
    }
}
