package com.diamonddogs.huddle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class create_profile_interests extends Fragment {

    Button finishedProfileCreation;

    public create_profile_interests() {
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
        return inflater.inflate(R.layout.fragment_create_profile_interests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        finishedProfileCreation = view.findViewById(R.id.button_doneProfileInterests);
        // TODO: potentially allow interests to be chosen at profile creation

        finishedProfileCreation.setOnClickListener(new View.OnClickListener() {
            // update user profile, then return to landing page
            @Override
            public void onClick(View view) {

                // return to landing page
                Navigation.findNavController(view).navigate(R.id.action_create_profile_interests_to_landingPage);

            }
        });
    }
}
