package com.yashade2001.spotiremoteclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button prevButton = (Button) findViewById(R.id.button_prev);
        Button nextButton = (Button) findViewById(R.id.button_next);
        Button playpauseButton = (Button) findViewById(R.id.button_playpause);
        final EditText searchEditText = (EditText) findViewById(R.id.editText);
        Button searchplayButton = (Button) findViewById(R.id.button_searchplay);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(getApplicationContext())
                        .load("http://192.168.1.31:1337/api/prev")
                        .setBodyParameter("a", "b")
                        .asString();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(getApplicationContext())
                        .load("http://192.168.1.31:1337/api/next")
                        .setBodyParameter("a", "b")
                        .asString();
            }
        });

        playpauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ion.with(getApplicationContext())
                        .load("http://192.168.1.31:1337/api/playpause")
                        .setBodyParameter("a", "b")
                        .asString();
            }
        });

        searchplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().replaceAll(" ", "%20");

                Ion.with(getApplicationContext())
                        .load("http://192.168.1.31:1337/api/searchplay?query=" + query)
                        .setBodyParameter("a", "b")
                        .asString();
            }
        });

    }
}
