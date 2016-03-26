package pl.temomuko.autostoprace.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.ui.widget.TextCircleView;
import pl.temomuko.autostoprace.util.AnimationUtil;
import pl.temomuko.autostoprace.util.ColorGenerator;
import pl.temomuko.autostoprace.util.DateUtil;
import pl.temomuko.autostoprace.util.LogUtil;

/**
 * Created by Rafał Naniewicz on 05.03.2016.
 */
public class LocationRecordsAdapter extends RecyclerView.Adapter<LocationRecordsAdapter.ViewHolder> {

    private static final int COLLAPSED_ITEM_MESSAGE_MAX_LINES = 2;
    private static final int EXPANDED_ITEM_MESSAGE_MAX_LINES = Integer.MAX_VALUE;
    private static final int RESIZING_ANIMATION_DURATION = 250;
    private static final String TAG = LocationRecordsAdapter.class.getSimpleName();

    private List<LocationRecordItem> mSortedLocationRecordItems;
    private Context mAppContext;

    @Inject
    public LocationRecordsAdapter(@AppContext Context context) {
        mSortedLocationRecordItems = new ArrayList<>();
        mAppContext = context;
    }

    public void setSortedLocationRecordItems(List<LocationRecordItem> newSortedLocationRecordItems) {
        mSortedLocationRecordItems = newSortedLocationRecordItems;
        notifyDataSetChanged();
    }

    public void updateLocationRecordItem(LocationRecordItem updatedLocationRecordItem) {
        int index = Collections.binarySearch(mSortedLocationRecordItems, updatedLocationRecordItem);
        if (index >= 0) {
            if (!mSortedLocationRecordItems.get(index).getLocationRecord().equals(
                    updatedLocationRecordItem.getLocationRecord())) {
                changeLocationRecordItemData(index, updatedLocationRecordItem.getLocationRecord());
            }
        } else {
            int insertionIndex = -index - 1;
            insertLocationRecordItem(insertionIndex, updatedLocationRecordItem);
        }
    }

    public void replaceLocationRecord(LocationRecord oldLocationRecord, LocationRecord newLocationRecord) {
        int index = Collections.binarySearch(mSortedLocationRecordItems, new LocationRecordItem(oldLocationRecord));
        if (index >= 0) {
            changeLocationRecordItemData(index, newLocationRecord);
        } else {
            LogUtil.wtf(TAG, "Location item couldn't be replaced, no such item found.");
        }
    }

    private void changeLocationRecordItemData(int locationRecordItemIndex, LocationRecord newLocationRecord) {
        mSortedLocationRecordItems.get(locationRecordItemIndex).setLocationRecord(newLocationRecord);
        notifyItemChanged(locationRecordItemIndex);
    }

    private void insertLocationRecordItem(int insertionIndex, LocationRecordItem locationRecordItem) {
        mSortedLocationRecordItems.add(insertionIndex, locationRecordItem);
        notifyItemInserted(insertionIndex);
    }

