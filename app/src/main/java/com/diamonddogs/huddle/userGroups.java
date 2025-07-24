package com.diamonddogs.huddle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class userGroups extends Fragment {

    Button createGroups;

    List<String> list;
    List<String> aboutgroup;
    List<String> size;
    List<String> req;
    List<String> activities;
    List<String> docIds;
    List<String> minAge;
    List<String> maxAge;
    List<LatLng> latlng;
    View rootView;
    RecyclerView recyclerView;

    private static final String TAG = MainActivity.class.getName();

    public userGroups() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_groups, container, false);
        list = new ArrayList<>();
        aboutgroup = new ArrayList<>();
        size = new ArrayList<>();
        req = new ArrayList<>();
        activities = new ArrayList<>();
        docIds = new ArrayList<>();
        minAge = new ArrayList<>();
        maxAge = new ArrayList<>();
        latlng = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Task<QuerySnapshot> allgroups = db.collection("groups").whereArrayContains("groupmembers",userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                list.add(document.getData().get("groupName").toString());
                                aboutgroup.add(document.getData().get("aboutGroup").toString());
                                size.add(document.getData().get("groupSize").toString());
                                req.add(document.getData().get("peopleRequired").toString());
                                activities.add(document.getData().get("groupActivity").toString());
                                docIds.add(document.getId());
                                minAge.add(document.get("minAge").toString());
                                maxAge.add(document.get("maxAge").toString());
                                Map<String, Object> coords = (Map<String, Object>) document.getData().get("location");
                                double lat = (Double) coords.get("Latitude");
                                double lon = (Double) coords.get("Longitude");
                                latlng.add(new LatLng(lat, lon));
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                RecyclerView recyclerView = rootView.findViewById(R.id.user_groups_recyclerview);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                recyclerView.setAdapter(new RecyclerViewAdapter(list,aboutgroup,size,req,docIds,activities, minAge, maxAge, latlng, R.id.action_user_groups2_to_group_Data));
                            }
                        }else {
                            Log.d(TAG,"Error getting documents",task.getException());
                        }
                    }
                });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
    }

    private void search(String str){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(!str.equals("")) {
            List<String> list1 = new ArrayList<>();
            List<String> aboutgroup1 = new ArrayList<>();
            List<String> size1 = new ArrayList<>();
            List<String> req1 = new ArrayList<>();
            List<String> activities1 = new ArrayList<>();
            List<String> docIds1 = new ArrayList<>();
            List<String> maxAge1 = new ArrayList<>();
            List<String> minAge1 = new ArrayList<>();
            List<LatLng> latlng1 = new ArrayList<>();

            db.collection("groups").whereEqualTo("groupActivityLowerCase", str.toLowerCase()).whereArrayContains("groupmembers",userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    list1.add(document.getData().get("groupName").toString());
                                    aboutgroup1.add(document.getData().get("aboutGroup").toString());
                                    size1.add(document.getData().get("groupSize").toString());
                                    req1.add(document.getData().get("peopleRequired").toString());
                                    activities1.add(document.getData().get("groupActivity").toString());
                                    docIds1.add(document.getId());
                                    minAge1.add(document.get("minAge").toString());
                                    maxAge1.add(document.get("maxAge").toString());
                                    Map<String, Object> coords = (Map<String, Object>) document.getData().get("location");
                                    double lat = (Double) coords.get("Latitude");
                                    double lon = (Double) coords.get("Longitude");
                                    latlng1.add(new LatLng(lat, lon));
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    recyclerView = rootView.findViewById(R.id.user_groups_recyclerview);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerView.setAdapter(new RecyclerViewAdapter(list1, aboutgroup1, size1, req1, docIds1, activities1, minAge1, maxAge1, latlng1, R.id.action_user_groups2_to_group_Data));
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
        } else {
            recyclerView.setAdapter(new RecyclerViewAdapter(list,aboutgroup,size,req,docIds,activities, minAge, maxAge, latlng, R.id.action_user_groups2_to_group_Data));
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createGroups = view.findViewById(R.id.create_group_in_user_groups);

        createGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_user_groups2_to_create_group2);
            }
        });
    }



}