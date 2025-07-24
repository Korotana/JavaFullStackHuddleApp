package com.diamonddogs.huddle;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{



    static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private CardView myCardView;
        private TextView myTextView;
        private TextView activity;
        private View view;
        private TextView peopReq;
        private TextView about;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.card_view_landing, container, false));
            myCardView = itemView.findViewById(R.id.card_container);
            myTextView = itemView.findViewById(R.id.userNameCardView);
            this.about = itemView.findViewById(R.id.userageCardView);
            this.activity = itemView.findViewById(R.id.activity_name_text_holder);
            this.peopReq = itemView.findViewById(R.id.size_text_holder);
        }
    }

    private List<String> myList;
    private List<String> aboutgroups;
    private List<String> minAge;
    private List<String> maxAge;
    private List<String> size;
    private List<String> peopReq;
    private List<String> activity;
    private List<String> docid;
    private List<LatLng> latLngList;
    private int actionID;

    public RecyclerViewAdapter(List<String> list, List<String> aboutgroups, List<String> size, List<String> req, List<String> docId, List<String> activity, List<String> minAge, List<String> maxAge, List<LatLng> latlng, int actionID) {
        myList = list;
        this.aboutgroups = aboutgroups;
        this.size = size;
        this.peopReq = req;
        this.activity = activity;
        this.docid = docId;
        this.actionID = actionID;
        this.latLngList = latlng;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RecyclerViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.myCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("name",myList.get(holder.getAdapterPosition()));
                bundle.putString("size",size.get(holder.getAdapterPosition()));
                bundle.putString("peoplerequired",peopReq.get(holder.getAdapterPosition()));
                bundle.putString("about",aboutgroups.get(holder.getAdapterPosition()));
                bundle.putString("activities",activity.get(holder.getAdapterPosition()));
                bundle.putString("docId",docid.get(holder.getAdapterPosition()));
                bundle.putString("minAge",minAge.get(holder.getAdapterPosition()));
                bundle.putString("maxAge",maxAge.get(holder.getAdapterPosition()));
                bundle.putParcelable("latlng", latLngList.get(holder.getAdapterPosition()));
                if(actionID == 2131296351){
                    bundle.putBoolean("alreadyMember", true);
                }
                Navigation.findNavController(view).navigate(actionID, bundle);
            }
        });

        holder.about.setText(aboutgroups.get(position));
        String groupNameAndAboutGroup = myList.get(position).toUpperCase();
        holder.myTextView.setText(groupNameAndAboutGroup);
        holder.peopReq.setText(peopReq.get(position));
        holder.activity.setText(activity.get(position));
        holder.myCardView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return myList.size();
    }


}
