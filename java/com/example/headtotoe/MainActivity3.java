package com.example.headtotoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity3 extends AppCompatActivity {

    Button button;

    String playerName = "";
    String roomName = "";
    String role = "";
    String message = "";

    FirebaseDatabase database;
    DatabaseReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        button = findViewById(R.id.button);
        button.setEnabled(false);

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            roomName = extras.getString("roomName");
            if (roomName.equals(playerName)){
                role = "host";
            }else {
                role = "guest";
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                message = role + ":Poked!";
                messageRef.setValue(message);
            }
        });
        messageRef = database.getReference("rooms/" + roomName + "/message");
        message = role + ":Poked!";
        messageRef.setValue(message);
        addRoomEventListener();
    }
    private void addRoomEventListener(){
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (role.equals("host")){
                    if (dataSnapshot.getValue(String.class).contains("guest:")){
                        button.setEnabled(true);
                        Toast.makeText(MainActivity3.this, "" + dataSnapshot.getValue(String.class).replace("guest:", ""),Toast.LENGTH_SHORT ).show();
                    } else {
                        if (dataSnapshot.getValue(String.class).contains("host:")) {
                            button.setEnabled(true);
                            Toast.makeText(MainActivity3.this, "" + dataSnapshot.getValue(String.class).replace("host :", ""), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageRef.setValue(message);
            }
        });
    }
}