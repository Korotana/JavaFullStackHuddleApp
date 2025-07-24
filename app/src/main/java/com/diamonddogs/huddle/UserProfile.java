package com.diamonddogs.huddle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {

    private FirebaseAuth fa;
    private FirebaseFirestore db;
    private String userID;
    private HashMap<String, Object> userProfile;
    private DocumentReference docRef;

    /**
     * constructor, initializes profile in database with user ID
     */
    public UserProfile(){
        fa = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = fa.getCurrentUser().getUid();
        docRef = db.collection("users").document(userID);
        userProfile = new HashMap<String, Object>();
        userProfile.put("uID", userID);
    }

    /**
     * method to update the profile field "Name" stored in database
     * @param name : The name of the user
     */
    public void setName(String name) {
        userProfile.put("Name", name);
        docRef.set(userProfile);
    }

    /**
     * method to update the profile field "Tag" stored in database
     * @param tag : The tag chosen by the user
     */
    public void setTag(String tag) {
        userProfile.put("Tag", tag);
        docRef.set(userProfile);
    }

    /**
     * method to update the profile field "Phone Number" stored in database
     * @param phoneNumber: The phone number entered by the user
     */
    public void setPhoneNumber(String phoneNumber) {
        userProfile.put("Phone Number", phoneNumber);
        docRef.set(userProfile);
    }

    /**
     * method to update the profile field "Age" stored in database
     * @param Age : The age of the user
     */
    public void setAge(String Age) {
        userProfile.put("Age", Age);
        docRef.set(userProfile);
    }

    /**
     * gets String mapped to "Name" in saved profile
     * @return the name of the user
     */
    public String getName() {
        return userProfile.get("Name").toString();
    }

    /**
     * gets String mapped to "Tag" in saved profile
     * @return the tag chosen by the user
     */
    public String getTag() {
        return userProfile.get("Tag").toString();
    }

    /**
     * gets String mapped to "Phone Number" in saved profile
     * @return the tag chosen by the user
     */
    public String getPhoneNumber() {
        return userProfile.get("Phone Number").toString();
    }

    /**
     * gets String mapped to "Age" in saved profile
     * @return the age of the user
     */
    public String getAge() {
        return userProfile.get("Age").toString();
    }

    /**
     * gets document reference to profile document stored in database
     * @return document reference to profile document stored in database
     */
    public DocumentReference getDocRef() {
        return docRef;
    }


    /**
     * if there is a document associated with user ID, this method should retrieve it from
     * FireBase database.
     * @postcondition User profile is updated to data retrieved from database.
     */
    public void RetrieveProfile() {
        DocumentReference profileRef = db.collection("users").document(userID);
        profileRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> docMap = documentSnapshot.getData();
            userProfile.put("Name", docMap.get("Name").toString());
            userProfile.put("uID", docMap.get("uID").toString());
            userProfile.put("Tag", docMap.get("Tag").toString());
            userProfile.put("Phone Number", docMap.get("Phone Number").toString());
            userProfile.put("Age", docMap.get("Age").toString());
        });
    }
}
