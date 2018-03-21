package ru.ratanov.dictionary.model;


import java.util.UUID;

public class Word {

    private UUID mId;
    private String mTitle;
    private String mTranslate;

    public Word() {
        this(UUID.randomUUID());
    }

    public Word(UUID id) {
        this.mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTranslate() {
        return mTranslate;
    }

    public void setTranslate(String translate) {
        this.mTranslate = translate;
    }
}
