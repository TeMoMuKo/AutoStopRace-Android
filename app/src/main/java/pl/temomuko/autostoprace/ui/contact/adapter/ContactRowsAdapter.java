package pl.temomuko.autostoprace.ui.contact.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.domain.model.ContactField;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.ui.contact.helper.ContactHandler;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class ContactRowsAdapter extends RecyclerView.Adapter<ContactRowsAdapter.ViewHolder> {

    private static final String TAG = ContactRowsAdapter.class.getSimpleName();

    private final Context mContext;
    private List<ContactField> mContactFieldList;
    private OnContactRowClickListener mOnContactRowClickListener;

    @Inject
    public ContactRowsAdapter(@AppContext Context context) {
        mContactFieldList = new ArrayList<>();
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_contact_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactField currentContactField = mContactFieldList.get(position);
        holder.mActionIconImageView.setImageResource(ContactHandler.getIcon(currentContactField.getType()));
        setColorFilter(holder, currentContactField.getType());
        holder.mTvContent.setText(currentContactField.getDisplayedValue());
        holder.mTvContentDescription.setText(currentContactField.getDescription());
        holder.itemView.setOnClickListener(view ->
                mOnContactRowClickListener.onContactRowClick(currentContactField.getType(), currentContactField.getValue())
        );
    }

    @Override
    public int getItemCount() {
        return mContactFieldList.size();
    }

    public void setOnContactRowClickListener(OnContactRowClickListener onContactRowClickListener) {
        mOnContactRowClickListener = onContactRowClickListener;
    }

    public void updateContactRows(List<ContactField> contactFields) {
        mContactFieldList = contactFields;
        notifyDataSetChanged();
    }

    private void setColorFilter(ViewHolder holder, String currentContactRowType) {
        if (ContactHandler.canSetColorFilter(currentContactRowType)) {
            holder.mActionIconImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.accent));
        } else {
            holder.mActionIconImageView.clearColorFilter();
        }
    }

    public interface OnContactRowClickListener {

        void onContactRowClick(String type, String value);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_action_icon) ImageView mActionIconImageView;
        @BindView(R.id.tv_displayed_value) TextView mTvContent;
        @BindView(R.id.tv_description) TextView mTvContentDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
