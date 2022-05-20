package com.nqc.nqccuoiki.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nqc.nqccuoiki.EditSongActivity;
import com.nqc.nqccuoiki.MainActivity;
import com.nqc.nqccuoiki.MediaService;
import com.nqc.nqccuoiki.MusicConstants;
import com.nqc.nqccuoiki.R;
import com.nqc.nqccuoiki.Song;
import com.nqc.nqccuoiki.SongAdepter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListMusicFragment extends Fragment {

    private ListView lv_list;
    private ArrayList<Song> onlsong;
    private SongAdepter adepter;

    private View view;
    private DatabaseReference mDatabase;
    DatabaseReference songref;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "songs";

    // TODO: Rename and change types of parameters

    public ListMusicFragment() {
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
            onlsong = (ArrayList<Song>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_music, container, false);
        Anhxa();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ArrayList<Song> songs = new ArrayList<>();

        adepter = new SongAdepter(getActivity(),R.layout.item_song,songs);
        lv_list.setAdapter(adepter);
        mDatabase.child("Song").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("TAG", "homesong add ");
                Song song = snapshot.getValue(Song.class);
                song.setKey(snapshot.getKey());
                songs.add(song);
                adepter.notifyDataSetChanged();

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
        ItemClick();

        return view;
    }

    private void ItemClick() {
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "onItemClick: ");
                Intent intent = new Intent(getActivity(), MediaService.class);
                intent.putExtra("song", onlsong.get(position));
                intent.putExtra("songs", onlsong);
                intent.setAction(MusicConstants.ACTION.START_ACTION);
                getActivity().startService(intent);
                Log.d("TAG", "onItemClick: finish");

            }
        });
        lv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Song song = onlsong.get(position);
                String idm = song.getKey();
                System.out.println(idm);
                PopupMenu popupMenu = new PopupMenu(getActivity(),view);
                popupMenu.getMenuInflater().inflate(R.menu.edit_delete, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                Intent intent = new Intent(getContext(), EditSongActivity.class);
                                intent.putExtra("id",idm);
                                intent.putExtra("songname",song.getSong());
                                intent.putExtra("singer",song.getSinger());
                                startActivity(intent);
                                break;
                            case R.id.delete:
                                songref = mDatabase.child("Song").child(idm);
                                songref.removeValue();
                                startActivity(new Intent(getContext(), MainActivity.class));
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    private void Anhxa() {
        lv_list = view.findViewById(R.id.lv_music);

    }

}