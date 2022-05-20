package com.nqc.nqccuoiki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditSongActivity extends AppCompatActivity {
    private EditText name_song_edit,singer_edit;
    private ImageView image,delete;
    private DatabaseReference mDatabase;
    DatabaseReference songref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);
        Anhxa();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        name_song_edit.setText(intent.getStringExtra("namesong"));
        singer_edit.setText(intent.getStringExtra("singer"));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        songref = mDatabase.child("Song").child(id);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songref.child("singer").setValue(singer_edit.getText().toString());
                songref.child("song").setValue(name_song_edit.getText().toString());
                startActivity(new Intent(EditSongActivity.this,MainActivity.class));
            }
        });

    }

    private void Anhxa() {
        image = findViewById(R.id.edit);
        name_song_edit = findViewById(R.id.name_song_edit);
        singer_edit = findViewById(R.id.singer_edit);
    }
}