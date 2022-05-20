package com.nqc.nqccuoiki;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Database {
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public Database(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }
    //Songs

    public ArrayList<Song> getOnlineSongs(){
        ArrayList<Song> songs = new ArrayList<>();
        mDatabase.child("Song").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("TAG", "homesong add ");
                Song song = snapshot.getValue(Song.class);
                song.setKey(snapshot.getKey());
                songs.add(song);


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
        return songs;
    }

    public ArrayList<Song> getFavouriteSongs(){
        ArrayList<Song> favourite_songs = new ArrayList<>();
        mDatabase.child("favourite").child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("TAG", snapshot.getKey());
                mDatabase.child("Song").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Song song = snapshot.getValue(Song.class);
                        song.setfav(true);
                        song.setKey(snapshot.getKey());
                        favourite_songs.add(song);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
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
        return favourite_songs;
    }
    public void favourite(String keys, String act){
        if(act.equals("add")){
            mDatabase.child("favourite").child(firebaseUser.getUid()).child(keys).setValue(true);
        }
        else mDatabase.child("favourite").child(firebaseUser.getUid()).child(keys).removeValue();
    }
    //user
    public void createUser(User user, String uid){
        mDatabase.child("Account").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {Log.d("TAG", "check user ");
                if(snapshot.getValue() == null){
                    Log.d("TAG", "create user ");
                    mDatabase.child("account").child(uid).setValue(user);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setUser(){
        firebaseUser = firebaseAuth.getCurrentUser();
    }
    public FirebaseUser getUser(){
        return firebaseUser;
    }

}
