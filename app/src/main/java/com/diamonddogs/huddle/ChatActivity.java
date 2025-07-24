package com.diamonddogs.huddle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class ChatActivity extends AppCompatActivity {

    private ChatAdapter adapter;
    private FirebaseFirestore db;
    private String groupID;
    ArrayList<ChatMessage> messageArrayList;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        groupID = ChatActivityArgs.fromBundle(getIntent().getExtras()).getGroupId().getString("id");
        db = FirebaseFirestore.getInstance();
        messageArrayList = new ArrayList<>();
        Log.d("group", groupID);
        DocumentReference documentReference = db.collection("groups").document(groupID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        String name = document.get("groupName").toString();
                        getSupportActionBar().setTitle(name);
                    }
                }
            }
        });


        mRef = FirebaseDatabase.getInstance().getReference();
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().build(),
                    1
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            displayChatMessages();
        }

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);
                ChatMessage chatMessage = new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName(), groupID);
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                db.collection("message").document(groupID).collection("messages").add(chatMessage);
                initMessages();

                // Clear the input
                input.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();

                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    /**
     * Initial initialization of chat messages
     */
    private void initMessages(){
        Task<QuerySnapshot> groupMessages = db.collection("message").document(groupID).collection("messages").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        ChatMessage chatMessage = new ChatMessage((String) document.get("messageText"), (String) document.get("messageUser"), (String) document.get("groupId"), (Long) document.get("messageTime"));
                        ArrayList<ChatMessage> messagetoremove = new ArrayList<>();
                        for(ChatMessage message : messageArrayList){
                            if(message.getMessageTime() == chatMessage.getMessageTime()){
                                messagetoremove.add(message);
                            }
                        }
                        messageArrayList.removeAll(messagetoremove);
                        messageArrayList.add(chatMessage);
                    }
                    Collections.sort(messageArrayList, new ChatComparator());
                    displayChatMessages();
                }
            }
        });
    }

    /**
     * Display all chat messages for the group
     */
    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        ChatAdapter adapter = new ChatAdapter(this, messageArrayList);
        Collections.sort(messageArrayList, new ChatComparator());
        listOfMessages.setAdapter(adapter);
        Log.d("Messages", messageArrayList.toString());

    }

    /**
     * Database listener for updating messages
     */
    private void initListener(){
        db.collection("message").document(groupID).collection("messages").addSnapshotListener((value, error) -> {
            if(error != null || value != null){
                onSnapShotChanged(value);
            }
        });
    }

    private void onSnapShotChanged(QuerySnapshot value){
        for(DocumentChange doc : value.getDocumentChanges()){
            ChatMessage chatMessage = new ChatMessage((String) doc.getDocument().get("messageText"), (String) doc.getDocument().get("messageUser"), (String) doc.getDocument().get("groupId"), (Long) doc.getDocument().get("messageTime"));
            ArrayList<ChatMessage> messagetoremove = new ArrayList<>();
            for(ChatMessage message : messageArrayList){
                if(message.getMessageTime() == chatMessage.getMessageTime()){
                    messagetoremove.add(message);
                }
            }

            messageArrayList.removeAll(messagetoremove);
            messageArrayList.add(chatMessage);
            Collections.sort(messageArrayList, new ChatComparator());
            displayChatMessages();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMessages();
        initListener();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Custom array adapter for populating listview in chat activity
     */
    public class ChatAdapter extends ArrayAdapter<ChatMessage> {

        public ChatAdapter(@NonNull Context context, ArrayList<ChatMessage> messageArray) {
            super(context, 0, messageArray);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ChatMessage message = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
            }
            TextView name = convertView.findViewById(R.id.message_user);
            TextView time = convertView.findViewById(R.id.message_time);
            TextView messageText = convertView.findViewById(R.id.message_text);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd, HH:mm:ss");
            Date date = new Date(message.getMessageTime());
            time.setText(DateFormat.format("MM-dd-yyyy (HH:mm:ss)", date));
            name.setText(message.getMessageUser());

            messageText.setText(message.getMessageText());
            return convertView;
        }
    }

    /**
     * Custom comparator for sorting chat messages by date
     */
    private class ChatComparator implements Comparator<ChatMessage>{

        @Override
        public int compare(ChatMessage chatMessage, ChatMessage t1) {
            if(chatMessage.getMessageTime() == t1.getMessageTime()){
                return 0;
            }else if(chatMessage.getMessageTime() > t1.getMessageTime()){
                return 1;
            }else{
                return -1;
            }
        }
    }
}


