package com.televiranga.spagreen.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.televiranga.spagreen.DetailsActivity;
import com.televiranga.spagreen.R;
import com.televiranga.spagreen.models.CommonModels;
import com.televiranga.spagreen.utils.ItemAnimation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LiveTvHomeAdapter extends RecyclerView.Adapter<LiveTvHomeAdapter.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;
    private String fromActivity;

    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;

    public LiveTvHomeAdapter(Context context, List<CommonModels> items, String fromActivity) {
        this.items = items;
        ctx = context;
        this.fromActivity = fromActivity;
    }


    @Override
    public LiveTvHomeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LiveTvHomeAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_live_tv_home, parent, false);
        vh = new LiveTvHomeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LiveTvHomeAdapter.OriginalViewHolder holder, final int position) {

        final CommonModels obj = items.get(position);

        holder.name.setText(obj.getTitle());
        Picasso.get().load(obj.getImageUrl()).into(holder.image);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ctx, DetailsActivity.class);
                intent.putExtra("vType",obj.getVideoType());
                intent.putExtra("id",obj.getId());
                if (fromActivity.equals(DetailsActivity.TAG)) {
                    boolean castSession = ((DetailsActivity)ctx).getCastSession();
                    //Toast.makeText(ctx, "castSession in"+castSession, Toast.LENGTH_SHORT).show();
                    intent.putExtra("castSession", castSession);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(intent);
            }
        });

        setAnimation(holder.itemView, position);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
        public View lyt_parent;


        public OriginalViewHolder(View v) {
            super(v);
            image =v.findViewById(R.id.image);
            name =  v.findViewById(R.id.name);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });



        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}