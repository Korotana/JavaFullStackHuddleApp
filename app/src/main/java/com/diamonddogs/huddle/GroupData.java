package com.diamonddogs.huddle;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupData} factory method to
 * create an instance of this fragment.
 */
public class GroupData extends Fragment implements OnMapReadyCallback {

    private TextView name;
    private TextView about;
    private TextView ageRange;
    private TextView activity;
    private TextView groupMembers;
    private Button joinGroup;
    private MapView mMapView;
    private GoogleMap mMap;
    private View rootView;
    private LatLng latLng;
    private String member;
    private boolean isMember;

    public GroupData() {
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

        isMember = getArguments().getBoolean("alreadyMember");
        rootView = inflater.inflate(R.layout.fragment_group__data, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
//        initMap();
        mMapView.onResume();

        name = rootView.findViewById(R.id.group_data_name);
        about = rootView.findViewById(R.id.group_data_about);
        ageRange = rootView.findViewById(R.id.group_data_age_range);
        activity = rootView.findViewById(R.id.group_data_activity);
        name.setText(getArguments().getString("name"));
        about.setText(about.getText().toString() + " " + getArguments().getString("about"));
        ageRange.setText(ageRange.getText().toString() + getArguments().getString("minAge") + " - " + getArguments().getString("maxAge"));
        activity.setText(activity.getText().toString()+ " " + getArguments().getString("activities"));
        groupMembers = rootView.findViewById(R.id.group_data_group_members);
        groupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_group_Data_to_groupMembers,getArguments());
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.latLng = getArguments().getParcelable("latlng");
        joinGroup = view.findViewById(R.id.group_data_join_button);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupsRef = db.collection("groups");

//        List<DocumentSnapshot> x = groupsRef.get().getResult().getDocuments();
        groupsRef.whereArrayContains("groupmembers",FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.getId().equals(getArguments().getString("docId")))  {
                                joinGroup.setVisibility(View.INVISIBLE);
                            }
                        }

                    }
                });

        if (getArguments().getBoolean("alreadyMember")){
            joinGroup.setVisibility(view.INVISIBLE);
        }
        joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("groups").document(getArguments().getString("docId"));
                int peopleRequired = Integer.parseInt(getArguments().getString("peoplerequired")) - 1;
                if (peopleRequired < 0){
                    Toast.makeText(getContext(), "No More people Required By this Group", Toast.LENGTH_SHORT).show();
                }else {
                    docRef.update("groupmembers", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                    docRef.update("peopleRequired", Integer.toString(peopleRequired));
//                ApiFuture<WriteResult> arrayUnion = docRef.update("groupmembers", FieldValue.arrayUnion(""));
                    ((MainActivity)getActivity()).toolbar.setTitle("My Groups");
                    Navigation.findNavController(view).navigate(R.id.action_group_Data_to_user_groups2);
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.group_data_menu,menu);
        MenuItem updateGroup = menu.findItem(R.id.group_data_menu_update);

        updateGroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Bundle bundle = new Bundle();
                bundle.putString("name",getArguments().getString("name"));
                bundle.putString("size",getArguments().getString("size"));
                bundle.putString("peoplerequired",getArguments().getString("peoplerequired"));
                bundle.putString("about",getArguments().getString("about"));
                bundle.putString("activities",getArguments().getString("activities"));
                bundle.putString("docId",getArguments().getString("docId"));
                ((MainActivity)getActivity()).toolbar.setTitle("Update Group");
                Navigation.findNavController(rootView).navigate(R.id.action_group_Data_to_updateGroup,bundle);
                return true;
            }
        });


        MenuItem leaveGroup = menu.findItem(R.id.group_data_menu_leave_group);
        if (!isMember){
            leaveGroup.setVisible(false);
        }else {
            leaveGroup.setVisible(true);
            leaveGroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("groups").document(getArguments().getString("docId"));
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    ArrayList<String> groupmembers = (ArrayList<String>) document.getData().get("groupmembers");
                                    groupmembers.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    if (groupmembers.stream().allMatch( member -> member == null)){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Leave Group");
                                        builder.setMessage("Group will be Deleted If You Leave");
                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do nothing, but close the dialog
                                                docRef.delete();
                                                Toast.makeText(getContext(), "You Have Left " + getArguments().getString("name"), Toast.LENGTH_SHORT).show();
                                                Navigation.findNavController(rootView).navigate(R.id.action_group_Data_to_landingPage);

                                            }
                                        });
                                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Do nothing
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }else{
                                    groupmembers.add(null);
                                    docRef.update("groupmembers", groupmembers);
                                    int peopReq = Integer.parseInt(getArguments().getString("peoplerequired")) + 1;
                                    docRef.update("peopleRequired", Integer.toString(peopReq));
                                    Toast.makeText(getContext(), "You Have Left " + getArguments().getString("name"), Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(rootView).navigate(R.id.action_group_Data_to_landingPage);

                                        Log.d(TAG, "DocumentSnapshot data: ");}
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    return false;
                }
            });
        }
    }

        private void initMap(){
        if(mMap == null){
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setMap(mMap, latLng);
    }

    private void setMap(GoogleMap map, LatLng latLng){
        map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.zoomTo(11));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume", "Fragment Resumed");
        initMap();
    }
}

