package com.diamonddogs.huddle;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatUserGroups extends Fragment {
    private RecyclerView recyclerView;
    private View rootView;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;
    List<String> groupNames;
    List<String> groupIDs;

    public ChatUserGroups() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chat_user_groups, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerViewChat);
        groupIDs = new ArrayList<>();
        groupNames = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("ID", userId);
        Task<QuerySnapshot> allgroups = db.collection("groups").whereArrayContains("groupmembers",userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                groupNames.add(document.getData().get("groupName").toString());
                                groupIDs.add(document.getId());
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(new ChatRecyclerViewAdapter(groupIDs, groupNames));
                            }
                        }else {
                            Log.d(TAG,"Error getting documents",task.getException());
                        }
                    }
                });


        return rootView;
    }
}