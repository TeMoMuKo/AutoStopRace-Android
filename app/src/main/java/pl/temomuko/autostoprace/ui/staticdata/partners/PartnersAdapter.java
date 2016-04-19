package pl.temomuko.autostoprace.ui.staticdata.partners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.ActivityContext;
import pl.temomuko.autostoprace.ui.staticdata.PartnersDrawables;

/**
 * Created by Szymon Kozak on 2016-04-18.
 */
public class PartnersAdapter extends RecyclerView.Adapter<PartnersAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> mPartnersDrawables;

    @Inject
    public PartnersAdapter(@ActivityContext Context context) {
        mContext = context;
        mPartnersDrawables = PartnersDrawables.getAsList();
        Collections.shuffle(mPartnersDrawables);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_partner_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        loadPartnerLogo(holder, position);
    }

    private void loadPartnerLogo(ViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mPartnersDrawables.get(position))
                .into(holder.mPartnerImageView);
    }

    @Override
    public int getItemCount() {
        return mPartnersDrawables.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_partner) ImageView mPartnerImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
