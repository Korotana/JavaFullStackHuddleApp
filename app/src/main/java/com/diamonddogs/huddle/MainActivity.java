package com.diamonddogs.huddle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Arrays;
import java.util.List;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    protected NavController navController;
    protected SharedPreferences preferences = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;
    NavigationView navigationView;
    double latitude;
    double longitude;
    Toolbar toolbar;

    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    int PERMISSION_ID = 44;

    private MenuItem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            createSignInIntent();
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        navController = navHostFragment.getNavController();

        // create preference file if it doesn't already exist
        if (preferences == null) {
            preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
            preferences.edit().putBoolean("first_run", true).apply();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar.setTitle("All groups");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_profile:
                        // if user has a profile in database, display it.
                        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    toolbar.setTitle("My Profile");
                                    drawer.closeDrawer(GravityCompat.START);
                                    navController.navigate(R.id.action_global_display_profile);
                                }else{
                                    toolbar.setTitle("Create Profile");
                                    drawer.closeDrawer(GravityCompat.START);
                                    navController.navigate(R.id.action_global_create_profile);
                                }
                            }
                        });
                        return true;

                    case R.id.nav_groups:
                        // User chose the "Settings" item, show the app settings UI...
                        //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
//                break;
                        toolbar.setTitle("My Groups");
                        drawer.closeDrawer(GravityCompat.START);
                        navController.navigate(MainNavDirections.actionGlobalUserGroups2());
                        return true;

                    case R.id.nav_logout:
                        signOut();
                        return true;
                    case R.id.nav_homepage:
                        toolbar.setTitle("All groups");
                        drawer.closeDrawer(GravityCompat.START);
                        navController.navigate(R.id.action_global_landingPage);
                        return true;

                    case R.id.friend_requests:
                        toolbar.setTitle("Friend Requests");
                        drawer.closeDrawer(GravityCompat.START);
                        navController.navigate(R.id.action_global_friendRequestsPage);
                        return true;

                    case R.id.myFriends:
                        toolbar.setTitle("My Friends");
                        drawer.closeDrawer(GravityCompat.START);
                        navController.navigate(R.id.friends);
                        return true;

                    case R.id.nav_message:
                        toolbar.setTitle("Group Chats");
                        drawer.closeDrawer(GravityCompat.START);
                        navController.navigate(R.id.action_global_chatUserGroups);
                        //Intent i = new Intent(MainActivity.this,ChatActivity.class);
                        //startActivity(i);
//                        Intent i = new Intent(MainActivity.this,ChatActivity.class);
//                        startActivity(i);
                        return true;

                    default:
                        // If we got here, the user's action was not recognized.
                        // Invoke the superclass to handle it.
                        return false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    /**
     * overriding onResume() method to do additional stuff on the first launch
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (preferences.getBoolean("first_run", true)) {
            // TODO: if anything should be done only the first time the app is launched add it here
            //
            //
            preferences.edit().putBoolean("first_run", false).apply();
        }
        if (checkPermissions()) {

        }
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    // Choose authentication providers
    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setIsSmartLockEnabled(true).build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // if user does not have a profile, send to create profile
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        TextView userName = findViewById(R.id.userName);
                        TextView userEmail = findViewById(R.id.userEmail);
                        userName.setText(mAuth.getCurrentUser().getDisplayName());
                        userEmail.setText(mAuth.getCurrentUser().getEmail());
                        if (!document.exists()) {
                            drawer.closeDrawer(GravityCompat.START);
                            navController.navigate(R.id.action_global_create_profile);
                        }
                    }
                }
            });
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            createSignInIntent();
        }
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        createSignInIntent();
                    }
                });
        // [END auth_fui_signout]
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(final OnCompleteCallback onCompleteCallback) {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        Log.d("Location", location.toString());
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            setLocation(location.getLatitude(), location.getLongitude());
                            onCompleteCallback.onComplete(true);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            setLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d("New Location", Double.toString(mLastLocation.getLatitude()));
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(new OnCompleteCallback() {
                    @Override
                    public void onComplete(boolean success) {
                    }
                });
            }
        }
    }

    public void setLocation(Double lat, Double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

}
