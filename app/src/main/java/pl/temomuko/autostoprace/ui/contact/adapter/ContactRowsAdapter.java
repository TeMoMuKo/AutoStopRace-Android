package pl.temomuko.autostoprace.ui.contact.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.ContactRow;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class ContactRowsAdapter extends RecyclerView.Adapter<ContactRowsAdapter.ViewHolder> {

    private static final String TAG = ContactRowsAdapter.class.getSimpleName();

    private List<ContactRow> mContactRowList;
    private Context mContext;
    private OnContactRowClickListener mOnContactRowClickListener;

    public interface OnContactRowClickListener {

        void onContactRowClick(String type, String value);
    }

    @Inject
    public ContactRowsAdapter(@AppContext Context context) {
        mContactRowList = new ArrayList<>();
        mContext = context;
    }

    public void setOnContactRowClickListener(OnContactRowClickListener onContactRowClickListener) {
        mOnContactRowClickListener = onContactRowClickListener;
    }

    public void updateContactRows(List<ContactRow> contactRows) {
        mContactRowList = contactRows;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_contact_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactRow currentContactRow = mContactRowList.get(position);
        //// TODO: 17.04.2016 image
        holder.mTvContent.setText(currentContactRow.getDisplayedValue());
        holder.mTvContentDescription.setText(currentContactRow.getDescription());
        holder.itemView.setOnClickListener(view ->
                mOnContactRowClickListener.onContactRowClick(currentContactRow.getType(), currentContactRow.getValue())
        );
    }

    @Override
    public int getItemCount() {
        return mContactRowList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_action_icon) ImageView mActionIconImageView;
        @Bind(R.id.tv_displayed_value) TextView mTvContent;
        @Bind(R.id.tv_description) TextView mTvContentDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
