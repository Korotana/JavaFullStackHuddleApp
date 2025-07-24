package com.diamonddogs.huddle;

import junit.framework.TestCase;

import java.util.Map;

public class UserGroupTesting extends TestCase {

    UserProfile profile = new UserProfile();
    public void testSetName() {
        profile.setName("Test Name");
        profile.getDocRef().get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> docMap = documentSnapshot.getData();
            if (! docMap.get("Name").toString().equals("Test Name")) {
                System.out.println("Error in setName(): unexpected name found in database");
            }
        });
    }
    public void testSetTag() {
        profile.setTag("Test Tag");
        profile.getDocRef().get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> docMap = documentSnapshot.getData();
            if (! docMap.get("Tag").toString().equals("Test Tag")) {
                System.out.println("Error in setTag(): unexpected tag found in database");
            }
        });
    }
    public void testSetPhoneNumber() {
        profile.setPhoneNumber("Test PhoneNumber");
        profile.getDocRef().get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> docMap = documentSnapshot.getData();
            if (! docMap.get("Phone Number").toString().equals("Test PhoneNumber")) {
                System.out.println("Error in setPhoneNumber(): unexpected phone number found in database");
            }
        });
    }
    public void testSetAge() {
        profile.setAge("Test Age");
        profile.getDocRef().get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> docMap = documentSnapshot.getData();
            if (! docMap.get("Age").toString().equals("Test Age")) {
                System.out.println("Error in setAge(): unexpected age found in database");
            }
        });
    }
    public void testSetAgeRange() {
        profile.setAgeRange("Test Min Age", "Test Max Age");
        profile.getDocRef().get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> docMap = documentSnapshot.getData();
            if (!docMap.get("Min Age").toString().equals("Test Min Age")) {
                System.out.println("Error in setAgeRange(): unexpected min age found in database");
            }
            if (!docMap.get("Max Age").toString().equals("Test Max Age")) {
                System.out.println("Error in setAgeRange(): unexpected max age found in database");
            }
        });
    }
    public void testGetName() {
        if (!profile.getName().equals("Test Name")) {
            System.out.println("Error in getName(): unexpected name found in profile");
        }
    }
    public void testGetTag() {
        if (!profile.getName().equals("Test Tag")) {
            System.out.println("Error in getTag(): unexpected tag found in profile");
        }
    }
    public void testGetPhoneNumber() {
        if (!profile.getPhoneNumber().equals("Test PhoneNumber")) {
            System.out.println("Error in getPhoneNumber(): unexpected phone number found in profile");
        }
    }
    public void testGetAge() {
        if (!profile.getAge().equals("Test Age")) {
            System.out.println("Error in getAge(): unexpected name found in profile");
        }
    }
    public void testGetMaxAge() {
        if (!profile.getMaxAge().equals("Test Max Age")) {
            System.out.println("Error in getMaxAge(): unexpected max age found in profile");
        }
    }
    public void testGetMinAge() {
        if (!profile.getMinAge().equals("Test Min Age")) {
            System.out.println("Error in getMinAge(): unexpected min age found in profile");
        }
    }
    public void testRetrieveProfile() {
        UserProfile uProfile = new UserProfile();
        uProfile.RetrieveProfile();
        if (!uProfile.getName().equals("Test Name")) {
            System.out.println("Error in RetrieveProfile(): unexpected name found in profile");
        }
    }
}