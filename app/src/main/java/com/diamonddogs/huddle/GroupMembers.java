package com.diamonddogs.huddle;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupMembers} factory method to
 * create an instance of this fragment.
 */
public class GroupMembers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private List<String> groupMembers;
    private List<String> groupMembersId;
    private HashMap<String,String> userData;

    public GroupMembers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupMembers.
     */
    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupMembers = new ArrayList<>();
        groupMembersId = new ArrayList<>();

        View View = inflater.inflate(R.layout.fragment_group_members, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("groups").document(getArguments().getString("docId"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        List Members = (List) document.getData().get("groupmembers");
                        for (Object member: Members) {
                            if (member != null){
                                userData = new HashMap<>();
                                db.collection("users").whereEqualTo("uID",member)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        groupMembers.add(document.getData().get("Name").toString());
                                                        groupMembersId.add(member.toString());
                                                        RecyclerView recyclerView = View.findViewById(R.id.group_members_recyclerview);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                        recyclerView.setAdapter(new RecyclerViewAdapter(groupMembers,groupMembersId));
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents", task.getException());
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Do nothing");
                                            }
                                        });
                                System.out.println(document.getData().get("groupmembers"));} }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        // Inflate the layout for this fragment
        return View;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
//        private CardView myCardView;
        private TextView myTextView;
        private Button addFriend;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.card_view_users, container, false));
//            myCardView = itemView.findViewById(R.id.card_container);
            myTextView = itemView.findViewById(R.id.userNameCardView);
            addFriend = itemView.findViewById(R.id.addFriendButton);
        }
    }
    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        private List<String> singleMember;
        private List<String> singleMemberId;

        public RecyclerViewAdapter(List<String> groupmembers,List<String> groupMembersId) {
            this.singleMember = groupmembers;
            this.singleMemberId = groupMembersId;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
            {
                holder.myTextView.setText(singleMember.get(position));
                if (singleMemberId.get(position).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    holder.addFriend.setVisibility(View.INVISIBLE);
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> friendsList = (List<String>) documentSnapshot.getData().get("Friends");
                        if (friendsList != null){
                            if (friendsList.contains(singleMemberId.get(position))){
                                holder.addFriend.setText("Friends");
                            }
                        }
                    }
                });

                holder.addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userData = new HashMap<>();
                        userData.put(singleMemberId.get(position),"");
                        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().toString());
                        Task<Void> friendReqRef = db.collection("FriendRequests").document(singleMemberId.get(position))
                                .set(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                DocumentReference friendReqRef = db.collection("FriendRequests").document(singleMemberId.get(position));
                                friendReqRef.update(singleMemberId.get(position),FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                            }
                        });
//                        docRef.update("userFriends", FieldValue.arrayUnion(singleMemberId.get(position)));
//                        DocumentReference docRef2 = db.collection("users").document(singleMemberId.get(position));
//                        docRef.update("userFriends", FieldValue.arrayUnion(FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().toString())));
                        Toast.makeText(getContext(), "Friend Request Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return singleMember.size();
        }
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    }
}