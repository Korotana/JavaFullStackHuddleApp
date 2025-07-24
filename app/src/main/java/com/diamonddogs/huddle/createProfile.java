package com.diamonddogs.huddle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class createProfile extends Fragment {

    Button createProfileInterests;
    EditText Name;
    EditText Age;
    EditText PhoneNumber;
    TextView radioGroupTagErrMsg;

    private FirebaseAuth fa;

    private RadioGroup radioGroupTags;
    private RadioButton radioButtonTag;

    UserProfile profile;

    public createProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profile = new UserProfile();
        fa = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Name = view.findViewById(R.id.edit_username);
        Age = view.findViewById(R.id.edit_age);
        PhoneNumber = view.findViewById(R.id.edit_phone_number);

        radioGroupTags = view.findViewById(R.id.radioGroupTags);
        radioGroupTagErrMsg = view.findViewById(R.id.textView_ChooseTag);

        createProfileInterests = view.findViewById(R.id.createProfile_next);
        createProfileInterests.setOnClickListener(view1 -> {

            // if any of the fields have not been filled...
            if (Name.getText().toString().matches("") ||
                    Age.getText().toString().matches("") ||
                    PhoneNumber.getText().toString().matches("") ||
                    radioGroupTags.getCheckedRadioButtonId() == -1) {
                if (TextUtils.isEmpty(Name.getText().toString())) {
                     Name.setError("Please enter a username.");
                }
                if (TextUtils.isEmpty(Age.getText().toString())) {
                    Age.setError("Please enter an age.");
                }
                if (TextUtils.isEmpty(PhoneNumber.getText().toString())) {
                    PhoneNumber.setError("Please enter a phone number.");
                }

                // This message doesn't get displayed, but an icon appears to show user what
                //  they need to select to continue
                }
                if (radioGroupTags.getCheckedRadioButtonId() == -1) {
                    radioGroupTagErrMsg.setError("Please select a tag");
                }
                
                
            else {
                // get chosen tag
                int selectedTag = radioGroupTags.getCheckedRadioButtonId();
                radioButtonTag = view.findViewById(selectedTag);

                // update profile with entered data

                profile.setName(Name.getText().toString());
                profile.setAge(Age.getText().toString());
                profile.setTag(radioButtonTag.getText().toString());
                profile.setPhoneNumber(PhoneNumber.getText().toString());

                Navigation.findNavController(view1).navigate(R.id.action_create_profile_to_create_profile_interests);
            }
        });
    }
}
