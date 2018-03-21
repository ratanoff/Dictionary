package ru.ratanov.dictionary.db;


import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import ru.ratanov.dictionary.db.WordDbSchema.WordTable;
import ru.ratanov.dictionary.model.Word;

public class WordCursorWrapper extends CursorWrapper {

    public WordCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Word getWord() {
        String uuidString = getString(getColumnIndex(WordTable.Cols.UUID));
        String title = getString(getColumnIndex(WordTable.Cols.TITLE));
        String translate = getString(getColumnIndex(WordTable.Cols.TRANSLATE));

        Word word = new Word(UUID.fromString(uuidString));
        word.setTitle(title);
        word.setTranslate(translate);

        return word;
    }
}
