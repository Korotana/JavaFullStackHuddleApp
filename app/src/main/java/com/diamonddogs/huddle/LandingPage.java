package com.diamonddogs.huddle;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.maps.model.LatLng;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import db.GroupDAO;
import db.GroupDataObject;

public class LandingPage extends Fragment {

    double userLat;
    double userLong;
    private int RADIUS = 100;
    private FloatingActionButton fabMap;
    private FloatingActionButton fabUserLocation;
    private boolean isFabOpen;
    private LatLng selectedLocation;
    int resumed = 0;

    View rootView;
    RecyclerView recyclerView;


    public LandingPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static Fragment newInstance() {
        return new LandingPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);



        if(getArguments() != null && LandingPageArgs.fromBundle(getArguments()).getLatlng() != null){
            if(selectedLocation.latitude != 0.0) {
                Log.d("resume", "Saved State");
                selectedLocation = LandingPageArgs.fromBundle(getArguments()).getLatlng();
                userLat = selectedLocation.latitude;
                userLong = selectedLocation.longitude;
                populateNoSearch();
            }
        } else {
            Log.d("init", "Init recycler view");
            ((MainActivity)getActivity()).getLastLocation(new OnCompleteCallback() {
                @Override
                public void onComplete(boolean success) {
                    getUserLocation();

                    populateNoSearch();
                }
            });
            /*
            ProgressDialog nBar;
            nBar = new ProgressDialog(getContext());
            nBar.setMessage("Loading...");
            nBar.setIndeterminate(false);
            nBar.setCancelable(true);
            nBar.show();

             */
            /*
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 5 seconds
                    //nBar.dismiss();
                    //ProgressBar progressBar = rootView.findViewById(R.id.progress_loader);
                    //progressBar.setVisibility(View.INVISIBLE);
                    //progressBar.setElevation(-1);
                    //recyclerView = rootView.findViewById(R.id.recyclerview);
                    //recyclerView.setElevation(2);
                    getUserLocation();
                    populateNoSearch();
                }
            }, 4000);

             */
        }

        FloatingActionButton fabMain = rootView.findViewById(R.id.floatingActionButtonMain);
        isFabOpen = false;
        fabMap = rootView.findViewById(R.id.floatingActionButtonMap);
        fabUserLocation = rootView.findViewById(R.id.floatingActionButtonUserLoc);
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFabOpen){
                    ViewCompat.animate(fabMain).rotation(360f).withLayer().setDuration(1200).setInterpolator(new OvershootInterpolator(0.5f));
                    showFABMenu();
                }else{
                    ViewCompat.animate(fabMain).rotation(0f).withLayer().setDuration(1200).setInterpolator(new OvershootInterpolator(0.5f));
                    closeFABMenu();
                }
            }
        });

        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("destination", R.id.landingPage);
                onPause();
                Navigation.findNavController(view).navigate(R.id.action_landingPage_to_createGroupSelectLocation, bundle);
            }
        });

        fabUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation();
                populateNoSearch();
            }
        });
        return rootView;
    }


    /**
     * OPen Floating Action Button menu
     */
    private void showFABMenu(){
        isFabOpen = true;
        fabMap.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabMap.isClickable();
        fabUserLocation.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fabUserLocation.isClickable();
    }

    /**
     * Close Floating Action Button menu
     */
    private void closeFABMenu(){
        isFabOpen = false;
        fabMap.animate().translationY(0);
        fabUserLocation.animate().translationY(0);
    }

    /**
     * Calculate distance between two coordinate points
     * @param lat1 Location 1 latitude
     * @param lng1 Location 1 longitude
     * @param lat2 Location 2 latitude
     * @param lng2 Location 2 longitude
     * @return Distance in kilometers
     */
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        //Log.d("Distance", Double.toString(dist));
        return dist;
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        MenuItem radiusGroup = menu.findItem(R.id.radiusGroup);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.radius50:
                RADIUS = 5;
                Log.d("Radius", Integer.toString(RADIUS));
                populateNoSearch();
                return true;
            case R.id.radius100:
                RADIUS = 15;
                Log.d("Radius", Integer.toString(RADIUS));
                populateNoSearch();
                return true;
            case R.id.radius300:
                RADIUS = 100;
                Log.d("Radius", Integer.toString(RADIUS));
                populateNoSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Filter groups by activity by searching with string
     * @param str Search keyword string
     */
    private void search(String str) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!str.equals("")) {
            LatLng userlatlang = new LatLng(userLat, userLong);
            GroupDAO groupDAO = new GroupDAO();
            GroupDataObject groupDataObject = groupDAO.getGroupsStringSearch(userlatlang, RADIUS, str);
            List<String>[] stringList = groupDataObject.getStrings();
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new RecyclerViewAdapter(stringList[0],stringList[1],stringList[2],stringList[3],stringList[5],stringList[4],stringList[6],stringList[7], groupDataObject.getLatLngList(), R.id.action_landingPage_to_group_Data));

        } else{
            LatLng userlatlang = new LatLng(userLat, userLong);
            GroupDAO groupDAO = new GroupDAO();
            groupDAO.getGroups(userlatlang, RADIUS, new OnCompleteCallback() {
                @Override
                public void onComplete(boolean success) {
                    GroupDataObject groupDataObject = groupDAO.getGroupDataObject();
                    List<String>[] stringList = groupDataObject.getStrings();
                    RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new RecyclerViewAdapter(stringList[0],stringList[1],stringList[2],stringList[3],stringList[5],stringList[4],stringList[6],stringList[7], groupDataObject.getLatLngList(), R.id.action_landingPage_to_group_Data));
                    Log.d("init", "should be displaying");
                }
            });
        }
    }

    /**
     * Populate landing page with groups, no filtering other than radius
     */
    private void populateNoSearch(){
        Log.d("User Lat 1", Double.toString(userLat));
        Log.d("User Long 1", Double.toString(userLong));
        LatLng userlatlang = new LatLng(userLat, userLong);
        GroupDAO groupDAO = new GroupDAO();
        groupDAO.getGroups(userlatlang, RADIUS, new OnCompleteCallback() {
            @Override
            public void onComplete(boolean success) {
                GroupDataObject groupDataObject = groupDAO.getGroupDataObject();
                if(groupDataObject != null) {
                    List<String>[] stringList = groupDataObject.getStrings();
                    RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new RecyclerViewAdapter(stringList[0], stringList[1], stringList[2], stringList[3], stringList[5], stringList[4], stringList[6], stringList[7], groupDataObject.getLatLngList(), R.id.action_landingPage_to_group_Data));
                    Log.d("init", "should be displaying");
                }

            }
        });
    }

    /**
     * Get users current location
     */
    private void getUserLocation(){
        userLat = ((MainActivity)getActivity()).latitude;
        userLong = ((MainActivity)getActivity()).longitude;
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume", Integer.toString(resumed));
        if(isStateSaved()) {
            Log.d("resume", "Saved State");
            selectedLocation = getArguments().getParcelable("latlng");
            userLat = selectedLocation.latitude;
            userLong = selectedLocation.longitude;
            populateNoSearch();
        }
        populateNoSearch();
    }

     */

    @Override
    public void onPause() {
        super.onPause();
        resumed = 1;
        Log.d("pause", Integer.toString(resumed));
        Bundle bundle = new Bundle();
        bundle.putInt("resume", resumed);
        onSaveInstanceState(bundle);
    }
}
