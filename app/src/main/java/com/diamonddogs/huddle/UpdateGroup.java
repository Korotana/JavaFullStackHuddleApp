package com.diamonddogs.huddle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateGroup} factory method to
 * create an instance of this fragment.
 */
public class UpdateGroup extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String activity_selected;
    private Button updateGroup;
    private EditText Name;
    private EditText sizeOfGroup;
    private EditText peopleRequired;
    private EditText aboutGroup;
    private EditText minAge;
    private EditText maxAge;
    private String oldSizeofGroup;
    String groupMembers[];

    private HashMap<String,Object> updategroupProfileData;

    public UpdateGroup() {
//        Name = new EditText(getContext());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateGroup.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_group, container, false);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

//        System.out.println("ggbkjkjbkjbkjbk"+getArguments().getString("name"));
        this.setUpdateName(view,getArguments().getString("name"));

        this.setSizeOfGroup(view,getArguments().getString("size"));

        this.setPeopleRequired(view,getArguments().getString("peoplerequired"));

        this.setAboutGroup(view,getArguments().getString("about"));

        this.setMaxAge(view, getArguments().getString("maxAge"));

        this.setMinAge(view, getArguments().getString("minAge"));

        Spinner spinner = view.findViewById(R.id.update_group_activity_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),R.array.activities,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int valueSpinnerPosition = adapter.getPosition(getArguments().getString("activities"));
        spinner.setSelection(valueSpinnerPosition);
        spinner.setOnItemSelectedListener(this);

        super.onViewCreated(view,savedInstanceState);

        updateGroup = view.findViewById(R.id.update_group_profile);
        updateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Name.getText().toString().equals("")  || aboutGroup.getText().toString().equals("") || sizeOfGroup.getText().toString().equals("") ||
                        peopleRequired.getText().toString().equals("") || minAge.getText().toString().equals("") || maxAge.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Cannot Leave a Field Empty", Toast.LENGTH_SHORT).show(); }
                else{
                updategroupProfileData = new HashMap<>();
                updategroupProfileData.put("groupName",Name.getText().toString());
                if (Integer.parseInt(sizeOfGroup.getText().toString()) > Integer.parseInt(oldSizeofGroup)){
                    updategroupProfileData.put("groupSize",sizeOfGroup.getText().toString());
                }
                updategroupProfileData.put("peopleRequired",peopleRequired.getText().toString());
                updategroupProfileData.put("aboutGroup",aboutGroup.getText().toString());
                updategroupProfileData.put("groupActivity",activity_selected);
                updategroupProfileData.put("timestamp", FieldValue.serverTimestamp());
                updategroupProfileData.put("minAge", minAge.getText().toString());
                updategroupProfileData.put("maxAge", maxAge.getText().toString());


                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("groups").document(getArguments().getString("docId")).set(updategroupProfileData,SetOptions.merge());

                Navigation.findNavController(view).navigate(R.id.action_updateGroup_to_user_groups2);}
            }
        });
    }

    @Override
   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.activity_selected = adapterView.getItemAtPosition(i).toString();

   }
   @Override

   public void onNothingSelected(AdapterView<?> adapterView) {
        this.activity_selected = adapterView.getItemAtPosition(Integer.parseInt(getArguments().getString("activities"))).toString();
    }

    public EditText getUpdateName() {
        return Name;
    }

    public void setUpdateName(View view, String updateText) {
        Name = view.findViewById(R.id.update_group_name);
        Name.setText(updateText);
    }

    public EditText getSizeOfGroup() {
        return sizeOfGroup;
    }

    public void setSizeOfGroup(View view, String sizeToUpdate) {
        sizeOfGroup = view.findViewById(R.id.update_group_size);
        oldSizeofGroup = sizeToUpdate;
        sizeOfGroup.setText(sizeToUpdate);
    }

    public EditText getPeopleRequired() {
        return peopleRequired;
    }

    public void setPeopleRequired(View view, String peopleRequiredvalue) {
        peopleRequired = view.findViewById(R.id.update_group_people_required);
        peopleRequired.setText(peopleRequiredvalue);
    }

    public EditText getAboutGroup() {
        return aboutGroup;
    }

    public void setAboutGroup(View view, String aboutGroupValue) {
        this.aboutGroup = view.findViewById(R.id.update_group_about);
        aboutGroup.setText(aboutGroupValue);
    }

    public EditText getMinAge() {
        return minAge;
    }

    public void setMinAge(View view, String minAgeValue){
        this.minAge = view.findViewById(R.id.update_group_min_age);
        minAge.setText(minAgeValue);
    }

    public EditText getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(View view, String maxAgeValue){
        this.maxAge = view.findViewById(R.id.update_group_max_age);
        maxAge.setText(maxAgeValue);
    }
}
