package ru.ratanov.dictionary.adapter;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.ratanov.dictionary.DetailActivity;
import ru.ratanov.dictionary.R;
import ru.ratanov.dictionary.model.Word;
import ru.ratanov.dictionary.model.WordFactory;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private Context mContext;
    private List<Word> mWords;

    public WordAdapter(Context context, List<Word> words) {
        mContext = context;
        mWords = words;
    }

    public void setWords(List<Word> words) {
        mWords = words;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        holder.bindItem(mWords.get(position));
    }

    @Override
    public int getItemCount() {
        return mWords.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView translate;

        WordViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            translate = itemView.findViewById(R.id.translate);
        }

        void bindItem(final Word word) {
            title.setText(word.getTitle());
            translate.setText(word.getTranslate());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = DetailActivity.newIntent(mContext, word.getId());
                    mContext.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Dialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.delete_dialog_title)
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteWord(word);
                                }
                            })
                            .create();

                    dialog.show();
                    return true;
                }
            });
        }

        private void deleteWord(Word word) {
            WordFactory factory = WordFactory.get(mContext);
            factory.deleteWord(word);
            mWords.remove(word);
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(), mWords.size());
        }
    }
}
