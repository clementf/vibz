package com.vibz.vibz;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by nicolasszewe on 23/10/15.
 * Modified by hugo on 30/10/2015
 */
public class PlaylistActivity extends AppCompatActivity {
    SwipeListView swipelistview;
    SongAdapter adapter;
    int lastPosition;
    ArrayList<Song> itemSong;
    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    public static WifiP2pManager.Channel channel;
    private boolean isWifiP2pEnabled = false;
    private BroadcastReceiver onCompletionListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("The_best", "La musique est finie");
            songAdt.notifyDataSetChanged();
            addFirstSong();
            seek_bar.setMax((int) musicSrv.CurrentSong.get(0).getDuration());
        }

    };

    public static MusicService musicSrv;
    public static SongAdapter songAdt;
    private Intent musicServiceIntent;
    private ListView itemView;
    private boolean musicBound = false;
    public static SeekBar seek_bar;
    private BroadcastReceiver receiver = null;
    private Handler seekHandler = new Handler();
    private TextView song_progress_text;
    private TextView current_song_text;
    private final IntentFilter intentFilter = new IntentFilter();
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicSrv.setList(MusicService.CurrentSong);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    private BroadcastReceiver onNext = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUINext();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.menu);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);

        swipelistview = (SwipeListView) findViewById(R.id.playlist);
        itemSong = new ArrayList<Song>();
        adapter = new SongAdapter(this, R.layout.song, itemSong, swipelistview);

        //MainActivity.onConnectionInfoAvailable();

        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                ListView listView = (ListView) findViewById(R.id.playlist);
                View v = listView.getChildAt(position -
                        listView.getFirstVisiblePosition());
                Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                remove_addButton.setVisibility(View.VISIBLE);
                v.findViewById(R.id.layout_vote).setVisibility(View.VISIBLE);
                if (lastPosition != -1 && lastPosition != position) {
                    swipelistview.closeAnimate(lastPosition);
                    ListView listView2 = (ListView) findViewById(R.id.playlist);
                    View v2 = listView2.getChildAt(lastPosition -
                            listView2.getFirstVisiblePosition());
                    Button remove_addButton2 = (Button) v2.findViewById(R.id.button_add_remove);
                    remove_addButton2.setVisibility(View.GONE);
                    v2.findViewById(R.id.layout_vote).setVisibility(View.GONE);
                }
                lastPosition = position;
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
                ListView listView = (ListView) findViewById(R.id.playlist);
                View v = listView.getChildAt(position -
                        listView.getFirstVisiblePosition());
                Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                remove_addButton.setVisibility(View.GONE);
                v.findViewById(R.id.layout_vote).setVisibility(View.GONE);
                lastPosition = -1;
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
                ListView listView = (ListView) findViewById(R.id.playlist);
                View v = listView.getChildAt(position -
                        listView.getFirstVisiblePosition());
                Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                remove_addButton.setVisibility(View.VISIBLE);
                v.findViewById(R.id.layout_vote).setVisibility(View.VISIBLE);
                if (lastPosition != -1 && lastPosition != position) {
                    swipelistview.closeAnimate(lastPosition);
                    ListView listView2 = (ListView) findViewById(R.id.playlist);
                    View v2 = listView2.getChildAt(lastPosition -
                            listView2.getFirstVisiblePosition());
                    Button remove_addButton2 = (Button) v2.findViewById(R.id.button_add_remove);
                    remove_addButton2.setVisibility(View.GONE);
                    v2.findViewById(R.id.layout_vote).setVisibility(View.GONE);
                }
                swipelistview.openAnimate(position); //when you touch front view it will open
                lastPosition = position;
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
                ListView listView = (ListView) findViewById(R.id.playlist);
                View v = listView.getChildAt(position -
                        listView.getFirstVisiblePosition());
                Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                remove_addButton.setVisibility(View.GONE);
                v.findViewById(R.id.layout_vote).setVisibility(View.GONE);
                swipelistview.closeAnimate(position);//when you touch back view it will close
                lastPosition = -1;
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        swipelistview.setOffsetLeft(convertDpToPixel(420f)); // left side offset
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
        swipelistview.setAnimationTime(200); // animarion time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress
        swipelistview.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        if (musicServiceIntent == null) {
            musicServiceIntent = new Intent(this, MusicService.class);
            bindService(musicServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicServiceIntent);
        }

        ImageView firstcoverart = (ImageView) findViewById(R.id.coverart);
        Drawable myDrawable = this.getResources().getDrawable(R.drawable.ic_fond_welcome);
        firstcoverart.setImageBitmap(((BitmapDrawable) myDrawable).getBitmap());
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(onCompletionListener,
                new IntentFilter("musicCompletion"));

        LocalBroadcastManager.getInstance(this).registerReceiver(onNext,
                new IntentFilter("player.next"));

        addFirstSong();
        displayVibzMessages();
        refreshPlaylist();
        seekUpdation();

        TextView t = (TextView) findViewById(R.id.playlistName);
        t.setText(musicSrv.PlaylistName);


        seek_bar = (SeekBar) findViewById(R.id.musicProgress);
        song_progress_text = (TextView) findViewById(R.id.song_progress_text);
        current_song_text = (TextView) findViewById(R.id.current_song_text);
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int songProgress, boolean arg2) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MusicService.firstPlay) {
                    int songProgress = seekBar.getProgress();
                    musicSrv.playSongAgain(songProgress);
                    song_progress_text.setText(convertPositionString(songProgress));
                    current_song_text.setText(musicSrv.CurrentSong.get(0).getStringDuration());
                    ImageButton b = (ImageButton) findViewById(R.id.pause_play_song);
                    b.setImageResource(R.drawable.ic_action_pause);
                }
            }
        });

        if (musicSrv.CurrentSong.size() > 0) {
            seek_bar.setMax((int) musicSrv.CurrentSong.get(0).getDuration());
        } else {
            seek_bar.setMax(0);
        }
        seekHandler.postDelayed(run, 1000);
    }


    //All the method that are called on start
    public void seekUpdation() {
        if (MusicService.isPlaying == true) {
            seek_bar.setProgress(musicSrv.getPosn());
            song_progress_text.setText(convertPositionString(musicSrv.getPosn()));
            current_song_text.setText(musicSrv.CurrentSong.get(0).getStringDuration());
        }
        seekHandler.postDelayed(run, 1000);
        displayVibzMessages();
    }

    public void displayVibzMessages() {
        TextView menuTitle = (TextView) findViewById(R.id.menuTitle);
        if (musicSrv.CurrentSong.size() == 0) {
            menuTitle.setVisibility(View.VISIBLE);
            menuTitle.setText("Empty playlist ! Add a song to show your Vibz !");
            menuTitle.setTextSize(20);
        } else if (musicSrv.PlaylistSongs.size() == 0) {
            menuTitle.setVisibility(View.VISIBLE);
            menuTitle.setText("Come on ! No song left after that one ! Show your Vibz and add a new one !");
            menuTitle.setTextSize(20);
        } else {
            menuTitle.setText("");
            menuTitle.setVisibility(View.GONE);
        }
    }

    public void addFirstSong() {
        if (musicSrv.CurrentSong.size() > 0) {
//            Set player background to black
//            findViewById(R.id.fondPlayer).setBackgroundColor(Color.BLACK);
            String artist = musicSrv.CurrentSong.get(0).getArtist();
            TextView firstsongView = (TextView) findViewById(R.id.firstsong_title);
            TextView firstartistView = (TextView) findViewById(R.id.firstsong_artist);

            ImageView firstcoverart = (ImageView) findViewById(R.id.coverart);
            LinearLayout covfond = (LinearLayout) findViewById(R.id.coverartfond);

            Bitmap blurbit = ImageProcessing.fastblur(musicSrv.CurrentSong.get(0).getCoverart(), 0.1f, 10);
            firstcoverart.setImageBitmap(musicSrv.CurrentSong.get(0).getCoverart()); //associated cover art in bitmap;
            BitmapDrawable ob = new BitmapDrawable(getResources(), blurbit);
            covfond.setBackgroundDrawable(ob);

            if (artist.equals("<unknown>")) {
                firstartistView.setText("");
            } else {
                firstartistView.setText(musicSrv.CurrentSong.get(0).getArtist());
            }
            firstsongView.setText(musicSrv.CurrentSong.get(0).getTitle());
        }
    }

    public void refreshPlaylist() {
        itemView = (ListView) findViewById(R.id.playlist);
        this.songAdt = new SongAdapter(this, R.layout.song, MusicService.PlaylistSongs, swipelistview);
        itemView.setAdapter(songAdt);
    }


    //Player function
    public void onPausePlaySong(View view) {
        if (musicSrv.isPlaying == true) {
            musicSrv.pauseSong();
            musicSrv.isPlaying = false;
            ImageButton b = (ImageButton) findViewById(R.id.pause_play_song);
            b.setImageResource(R.drawable.ic_action_play);

        } else if (musicSrv.CurrentSong.size() > 0) {
            musicSrv.playSongAgain();
            MusicService.isPlaying = true;
            ImageButton b = (ImageButton) findViewById(R.id.pause_play_song);
            b.setImageResource(R.drawable.ic_action_pause);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onCompletionListener);
        super.onPause();
    }

    public void onNextSong(View view) {
        musicSrv.nextSong();
        updateUINext();
    }

    public void updateUINext() {
        if (MusicService.CurrentSong.size() > 0) {
            seek_bar.setMax((int) MusicService.CurrentSong.get(0).getDuration());
            songAdt.notifyDataSetChanged();
            addFirstSong();
        }
    }


    //Useful method
    public String convertPositionString(long position) {
        String seconds = String.valueOf((position % 60000) / 1000);
        String minutes = String.valueOf(position / 60000);
        String stringDuration = minutes + ":" + seconds;
        return stringDuration;
    }

    public void AddSongButton(View view) {
        Intent intent = new Intent(this, ListCategoryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Put up the Yes/No message box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Quit & destroy your playlist")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked, do something
                        if (MusicService.isPlaying == true) {
                            MusicService.player.pause();
                            MusicService.isPlaying = false;
                            MusicService.firstPlay = false;
                            MusicService.CurrentSong.clear();
                            MusicService.PlaylistSongs.clear();
                            PlaylistActivity.songAdt.notifyDataSetChanged();
                        }
                        PlaylistActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null) //Do nothing on no
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


}