package pl.temomuko.autostoprace.ui.staticdata.partners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.ActivityContext;
import pl.temomuko.autostoprace.ui.staticdata.PartnerDrawables;

/**
 * Created by Szymon Kozak on 2016-04-18.
 */
public class PartnersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LOGO = 1;
    private static final String DRAWABLE_TYPE = "drawable";

    private final List<Integer> mItems;
    private final Context context;

    @Inject
    public PartnersAdapter(@ActivityContext Context context) {
        this.context = context;
        mItems = new ArrayList<>();
        mItems.add(R.string.header_partners_strategic);
        mItems.addAll(PartnerDrawables.getStrategic());
        mItems.add(R.string.header_partners_gold);
        mItems.addAll(PartnerDrawables.getGold());
        mItems.add(R.string.header_partners_silver);
        mItems.addAll(PartnerDrawables.getSilver());
        mItems.add(R.string.header_partners_media);
        mItems.addAll(PartnerDrawables.getMedia());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int itemLayout = viewType == TYPE_HEADER ? R.layout.item_partners_header : R.layout.item_partner_row;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return viewType == TYPE_HEADER ? new HeaderViewHolder(itemView) : new LogoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_LOGO) {
            loadPartnerLogo((LogoViewHolder) holder, position);
        } else if (getItemViewType(position) == TYPE_HEADER) {
            ((HeaderViewHolder) holder).mHeaderTextView.setText(mItems.get(position));
        }
    }

    private void loadPartnerLogo(LogoViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(mItems.get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.mPartnerImageView);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        String resType = context.getResources().getResourceTypeName(mItems.get(position));
        if (resType.equals(DRAWABLE_TYPE)) {
            return TYPE_LOGO;
        } else {
            return TYPE_HEADER;
        }
    }

    static class LogoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_partner) ImageView mPartnerImageView;

        public LogoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_header) TextView mHeaderTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
