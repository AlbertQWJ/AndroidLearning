package com.example.myapplicationexample;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicFragment extends Fragment implements View.OnClickListener{

    ImageView nextIv,playIv,lastIv,albumIv;
    TextView singerTv,songTv;
    RecyclerView musicRv;

    //Data source
    List<LocalMusicBean> mDatas;
    private LocalMusicAdapter adapter;

    //Record the position of the currently playing music
    int currnetPlayPosition = -1;
    //Record the position of the progress bar when music is paused
    int currentPausePositionInSong = 0;
    MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music,container,false);

        nextIv = view.findViewById(R.id.local_music_bottom_iv_next);
        playIv = view.findViewById(R.id.local_music_bottom_iv_play);
        lastIv = view.findViewById(R.id.local_music_bottom_iv_last);
        albumIv = view.findViewById(R.id.local_music_bottom_iv_icon);
        singerTv = view.findViewById(R.id.local_music_bottom_tv_singer);
        songTv = view.findViewById(R.id.local_music_bottom_tv_song);
        musicRv = view.findViewById(R.id.local_music_rv);
        nextIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
        playIv.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mDatas = new ArrayList<>();
//      Creating Adapter Objects
        adapter = new LocalMusicAdapter(getContext(), mDatas);
        musicRv.setAdapter(adapter);
//      Setting up the layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);
//      Loading local data sources
        loadLocalMusicData();
//      Set the click event for each item
        setEventListener();

        // Inflate the layout for this fragment
        return view;
    }

    private void setEventListener() {
        /* Set the click event for each item */
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currnetPlayPosition = position;
                LocalMusicBean musicBean = mDatas.get(position);
                playMusicInMusicBean(musicBean);
            }
        });
    }

    public void playMusicInMusicBean(LocalMusicBean musicBean) {
        /* Play music according to the incoming object*/
        // Set the artist name and song name to be displayed at the bottom
        singerTv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());
        stopMusic();
//                Reset Media Player
        mediaPlayer.reset();
//                Set a new play path
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            //String albumArt = musicBean.getAlbumArt();
//            Log.i("lsh123", "playMusicInMusicBean: albumpath=="+albumArt);
//            Bitmap bm = BitmapFactory.decodeFile(albumArt);
//            Log.i("lsh123", "playMusicInMusicBean: bm=="+bm);
            //albumIv.setImageBitmap(bm);
            playMusic();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Click the play button to play the music, or pause to play it again
     * There are two scenarios for playing music:
     * 1. from pause to play
     * 2. From stop to play
     * */
    private void playMusic() {
        /* Function to play music */
        if (mediaPlayer!=null&&!mediaPlayer.isPlaying()) {
            if (currentPausePositionInSong == 0) {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
//              From pause to play
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            playIv.setImageResource(R.drawable.ic_baseline_pause_24);
        }
    }
    private void pauseMusic() {
        /* function to pause the music */
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            playIv.setImageResource(R.drawable.ic_baseline_play_circle_24);
        }
    }
    private void stopMusic() {
        /* Function to stop the music */
        if (mediaPlayer!=null) {
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            playIv.setImageResource(R.drawable.ic_baseline_play_circle_24);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    private void loadLocalMusicData() {
        /* Load the music mp3 files from local storage into the collection */
//      1. Get the ContentResolver object
        ContentResolver resolver = getContext().getContentResolver();
//      2. Get the Uri address of local music storage
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//      3. Start search address
        Cursor cursor = resolver.query(uri, null, null, null, null);
//      4. Iterate through the Cursor
        int id = 0;
        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            id++;
            String sid = String.valueOf(id);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));
//          The main way to get album images is to query by album_id
            String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//            String albumArt = getAlbumArt(album_id);
//          Wrapping the data in a row into an object
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path);
            mDatas.add(bean);
        }
//      Data source changes, prompting adapter updates
        adapter.notifyDataSetChanged();
    }


    private String getAlbumArt(String album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = getContext().getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + album_id),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_music_bottom_iv_last:
                if (currnetPlayPosition ==0) {
                    Toast.makeText(getContext().getApplicationContext(),"It's already the first song, not the last one!",Toast.LENGTH_SHORT).show();
                    return;
                }
                currnetPlayPosition = currnetPlayPosition-1;
                LocalMusicBean lastBean = mDatas.get(currnetPlayPosition);
                playMusicInMusicBean(lastBean);
                break;
            case R.id.local_music_bottom_iv_next:
                if (currnetPlayPosition ==mDatas.size()-1) {
                    Toast.makeText(getContext().getApplicationContext(),"It's already the last song, there is no next song!",Toast.LENGTH_SHORT).show();
                    return;
                }
                currnetPlayPosition = currnetPlayPosition+1;
                LocalMusicBean nextBean = mDatas.get(currnetPlayPosition);
                playMusicInMusicBean(nextBean);
                break;
            case R.id.local_music_bottom_iv_play:
                if (currnetPlayPosition == -1) {
//                  There is no music selected to be played
                    Toast.makeText(getContext().getApplicationContext(),"Please select the music you want to play",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
//                  At this point in the play state, you need to pause the music
                    pauseMusic();
                }else {
//                  No music is playing at this time, click to start playing music
                    playMusic();
                }
                break;
        }
    }

}