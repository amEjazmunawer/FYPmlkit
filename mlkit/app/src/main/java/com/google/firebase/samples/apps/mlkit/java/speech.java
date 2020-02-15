package com.google.firebase.samples.apps.mlkit.java;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Static class to access TextToSpeech across the apps
 */
public class speech {
    private static TextToSpeech t1=null;

    public static void init(Context context){
        if(t1 == null)
        {
            t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.UK);
                    }
                }
            });
        }
    }

    public static void speak(String s){
        t1.speak(s, TextToSpeech.QUEUE_ADD, null);
    }

    public static void speak_flush(String s){
        t1.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }

}
