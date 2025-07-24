package db;

import static android.content.ContentValues.TAG;

import static com.diamonddogs.huddle.LandingPage.distFrom;

import android.util.Log;

import androidx.annotation.NonNull;

import com.diamonddogs.huddle.OnCompleteCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Group Data
 */
public class GroupDAO {
    List<String> list;
    List<String> aboutgroup;
    List<String> minAge;
    List<String> maxAge;
    List<String> size;
    List<String> req;
    List<String> activities;
    List<String> docIds;
    List<LatLng> latLngList;
    GroupDataObject groupDataObject;


    /**
     * Get groups from database with no search parameters other than location
     * @param userLocation LatLng object with current user location
     * @param RADIUS Radius of search
     * @return GroupDataObject instance
     */
    public void getGroups(LatLng userLocation, int RADIUS, OnCompleteCallback onCompleteCallback){
        list = new ArrayList<>();
        aboutgroup = new ArrayList<>();
        minAge = new ArrayList<>();
        maxAge = new ArrayList<>();
        size = new ArrayList<>();
        req = new ArrayList<>();
        activities = new ArrayList<>();
        docIds = new ArrayList<>();
        latLngList = new ArrayList<>();
        double userLat = userLocation.latitude;
        double userLong = userLocation.longitude;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Task<QuerySnapshot> allgroups = db.collection("groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if(document.getData().get("location") != "TBD") {
                                    Map<String, Object> coords = (Map<String, Object>) document.getData().get("location");
                                    double lat = (Double) coords.get("Latitude");
                                    double lon = (Double) coords.get("Longitude");
                                    if(distFrom(userLat, userLong, lat, lon) < RADIUS) {
                                        latLngList.add(new LatLng(lat, lon));
                                        list.add(document.getData().get("groupName").toString());
                                        aboutgroup.add(document.getData().get("aboutGroup").toString());
                                        minAge.add(document.getData().get("minAge").toString());
                                        maxAge.add(document.getData().get("maxAge").toString());
                                        size.add(document.getData().get("groupSize").toString());
                                        req.add(document.getData().get("peopleRequired").toString());
                                        activities.add(document.getData().get("groupActivity").toString());
                                        docIds.add(document.getId());
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                    }
                                }
                            }
                            groupDataObject = new GroupDataObject(list, aboutgroup, size, req, activities,docIds, latLngList, minAge, maxAge);
                            onCompleteCallback.onComplete(true);
                        }else {
                            Log.d(TAG,"Error getting documents",task.getException());
                        }
                    }
                });
        /*
        while(true){
            if(!allgroups.isComplete()){

            }else{
                groupDataObject = new GroupDataObject(list, aboutgroup, size, req, activities, docIds, latLngList, minAge, maxAge);
                Log.d("dataObject", groupDataObject.aboutgroup.toString());
                //return groupDataObject;
            }
        }

         */
    }

    /**
     * Get groups from database with a single search string for group activity
     * @param userLocation LatLng object with users current location
     * @param RADIUS Search radius
     * @param search Search keyword string
     * @return GroupDataObject instance
     */
    public GroupDataObject getGroupsStringSearch(LatLng userLocation, int RADIUS, String search){
        list = new ArrayList<>();
        aboutgroup = new ArrayList<>();
        minAge = new ArrayList<>();
        maxAge = new ArrayList<>();
        size = new ArrayList<>();
        req = new ArrayList<>();
        activities = new ArrayList<>();
        docIds = new ArrayList<>();
        latLngList = new ArrayList<>();
        double userLat = userLocation.latitude;
        double userLong = userLocation.longitude;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> allgroups = db.collection("groups").whereEqualTo("groupActivityLowerCase", search.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if(document.getData().get("location") != "TBD") {
                                    Map<String, Object> coords = (Map<String, Object>) document.getData().get("location");
                                    double lat = (Double) coords.get("Latitude");
                                    double lon = (Double) coords.get("Longitude");
                                    if(distFrom(userLat, userLong, lat, lon) < RADIUS) {
                                        latLngList.add(new LatLng(lat, lon));
                                        list.add(document.getData().get("groupName").toString());
                                        aboutgroup.add(document.getData().get("aboutGroup").toString());
                                        minAge.add(document.getData().get("minAge").toString());
                                        maxAge.add(document.getData().get("maxAge").toString());
                                        size.add(document.getData().get("groupSize").toString());
                                        req.add(document.getData().get("peopleRequired").toString());
                                        activities.add(document.getData().get("groupActivity").toString());
                                        docIds.add(document.getId());
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                }
                            }
                        }else {
                            Log.d(TAG,"Error getting documents",task.getException());
                        }
                    }
                });

        while(true){
            if(!allgroups.isComplete()){
            }else{
                groupDataObject = new GroupDataObject(list, aboutgroup, size, req, activities, docIds, latLngList, minAge, maxAge);
                Log.d("dataObject", groupDataObject.aboutgroup.toString());
                return groupDataObject;
            }
        }
    }

    public GroupDataObject getGroupDataObject() {
        return groupDataObject;
    }
}
