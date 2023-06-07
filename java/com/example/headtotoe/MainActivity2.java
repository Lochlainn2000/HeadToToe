package com.example.headtotoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ListView listView;
    Button button;

    List<String> roomsList;

    String playerName = "";
    String roomName = "";
    FirebaseAuth auth;
    Button btnLogout;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference currentRoomRef;
    DatabaseReference allRoomsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.logout);
        user = auth.getCurrentUser();
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        roomName = playerName + "'s" + " room";

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.button);

        roomsList = new ArrayList<>();

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), signup.class);
            startActivity(intent);
            finish();
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setText("Creating Room!");
                button.setEnabled(false);
                roomName = playerName+ "'s" + " room";
                currentRoomRef = database.getReference("rooms/" + roomName + "/player1");
                addRoomEventListener();
                currentRoomRef.setValue(playerName);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomName = roomsList.get(position);
                currentRoomRef = database.getReference("rooms/" + roomName + "/player2");
                addRoomEventListener();
                currentRoomRef.setValue(playerName);
            }
        });

        addRoomsEventListener();
    }
    private void addRoomEventListener(){
        currentRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                button.setText("Create Room");
                button.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                button.setText("Create Room");
                button.setEnabled(true);
                Toast.makeText(MainActivity2.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsEventListener(){
        allRoomsRef = database.getReference("rooms");
        allRoomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomsList.clear();
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : rooms){
                    roomsList.add(snapshot.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error - nothing
                Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
            }
        });
    }
}