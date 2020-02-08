package com.google.firebase.samples.apps.mlkit.java;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.samples.apps.mlkit.R;

public class SplashScreenActivity extends AppCompatActivity {

    private TextToSpeech textToSpeechSystem;
    private static int SPLASH_TIME_OUT = 3000;

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashScreenActivity.this, StillImageActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);



    }
    @Override
    protected void onStart() {
        super.onStart();
        textToSpeechSystem = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    String textToSay = "Welcome!";
                    textToSpeechSystem.speak(textToSay, TextToSpeech.QUEUE_ADD, null);
                    Toast.makeText(getApplicationContext(),
                            textToSay, Toast.LENGTH_LONG).show();;
                }

            }
        });


    }

}
