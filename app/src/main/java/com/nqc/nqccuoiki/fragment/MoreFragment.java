package com.nqc.nqccuoiki.fragment;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nqc.nqccuoiki.AudioActivity;
import com.nqc.nqccuoiki.EditSongActivity;
import com.nqc.nqccuoiki.R;
import com.nqc.nqccuoiki.Song;
import com.nqc.nqccuoiki.SongAdepter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    private ListView lv_list;
    private ArrayList<Song> songs;
    private SongAdepter adepter;

    private View view;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "songs";

    // TODO: Rename and change types of parameters

    public MoreFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param Parameter 1.
     * @return A new instance of fragment ListMusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListMusicFragment newInstance(ArrayList<Song> param) {
        ListMusicFragment fragment = new ListMusicFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songs = (ArrayList<Song>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_music, container, false);
        Anhxa();
        adepter = new SongAdepter(getActivity(),R.layout.item_song,songs);
        lv_list.setAdapter(adepter);
        ItemClick();

        return view;
    }

    private void ItemClick() {
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AudioActivity.class);
                intent.putExtra("song", songs.get(position));
                intent.putExtra("position", position);
                intent.putExtra("songs", songs);
                startActivity(intent);

            }
        });
        lv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songs.get(position);
                String idm = song.getKey();
                System.out.println(idm);
                Intent intent = new Intent(getContext(), EditSongActivity.class);
                intent.putExtra("id",idm);
                intent.putExtra("songname",song.getSong());
                intent.putExtra("singer",song.getSinger());
                startActivity(intent);
                return true;
            }
        });
    }

    private void Anhxa() {
        lv_list = view.findViewById(R.id.lv_music);

    }

}