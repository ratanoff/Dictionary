package ru.ratanov.dictionary.db;


public class WordDbSchema {
    public static final class WordTable {
        public static final String NAME = "words";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String TRANSLATE = "translate";
        }
    }
}
