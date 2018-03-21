
package ru.ratanov.dictionary.translate;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Translate {

    @SerializedName("head")
    @Expose
    private Head head;
    @SerializedName("def")
    @Expose
    private List<Def> def = null;

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public List<Def> getDef() {
        return def;
    }

    public void setDef(List<Def> def) {
        this.def = def;
    }

    public List<String> getVariants() {
        List<String> synonyms = new ArrayList<>();
        for (Def def : getDef()) {
            for (Tr tr : def.getTr()) {
                synonyms.add(tr.getText());
            }
        }
        return synonyms;
    }
}
