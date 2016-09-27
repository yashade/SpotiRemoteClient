package com.yashade2001.spotiremoteclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_URL = "https://spotiremote.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button prevButton = (Button) findViewById(R.id.button_prev);
        Button nextButton = (Button) findViewById(R.id.button_next);
        Button playpauseButton = (Button) findViewById(R.id.button_playpause);
        final EditText searchEditText = (EditText) findViewById(R.id.editText);
        Button searchplayButton = (Button) findViewById(R.id.button_searchplay);
        final SeekBar volumeSeekBar = (SeekBar) findViewById(R.id.seekBar);

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
}
