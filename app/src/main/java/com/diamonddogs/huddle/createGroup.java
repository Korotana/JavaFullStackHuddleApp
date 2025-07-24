package com.diamonddogs.huddle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.type.LatLng;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.diamonddogs.huddle.createGroup#} factory method to
 * create an instance of this fragment.
 */
public class createGroup extends Fragment implements AdapterView.OnItemSelectedListener {

    private String activity_selected;
    private Button createGroup;
    private Button selectLocation;
    private EditText groupName;
    private EditText sizeOfGroup;
    private EditText peopleRequired;
    private EditText aboutGroup;
    private TextView aboutCharLength;
    private TextView nameCharLength;
    private EditText minAge;
    private EditText maxAge;
    private LatLng latLng;
    String groupMembers[];

    private HashMap<String,Object> groupProfileData;

    public createGroup() {
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
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            latLng = createGroupArgs.fromBundle(getArguments()).getGroupLocation();
        }
        Spinner spinner = view.findViewById(R.id.create_group_activity_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        groupName = (EditText) view.findViewById(R.id.create_group_name);
        sizeOfGroup = (EditText) view.findViewById(R.id.create_group_size);
        peopleRequired = (EditText) view.findViewById(R.id.create_group_people_required);
        aboutGroup = (EditText) view.findViewById(R.id.create_group_about);
        minAge = (EditText) view.findViewById(R.id.create_group_min_age);
        maxAge = (EditText) view.findViewById(R.id.create_group_max_age);
        createGroup = view.findViewById(R.id.create_group_profile);
        selectLocation = view.findViewById(R.id.selectLocationButton);
        aboutCharLength = view.findViewById(R.id.aboutCharLength);
        nameCharLength = view.findViewById(R.id.nameGroupCharLength);
        groupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = groupName.length();
                nameCharLength.setText(Integer.toString(length) + "/50");
            }
            @Override
            public void afterTextChanged(Editable editable) { }

        });

        aboutGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = aboutGroup.length();
                aboutCharLength.setText(Integer.toString(length) + "/250");
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latLng == null) {
                    Toast.makeText(getContext(), "Location Required", Toast.LENGTH_LONG).show();
                } else {

                    if (groupName.getText().toString().equals("") || aboutGroup.getText().toString().equals("") || sizeOfGroup.getText().toString().equals("") ||
                            peopleRequired.getText().toString().equals("") || minAge.getText().toString().equals("") || maxAge.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Cannot Leave a Field Empty", Toast.LENGTH_SHORT).show();
                    } else {

                    groupMembers = new String[Integer.parseInt(sizeOfGroup.getText().toString())];
                    groupProfileData = new HashMap<>();
                    HashMap<String, Double> location = new HashMap<>();
                    location.put("Latitude", latLng.latitude);
                    location.put("Longitude", latLng.longitude);
                    if (Integer.parseInt( sizeOfGroup.getText().toString()) <= 0 || Integer.parseInt(peopleRequired.getText().toString()) <= 0 ){
                        Toast.makeText(getContext(), "Size or People Required cannot be 0", Toast.LENGTH_SHORT).show();
                    }
                    else if (Integer.parseInt(peopleRequired.getText().toString()) >= Integer.parseInt(sizeOfGroup.getText().toString())){
                        Toast.makeText(getContext(), "People Required cannot be greater than Size", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    groupProfileData.put("groupName", groupName.getText().toString());
                    groupProfileData.put("groupSize", sizeOfGroup.getText().toString());
                    groupProfileData.put("peopleRequired", peopleRequired.getText().toString());
                    groupProfileData.put("aboutGroup", aboutGroup.getText().toString());
                    groupProfileData.put("groupActivity", activity_selected);
                    groupProfileData.put("groupActivityLowerCase", activity_selected.toLowerCase());
                    groupProfileData.put("minAge", minAge.getText().toString());
                    groupProfileData.put("maxAge", maxAge.getText().toString());
                    groupProfileData.put("timestamp", FieldValue.serverTimestamp());
                    groupProfileData.put("location", location);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    groupMembers[0] = userId;
                    groupProfileData.put("groupmembers", Arrays.asList(groupMembers));

                        db.collection("groups").add(groupProfileData).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Error writing Document", e);
                            }
                        });
                        Navigation.findNavController(view).navigate(R.id.action_create_group2_to_user_groups2);
                    }
                    }
                }
            }
        });
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle =  new Bundle();
                bundle.putInt("destination", 0);
                Navigation.findNavController(view).navigate(R.id.action_create_group2_to_createGroupSelectLocation, bundle);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.activity_selected = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        activity_selected = adapterView.getItemAtPosition(0).toString();
    }

}
