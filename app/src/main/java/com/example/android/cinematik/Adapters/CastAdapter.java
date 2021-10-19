package com.example.android.cinematik.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

@SuppressWarnings("ALL")
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private final static String TAG = CastAdapter.class.getSimpleName();
    private List<CastMember> castList = new ArrayList<>();
    Context context;
    public int id = 0;

    public CastAdapter(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .grid_item_cast, null);
        return new CastAdapter.CastViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {

        Transformation transformation = new CropCircleTransformation();

        String castProfilePic = castList.get(position).getCastProfile();
        String castActorName = castList.get(position).getCastActorName();
        String castCharName = castList.get(position).getCastCharName();

        Picasso.get()
                .load(castProfilePic)
                .transform(transformation)
                .resize(200,200)
                .centerCrop()
                .into(holder.castProfilePath);

        holder.castActorNameTV.setText(castActorName);
        holder.castCharacterNameTV.setText(castCharName);
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
        private TextView castActorNameTV;
        private TextView castCharacterNameTV;

        public CastViewHolder(View itemView) {
            super(itemView);

            castProfilePath = itemView.findViewById(R.id.imageViewProfile);
            castActorNameTV = itemView.findViewById(R.id.actor_name);
            castCharacterNameTV = itemView.findViewById(R.id.character_name);
        }
    }

    public void addMembers(List<CastMember> members) {
        this.castList = members;
        notifyDataSetChanged();
    }
}
