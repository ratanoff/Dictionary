
package ru.ratanov.dictionary.translate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Syn {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("pos")
    @Expose
    private String pos;
    @SerializedName("asp")
    @Expose
    private String asp;
    @SerializedName("gen")
    @Expose
    private String gen;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getAsp() {
        return asp;
    }

    public void setAsp(String asp) {
        this.asp = asp;
    }

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

}
