package com.nqc.nqccuoiki;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdepter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Song> list;

    public SongAdepter(Context context, int layout, List<Song> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = layoutInflater.inflate(layout,null);
        ImageView imageView = convertView.findViewById(R.id.image_list);
        TextView songname = convertView.findViewById(R.id.name_list);
        TextView singer = convertView.findViewById(R.id.singer_list);

        Song song = list.get(position);

        Picasso.get().load(song.getImage()).into(imageView);
        songname.setText(song.getSong());
        singer.setText(song.getSinger());

        return convertView;
    }
}
