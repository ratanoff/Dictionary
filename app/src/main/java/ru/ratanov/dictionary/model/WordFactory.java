package ru.ratanov.dictionary.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.ratanov.dictionary.db.WordBaseHelper;
import ru.ratanov.dictionary.db.WordCursorWrapper;

import static ru.ratanov.dictionary.db.WordDbSchema.WordTable;
import static ru.ratanov.dictionary.db.WordDbSchema.WordTable.Cols.*;

public class WordFactory {

    private static WordFactory sWordFactory;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static WordFactory get(Context context) {
        if (sWordFactory == null) {
            sWordFactory = new WordFactory(context);
        }
        return sWordFactory;
    }

    private WordFactory(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new WordBaseHelper(mContext).getWritableDatabase();
    }

    public void addWord(Word word) {
        ContentValues values = getContentValues(word);
        mDatabase.insert(WordTable.NAME, null, values);
    }

    public List<Word> getWords() {
        List<Word> words = new ArrayList<>();
        try (WordCursorWrapper cursor = queryWords(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                words.add(cursor.getWord());
                cursor.moveToNext();
            }
        }
        return words;
    }

    public Word getWord(UUID id) {
        WordCursorWrapper cursor = queryWords(
                UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getWord();
        } finally {
            cursor.close();
        }
    }

    public void updateWord(Word word) {
        String uuidString = word.getId().toString();
        ContentValues values = getContentValues(word);
        mDatabase.update(
                WordTable.NAME,
                values,
                UUID + " = ?",
                new String[]{uuidString}
        );
    }

    public void deleteWord(Word word) {
        String uuidString = word.getId().toString();
        mDatabase.delete(
                WordTable.NAME,
                UUID + " = ?",
                new String[]{uuidString}
        );
    }

    private WordCursorWrapper queryWords(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                WordTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                TITLE + " ASC"
        );
        return new WordCursorWrapper(cursor);
    }

    public static ContentValues getContentValues(Word word) {
        ContentValues values = new ContentValues();
        values.put(UUID, word.getId().toString());
        values.put(TITLE, word.getTitle());
        values.put(TRANSLATE, word.getTranslate());
        return values;
    }
}
