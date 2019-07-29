package app.co.francisco.co_app.manager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import app.co.francisco.co_app.R;
import app.co.francisco.co_app.connectivity.Connectivity;

/**
 * Created by ASUS on 16/03/2019.
 */

public class App_manager {
    boolean boucle_infini=true;
    public  app_services service;
    public Context context;
    public  String TAG="App_Manager";

    Timer timer;
    TimerTask task;

    public  interface app_services{
        void service_camera();
        void service_bleutooth();
        void service_wifi();
        void service_musique();
        void service_mobile_connexion();
    }

    public App_manager(Context ctx,app_services serv){
            this.context=ctx;
            this.service = serv;
            timer = new Timer();
    }

    public  void  my_run(){
        if (boucle_infini){
            final Handler handler = new Handler();
            task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                                service.service_camera();
                                service.service_bleutooth();
                                service.service_wifi();
                                service.service_musique();
                                service.service_mobile_connexion();
                                Log.d(TAG,"Timer ok");
                        }
                    });
                }
            };
            timer.schedule(task,0,500);


        }
    }
    public  void stop (){
        task.cancel();
        timer.cancel();
    }
}
