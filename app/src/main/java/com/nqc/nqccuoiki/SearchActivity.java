package com.nqc.nqccuoiki;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity{
    private EditText search;
    private DatabaseReference mDatabase;
    private ListView lv_list;
    private ArrayList<Song> songs;
    private SongAdepter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Anhxa();

    }

    private void Anhxa() {
        lv_list = findViewById(R.id.lv_music);
        songs =  new ArrayList<>();
        search = findViewById(R.id.edtTxtSearch);
        adapter = new SongAdepter(this,R.layout.item_song,songs);
        lv_list.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = String.valueOf(search.getText());
                mDatabase.child("Song").orderByChild("song").startAt(text).endAt(text+"\uf8ff").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Song song = snapshot.getValue(Song.class);
                        songs.add(song);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }



}