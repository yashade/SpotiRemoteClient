package com.yashade2001.spotiremoteclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_URL = "https://spotiremote.herokuapp.com";

    ImageView albumCoverImageView;
    TextView metaDataTextView;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ion.getDefault(getApplicationContext()).configure().setLogging("ion", Log.DEBUG);

        mSocket.on("metadatachanged", onMetadatachangedEvent);
        mSocket.connect();

        Button prevButton = (Button) findViewById(R.id.button_prev);
        Button nextButton = (Button) findViewById(R.id.button_next);
        Button playpauseButton = (Button) findViewById(R.id.button_playpause);
        final EditText searchEditText = (EditText) findViewById(R.id.editText);
        Button searchplayButton = (Button) findViewById(R.id.button_searchplay);
        final SeekBar volumeSeekBar = (SeekBar) findViewById(R.id.seekBar);
        albumCoverImageView = (ImageView) findViewById(R.id.imageView_album_cover);
        metaDataTextView = (TextView) findViewById(R.id.textView_metadata);

        Intent intent = getIntent();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            String trackUri = sharedText.replace("https://open.spotify.com/track/", "spotify:track:");
            Ion.with(getApplicationContext())
                    .load("POST", SERVER_URL + "/api/share?track=" + trackUri)
                    .asString();
        }

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Ion.with(getApplicationContext())
                        .load("POST", SERVER_URL + "/api/setvolume?level=" + progress)
                        .asString();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(getApplicationContext())
                        .load("POST", SERVER_URL + "/api/prev")
                        .asString();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(getApplicationContext())
                        .load("POST", SERVER_URL + "/api/next")
                        .asString();
            }
        });

        playpauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(getApplicationContext())
                        .load("POST", SERVER_URL + "/api/playpause")
                        .asString();
            }
        });

        searchplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString()
                        .replaceAll(" ", "%20")
                        .replaceAll("ü", "%C3%BC")
                        .replaceAll("ö", "%C3%B6")
                        .replaceAll("ä", "%C3%84")
                        .replaceAll("ß", "%C3%9F");

                Ion.with(getApplicationContext())
                        .load("POST", SERVER_URL + "/api/searchplay?query=" + query)
                        .asString();
            }
        });

    }

    private Emitter.Listener onMetadatachangedEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            refreshMetadata();
        }
    };

    void refreshMetadata() {
        Ion.with(getApplicationContext())
                .load("GET", SERVER_URL + "/api/metadata")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject metadata = new JSONObject(result);
                            String track = metadata.getString("track");
                            String artist = metadata.getString("artist");
                            String album = metadata.getString("album");
                            String albumCover = metadata.getString("albumcover");

                            metaDataTextView.setText(track + "\n \n" +
                                                     artist + "\n \n" +
                                                     album);
                            Picasso.with(getApplicationContext())
                                    .load(albumCover)
                                    .into(albumCoverImageView);
                        } catch (JSONException | NullPointerException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("metadatachanged", onMetadatachangedEvent);
    }
}
