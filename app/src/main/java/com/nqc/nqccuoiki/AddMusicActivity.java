package com.nqc.nqccuoiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddMusicActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    DatabaseReference mDataReference;

    private ImageView choose_media,add;
    private TextView media_name;
    private EditText namesong,singer;

    int REQUEST_CODE_MEDIA = 1;

    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mDataReference = FirebaseDatabase.getInstance().getReference();

        Anhxa();

        event();
    }

    private void event() {
        choose_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Audio "), REQUEST_CODE_MEDIA);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_MEDIA) {

                filePath = data.getData();
            } else if (requestCode == REQUEST_CODE_MEDIA) {
                //TODO: save the audio in the db
            }
            uploadData();
        }
    }
    private void uploadData() {
        if(filePath != null){
            StorageReference ref = storageReference.child("audios/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                    Log.d("AAA",uri+"");
//
//
//                                    String keyID = mDataReference.push().getKey();
//                                    Song song = new Song(R.drawable.logo,namesong.getText().toString(),singer.getText().toString(),uri+"",276000,keyID);
//                                    song.setKey(keyID);
//                                    mDataReference.child("Song").child(keyID).setValue(song, new DatabaseReference.CompletionListener() {
//                                        @Override
//                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                                        }
//                                    });
                                }
                            });
                        }
                    });

        }else {

        }

    }

    private void Anhxa() {
        choose_media = findViewById(R.id.choose_media);
        add = findViewById(R.id.add);
        media_name = findViewById(R.id.media_name);
        namesong = findViewById(R.id.name_song_add);
        singer = findViewById(R.id.singer_edit);
    }
}