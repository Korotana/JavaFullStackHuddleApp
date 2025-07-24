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
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendRequests} factory method to
 * create an instance of this fragment.
 */
public class FriendRequests extends Fragment {

    private List<String> requestSenderNames;
    private List<String> friendRequestsId;

    public FriendRequests() {
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
        friendRequestsId = new ArrayList<>();
        requestSenderNames = new ArrayList<>();
        View View = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("FriendRequests").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        List Requests = (List) document.getData().get(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        for (Object request : Requests) {
                            db.collection("users").whereEqualTo("uID", request)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    requestSenderNames.add(document.getData().get("Name").toString());
                                                    friendRequestsId.add(document.getData().get("uID").toString());
                                                    RecyclerView recyclerView = View.findViewById(R.id.friendRequestRecyclerView);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                    recyclerView.setAdapter(new FriendRequests.RecyclerViewAdapter(requestSenderNames,friendRequestsId));
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
                        Toast.makeText(getContext(), "No Requests at the Moment", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No such document");
                    }
                }
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
        private Button rejectFriend;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.card_view_friendrequests, container, false));
            myCardView = itemView.findViewById(R.id.card_container);
            friendRequest = itemView.findViewById(R.id.userNameFriendRequestCardView);
            acceptFriend = itemView.findViewById(R.id.acceptFriendRequest);
            rejectFriend = itemView.findViewById(R.id.friendRequestRejectButton);
        }
    }
    private class RecyclerViewAdapter extends RecyclerView.Adapter<FriendRequests.RecyclerViewHolder> {
        private List<String> singleRequest;
        private List<String> requestId;

        public RecyclerViewAdapter(List<String> friendRequests, List<String> friendRequestId) {
            this.singleRequest = friendRequests;
            this.requestId = friendRequestId;
        }

        @NonNull
        @Override
        public FriendRequests.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new FriendRequests.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendRequests.RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
            {
                holder.friendRequest.setText(singleRequest.get(position));
                holder.acceptFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        HashMap<String,String> userData = new HashMap<>();
//                        userData.put("Friends",requestId.get(position));
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        docRef.update("Friends",FieldValue.arrayUnion(requestId.get(position)));
                        friendRequestsId.remove(requestId.get(position));
                        DocumentReference friendReqRef = db.collection("FriendRequests").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        friendReqRef.update(FirebaseAuth.getInstance().getCurrentUser().getUid(),friendRequestsId);
                        Toast.makeText(getContext(), singleRequest.get(position) +" added to your Friend List", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_friendRequestsPage_self,getArguments());
                    }
                });
                holder.rejectFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        friendRequestsId.remove(requestId.get(position));
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference friendReqRef = db.collection("FriendRequests").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        friendReqRef.update(FirebaseAuth.getInstance().getCurrentUser().getUid(),friendRequestsId);
                        Navigation.findNavController(view).navigate(R.id.action_friendRequestsPage_self,getArguments());
                    }
                });
            }
            }

        @Override
        public int getItemCount() {
            return singleRequest.size();
        }
    }
}