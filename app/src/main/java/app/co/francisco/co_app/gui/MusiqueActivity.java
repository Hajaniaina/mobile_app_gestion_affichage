package app.co.francisco.co_app.gui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.github.clans.fab.FloatingActionButton;

import app.co.francisco.co_app.R;
import app.co.francisco.co_app.utils.Preferences;

public class MusiqueActivity extends AppCompatActivity {
    Button start,pause,stop;
    FloatingActionButton left_musique;
    Preferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musique);

        init();
    }

    void init(){
        pref = new Preferences(MainActivity.context);
        start=(Button)findViewById(R.id.start);
        pause=(Button)findViewById(R.id.pause);
        stop=(Button)findViewById(R.id.stop);
        left_musique = (FloatingActionButton)findViewById(R.id.left_musique);
        //creating media player
        final MediaPlayer mp=new MediaPlayer();
        try{
            //you can change the path, here path is external directory(e.g. sdcard) /Music/maine.mp3
            mp.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/AGRAD - Adino-1.mp3");

            mp.prepare();
        }catch(Exception e){e.printStackTrace();}

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.set_preference("musique_value","1","musique");
                mp.start();

            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.set_preference("musique_value","1","musique");
                mp.pause();

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.set_preference("musique_value","0","musique");
                mp.stop();

            }
        });

        left_musique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu,menu);
        return true;
    }
}
