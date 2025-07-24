package com.diamonddogs.huddle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class display_profile extends Fragment {

    TextView Name;
    TextView Age;
    TextView Tag;
    UserProfile profile;
    Button addFriend;

    public display_profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profile = new UserProfile();
        profile.RetrieveProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_display_profile, container, false);
        Handler handler = new Handler();
        // pause execution for a bit while profile is fetched from database
        handler.postDelayed(() -> {
            Name = rootView.findViewById(R.id.displayName);
            Name.setText(profile.getName());
            Age = rootView.findViewById(R.id.displayAge);
            Age.setText(profile.getAge());
            Tag = rootView.findViewById(R.id.displayTag);
            Tag.setText(profile.getTag());
        }, 300);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
