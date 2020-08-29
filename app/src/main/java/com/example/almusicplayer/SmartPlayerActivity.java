package com.example.almusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayerActivity extends AppCompatActivity {

     private  RelativeLayout parentRelativeLayout ;
     private SpeechRecognizer speechRecognizer ;
     private Intent speechRecognizerIntent ;
     private String keeper="" ;

     private ImageView pauseplayBtn , previousBtn , nextBtn ;
     private TextView songNameTxt;

     private ImageView imageView ;
     private RelativeLayout lowerRelativeLayout ;
     private Button voiceEnableBtn ;

     private String Mod = "ON" ;

     private MediaPlayer mymediaPlayer ;
     private int position ;
     private ArrayList<File> mySongs;
     private String mSongName ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);

        cheackVoicCommandPermmision();

        initalVariable();
        touchedListener();

        speechRecognizerListener();

        validateReceiveValuesAndStartPlaying();

        imageView.setBackgroundResource(R.drawable.logo);


    }



    private void initalVariable() {
        parentRelativeLayout = findViewById(R.id.parentRelativLayout);
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        pauseplayBtn = findViewById(R.id.play_pause_btn);
        nextBtn=findViewById(R.id.next_btn);
        previousBtn=findViewById(R.id.previous_btn);
        songNameTxt = findViewById(R.id.songName);

        imageView=findViewById(R.id.logo);
        lowerRelativeLayout=findViewById(R.id.lower);
        voiceEnableBtn=findViewById(R.id.voice_enable_btn);


    }


    private void speechRecognizerListener() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null){
                   if (Mod.equals("ON")){
                       keeper=matchesFound.get(0);

                       if (keeper.equals("pause the song"))
                       {
                           playPauseSong();
                           Toast.makeText(SmartPlayerActivity.this, "Command ="+keeper, Toast.LENGTH_SHORT).show();
                       }else
                       if(keeper.equals("play the song"))
                       {
                           playPauseSong();
                           Toast.makeText(SmartPlayerActivity.this, "Command ="+keeper, Toast.LENGTH_SHORT).show();

                       }else
                       if(keeper.equals("play next song"))
                       {
                           playNextSong();
                           Toast.makeText(SmartPlayerActivity.this, "Command ="+keeper, Toast.LENGTH_SHORT).show();

                       }else
                       {
                           if(keeper.equals("play previous song"))
                           {
                               playPreviousSong();
                               Toast.makeText(SmartPlayerActivity.this, "Command ="+keeper, Toast.LENGTH_SHORT).show();

                           }
                       }
                   }
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        voiceEnableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Mod.equals("ON")){

                    Mod = "OFF";
                    voiceEnableBtn.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else {

                    Mod = "ON";
                    voiceEnableBtn.setText("Voice Enabled Mode - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);

                }
            }
        });

        pauseplayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseSong();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mymediaPlayer.getCurrentPosition()>0){
                    playPreviousSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mymediaPlayer.getCurrentPosition()>0){
                    playNextSong();
                }
            }
        });

    }


    private void touchedListener() {
        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }

                return false;

            }
        });
    }


    private void validateReceiveValuesAndStartPlaying(){
        if (mymediaPlayer != null){
            mymediaPlayer.stop();
            mymediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle  = intent.getExtras();

        mySongs= (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();

        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position",0);

        Uri uri = Uri.parse(mySongs.get(position).toString());

        mymediaPlayer = MediaPlayer.create(SmartPlayerActivity.this,uri);
        mymediaPlayer.start();

    }

    private void cheackVoicCommandPermmision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package :"+ getPackageName()));
                startActivity(intent);
                finish();

            }

        }
    }


    private void  playPauseSong(){
        imageView.setBackgroundResource(R.drawable.four);

        if (mymediaPlayer.isPlaying()){
            pauseplayBtn.setImageResource(R.drawable.play);
            mymediaPlayer.pause();
        }else {
            pauseplayBtn.setImageResource(R.drawable.pause);
            mymediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.five);


        }

    }

    private void playNextSong(){
        mymediaPlayer.pause();
        mymediaPlayer.stop();
        mymediaPlayer.release();

        position=((position+1)%mySongs.size());

        Uri uri = Uri.parse(mySongs.get(position).toString());
        mymediaPlayer=MediaPlayer.create(SmartPlayerActivity.this , uri);

        mSongName=mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        mymediaPlayer.start();

        imageView.setImageResource(R.drawable.three);

        if (mymediaPlayer.isPlaying()){
            pauseplayBtn.setImageResource(R.drawable.pause);
        }else {
            pauseplayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);


        }

    }

    private void playPreviousSong(){

        mymediaPlayer.pause();
        mymediaPlayer.stop();
        mymediaPlayer.release();

        position = ((position-1)<0 ? (mySongs.size()-1) : (position-1));

        Uri uri = Uri.parse(mySongs.get(position).toString());
        mymediaPlayer=MediaPlayer.create(SmartPlayerActivity.this , uri);

        mSongName=mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        mymediaPlayer.start();

        imageView.setImageResource(R.drawable.two);

        if (mymediaPlayer.isPlaying()){
            pauseplayBtn.setImageResource(R.drawable.pause);
        }else {
            pauseplayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);


        }



    }

}
