package com.example.android.cinematik.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.cinematik.Interfaces.CircleTransform;
import com.example.android.cinematik.R;
import com.example.android.cinematik.pojos.CastMember;
import com.example.android.cinematik.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabina on 4/8/2018.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private final static String TAG = CastAdapter.class.getSimpleName();
    private List<CastMember> castList = new ArrayList<>();
    Context context;
    public int id = 0;

    public CastAdapter(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .grid_item_cast, null);
        CastViewHolder castViewHolder = null;

        castViewHolder = new CastViewHolder(layout);
        return castViewHolder;
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {

        final String profileUrl = NetworkUtils.buildUrlPoster(castList.get(position)
                .getCastProfile().substring(1), NetworkUtils.URL_PROFILE_SIZE_VALUE);
        Picasso.with(context)
                .load(profileUrl)
                .transform(new CircleTransform())
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        }
        return 0;
    }

    public void addList(List<CastMember> castMembers) {
        this.castList.addAll(castMembers);
        notifyDataSetChanged();
    }


    public class CastViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;

        public CastViewHolder(View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.imageViewProfile);
        }
    }
}
