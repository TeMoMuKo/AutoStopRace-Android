package pl.temomuko.autostoprace.ui.phrasebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.injection.ActivityContext;

/**
 * Created by Szymon Kozak on 2016-04-09.
 */
public class PhrasebookAdapter extends RecyclerView.Adapter<PhrasebookAdapter.ViewHolder> {

    private Context mContext;
    private List<Phrasebook.Item> mPhrasebookItems;
    private int mLanguagePosition = Constants.DEFAULT_FOREIGN_LANG_SPINNER_POSITION;

    @Inject
    public PhrasebookAdapter(@ActivityContext Context context) {
        mContext = context;
        mPhrasebookItems = new ArrayList<>();
    }

    public void setPhrasebookItems(List<Phrasebook.Item> phrasebookItems) {
        mPhrasebookItems = phrasebookItems;
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
        Phrasebook.Item phraseItem = mPhrasebookItems.get(position);
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
        return mPhrasebookItems.size();
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