    @Override
    public LocationRecordsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mAppContext)
                .inflate(R.layout.item_location_record, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LocationRecordsAdapter.ViewHolder holder, int position) {
        LocationRecordItem item = mSortedLocationRecordItems.get(position);
        LocationRecord locationRecord = item.getLocationRecord();
        setupCountryCodeCircleView(holder, locationRecord);
        setupServerSynchronizationState(holder.mImageServerSynchronizationState, locationRecord);
        setupLocation(holder.mTvLocation, locationRecord);
        setupReceiptDate(holder, locationRecord);
        holder.mTvLocationRecordMessage.setText(locationRecord.getMessage());
        setupMessage(holder, item);
        holder.itemView.setOnClickListener(view -> switchMessageState(holder, item));
    }

    private void setupMessage(ViewHolder holder, LocationRecordItem item) {
        holder.mTvLocationRecordMessage.setMaxLines(
                item.isExpanded() ? EXPANDED_ITEM_MESSAGE_MAX_LINES : COLLAPSED_ITEM_MESSAGE_MAX_LINES
        );
    }

    private void switchMessageState(ViewHolder holder, LocationRecordItem item) {
        if (item.isExpanded()) {
            item.setIsExpanded(false);
            AnimationUtil.animateTextViewMaxLinesChange(holder.mTvLocationRecordMessage,
                    COLLAPSED_ITEM_MESSAGE_MAX_LINES,
                    RESIZING_ANIMATION_DURATION);
        } else {
            item.setIsExpanded(true);
            AnimationUtil.animateTextViewMaxLinesChange(holder.mTvLocationRecordMessage,
                    EXPANDED_ITEM_MESSAGE_MAX_LINES,
                    RESIZING_ANIMATION_DURATION);
        }
    }

    private void setupCountryCodeCircleView(ViewHolder holder, LocationRecord locationRecord) {
        if (locationRecord.getCountryCode() != null) {
            holder.setCountryCodeAvailable(true);
            holder.mCountryCodeCircleView.setText(locationRecord.getCountryCode());
            holder.mCountryCodeCircleView.setCircleColor(ColorGenerator.getStringBasedColor(
                    mAppContext, locationRecord.getCountryCode()));
        } else {
            holder.setCountryCodeAvailable(false);
        }
    }

    private void setupServerSynchronizationState(ImageView serverSynchronizationImageView, LocationRecord locationRecord) {
        if (locationRecord.getServerReceiptDate() != null) {
            Picasso.with(mAppContext).load(R.drawable.ic_cloud_done_black_24dp).fit().into(serverSynchronizationImageView);
        } else {
            Picasso.with(mAppContext).load(R.drawable.ic_cloud_queue_black_24dp).fit().into(serverSynchronizationImageView);
        }
    }

    private void setupLocation(TextView locationTextView, LocationRecord locationRecord) {
        if (locationRecord.getAddress() != null) {
            locationTextView.setText(locationRecord.getAddress());
        } else {
            String coordinates = locationRecord.getLatitude() + ", " + locationRecord.getLongitude();
            locationTextView.setText(coordinates);
        }
    }

    private void setupReceiptDate(ViewHolder holder, LocationRecord locationRecord) {
        if (locationRecord.getServerReceiptDate() != null) {
            Date receiptDate = locationRecord.getServerReceiptDate();
            holder.setDatesVisibility(View.VISIBLE);
            holder.mTvServerReceiptDayMonth.setText(DateUtil.getDayAndMonthString(receiptDate));
            holder.mTvServerReceiptTime.setText(DateUtil.getTimeString(receiptDate));
        } else {
            holder.setDatesVisibility(View.GONE);
        }
    }

    public List<LocationRecordItem> getSortedLocationRecordItems() {
        return mSortedLocationRecordItems;
    }

    @Override
    public int getItemCount() {
        return mSortedLocationRecordItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.country_code_circle_view) TextCircleView mCountryCodeCircleView;
        @Bind(R.id.tv_location) TextView mTvLocation;
        @Bind(R.id.tv_location_record_message) TextView mTvLocationRecordMessage;
        @Bind(R.id.tv_server_receipt_day_month) TextView mTvServerReceiptDayMonth;
        @Bind(R.id.tv_server_receipt_time) TextView mTvServerReceiptTime;
        @Bind(R.id.image_server_synchronization_state) ImageView mImageServerSynchronizationState;
        @Bind(R.id.image_unknown_country_code) ImageView mImageUnknownCountryCode;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDatesVisibility(int visibility) {
            mTvServerReceiptDayMonth.setVisibility(visibility);
            mTvServerReceiptTime.setVisibility(visibility);
        }

        public void setCountryCodeAvailable(boolean availability) {
            if (availability) {
                mCountryCodeCircleView.setVisibility(View.VISIBLE);
                mImageUnknownCountryCode.setVisibility(View.GONE);
            } else {
                mCountryCodeCircleView.setVisibility(View.GONE);
                mImageUnknownCountryCode.setVisibility(View.VISIBLE);
            }
        }
    }
}
