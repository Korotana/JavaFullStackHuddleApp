package com.diamonddogs.huddle;

import android.content.Intent;
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

import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatRecyclerViewHolder> {



    class ChatRecyclerViewHolder extends RecyclerView.ViewHolder{
        private CardView myCardView;
        private TextView mName;
        public ChatRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public ChatRecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.group_char_cardview, container, false));
            myCardView = itemView.findViewById(R.id.group_chat_card);
            mName = itemView.findViewById(R.id.group_name);
        }
    }


    private List<String> groupId;
    private List<String> name;

    public ChatRecyclerViewAdapter(List<String> docId, List<String> name) {
        this.groupId = docId;
        this.name = name;
        Log.d("Recycler View", "Chat Recycler View");
    }

    @NonNull
    @Override
    public ChatRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ChatRecyclerViewAdapter.ChatRecyclerViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewHolder holder, int position) {
        holder.mName.setText(name.get(position));
        holder.myCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", groupId.get(holder.getAdapterPosition()));
                Navigation.findNavController(view).navigate(ChatUserGroupsDirections.actionChatUserGroupsToChatActivity(bundle));
            }
        });
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */

    @Override
    public int getItemCount() {
        return groupId.size();
    }





}

