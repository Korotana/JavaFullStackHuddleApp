package com.diamonddogs.huddle;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Friends extends Fragment {

    List<String> Friends;
    List<String> FriendsId;
    RecyclerView recyclerView;

    public Friends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Friends.
     */
    // TODO: Rename and change types and number of parameters
    public static Friends newInstance(String param1, String param2) {
        Friends fragment = new Friends();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Friends = new ArrayList<>();
        View View = inflater.inflate(R.layout.fragment_friends, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (document.getData().get("Friends") != null){
                        FriendsId = (List) document.getData().get("Friends");
                        for (Object friend : FriendsId) {
                            db.collection("users").whereEqualTo("uID", friend)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Friends.add(document.getData().get("Name").toString());
//                                                    friendRequestsId.add(document.getData().get("uID").toString());
                                                    recyclerView = View.findViewById(R.id.friendRequestRecyclerView);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                    recyclerView.setAdapter(new Friends.RecyclerViewAdapter(Friends,FriendsId));
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
                            System.out.println(document.getData().get("groupmembers"));
                        }
                    } else {
                        Toast.makeText(getContext(), "No Friends at the Moment", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No such document");
                    }
                }}
                else{
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }});
        // Inflate the layout for this fragment
        return View;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CardView myCardView;
        private TextView friendRequest;
        private Button acceptFriend;
        private Button removeFriend;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.card_view_friendrequests, container, false));
            myCardView = itemView.findViewById(R.id.card_container);
            friendRequest = itemView.findViewById(R.id.userNameFriendRequestCardView);
            acceptFriend = itemView.findViewById(R.id.acceptFriendRequest);
            removeFriend = itemView.findViewById(R.id.friendRequestRejectButton);
        }
    }
    private class RecyclerViewAdapter extends RecyclerView.Adapter<Friends.RecyclerViewHolder> {
        private List<String> singleFriend;
        private List<String> friendId;

        public RecyclerViewAdapter(List<String> friendRequests, List<String> friendIds) {
            this.singleFriend = friendRequests;
            this.friendId = friendIds;
        }

        @NonNull
        @Override
        public Friends.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new Friends.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull Friends.RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
            {
                holder.friendRequest.setText(singleFriend.get(position));
                holder.acceptFriend.setVisibility(View.INVISIBLE);
                holder.removeFriend.setText("UnFriend");
                holder.removeFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                List<String> friendsList = (List<String>) documentSnapshot.getData().get("Friends");
                                if (friendsList != null) {
                                    friendsList.remove(friendId.remove(holder.getAdapterPosition()));
                                }
                                docRef.update("Friends",friendsList);
                                if (friendsList.size() == 0){
                                    Toast.makeText(getContext(), "No Friends", Toast.LENGTH_SHORT).show();
                                }
                                Navigation.findNavController(view).navigate(R.id.action_friends_self);
                            }
                        });
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return singleFriend.size();
        }
    }
}
