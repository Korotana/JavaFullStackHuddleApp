package db;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Object ofr storing group data in arrays
 */
public class GroupDataObject {
    List<String> name;
    List<String> aboutgroup;
    List<String> minAge;
    List<String> maxAge;
    List<String> size;
    List<String> req;
    List<String> activities;
    List<String> docIds;
    List<LatLng> latLngList;

    /**
     * Public constructor for group data object
     * @param name Array of names
     * @param aboutgroup Array of group info strings
     * @param size Array of group sizes
     * @param req Array of required people
     * @param activities Array of activities
     * @param docIds Array of document IDs
     * @param latLngList Array of LatLng objects for location
     */
    public GroupDataObject(List<String> name, List<String> aboutgroup, List<String> size, List<String> req, List<String> activities, List<String> docIds, List<LatLng> latLngList, List<String> minAge, List<String> maxAge){
        this.name = name;
        this.aboutgroup = aboutgroup;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.size = size;
        this.req = req;
        this.activities = activities;
        this.docIds = docIds;
        this.latLngList = latLngList;
    }


    /**
     * Get array of all arraylists containing strings
     * @return Array of Arraylists
     */
    public List<String>[] getStrings(){
        List<String>[] stringArrays = new List[8];
        stringArrays[0] = this.name;
        stringArrays[1] = this.aboutgroup;
        stringArrays[2] = this.size;
        stringArrays[3] = this.req;
        stringArrays[4] = this.activities;
        stringArrays[5] = this.docIds;
        stringArrays[6] = this.minAge;
        stringArrays[7] = this.maxAge;
        return stringArrays;
    }

    /**
     * Get arraylist containing LatLng objects
     * @return Arraylist containing LatLng objects
     */
    public List<LatLng> getLatLngList(){
        return this.latLngList;
    }
}
