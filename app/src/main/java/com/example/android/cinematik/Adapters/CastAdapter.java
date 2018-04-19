package com.example.android.cinematik.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cinematik.R;
import com.example.android.cinematik.pojos.CastMember;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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

        Transformation transformation = new CropCircleTransformation();

        Picasso.with(this.context)
                .load(castList.get(position).getCastProfile())
                .transform(transformation)
                .resize(200,280)
                .centerCrop()
                .into(holder.castProfilePath);

        holder.castActorName.setText(castList.get(position).getCastActorName());
        holder.castCharacterName.setText(castList.get(position).getCastCharName());
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        }
        return 0;
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {

        private ImageView castProfilePath;
        private TextView castActorName;
        private TextView castCharacterName;

        public CastViewHolder(View itemView) {
            super(itemView);

            castProfilePath = itemView.findViewById(R.id.imageViewProfile);
            castActorName = itemView.findViewById(R.id.actor_name);
            castCharacterName = itemView.findViewById(R.id.character_name);
        }
    }

    public void addList(List<CastMember> castMembers) {
        this.castList = castMembers;
        notifyDataSetChanged();
    }
}
