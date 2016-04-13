package pl.temomuko.autostoprace.ui.phrasebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Phrasebook;

/**
 * Created by Szymon Kozak on 2016-04-09.
 */
public class PhrasebookAdapter extends RecyclerView.Adapter<PhrasebookAdapter.ViewHolder> implements Filterable {

    private int mLanguagePosition = Constants.DEFAULT_FOREIGN_LANG_SPINNER_POSITION;
    private Context mContext;
    private OnIsEmptyResultsListener mOnIsEmptyResultsListener;
    private List<Phrasebook.Item> mActualPhrasebookItems;
    private List<Phrasebook.Item> mAllPhrasebookItems;
    private Filter mFilter;

    public PhrasebookAdapter(Context context, OnIsEmptyResultsListener onIsEmptyResultsListener) {
        mContext = context;
        mOnIsEmptyResultsListener = onIsEmptyResultsListener;
        mActualPhrasebookItems = new ArrayList<>();
        setupFilter();
    }

    public void setActualPhrasebookItems(List<Phrasebook.Item> actualPhrasebookItems) {
        mActualPhrasebookItems = actualPhrasebookItems;
        mAllPhrasebookItems = mActualPhrasebookItems;
    }

    public void setLanguagePosition(int languagePosition) {
        mLanguagePosition = languagePosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_phrasebook, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Phrasebook.Item phraseItem = mActualPhrasebookItems.get(position);
        holder.mPhraseTextView.setText(phraseItem.getOriginalPhrase());
        holder.mTranslationTextView.setText(phraseItem.getTranslation(mLanguagePosition));
        setupMessageIcon(holder);
    }

    private void setupMessageIcon(ViewHolder holder) {
        Picasso.with(mContext)
                .load(R.drawable.ic_message_black_24dp)
                .placeholder(R.drawable.ic_message_black_24dp)
                .into(holder.mTranslationImageView);
    }

    @Override
    public int getItemCount() {
        return mActualPhrasebookItems.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void clearFilter() {
        setActualPhrasebookItems(mAllPhrasebookItems);
        notifyDataSetChanged();
    }

    private void setupFilter() {
        mFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    List<Phrasebook.Item> filteredItems = collectFilteredItems(constraint);
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                } else {
                    results.values = mAllPhrasebookItems;
                    results.count = mAllPhrasebookItems.size();
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mActualPhrasebookItems = (List<Phrasebook.Item>) results.values;
                mOnIsEmptyResultsListener.onIsEmptyResults(results.count == 0);
                notifyDataSetChanged();
            }
        };
    }

    private List<Phrasebook.Item> collectFilteredItems(CharSequence constraint) {
        List<Phrasebook.Item> filteredItems = new ArrayList<>();
        for (Phrasebook.Item item : mAllPhrasebookItems) {
            String originalPhrase = normalize(item.getOriginalPhrase().toLowerCase());
            String actualPhrase = normalize(constraint.toString().toLowerCase());
            if (originalPhrase.contains(actualPhrase)) filteredItems.add(item);
        }
        return filteredItems;
    }

    public String normalize(String query) {
        query = Normalizer.normalize(query, Normalizer.Form.NFD);
        query = query.replaceAll("[łŁ]", "l"); //fix normalizer bug.
        return query.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_phrase) TextView mPhraseTextView;
        @Bind(R.id.tv_translation) TextView mTranslationTextView;
        @Bind(R.id.iv_translation) ImageView mTranslationImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
