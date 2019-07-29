package app.co.francisco.co_app.manager;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ASUS on 21/03/2019.
 */

public class MapManager {
    MapService service;
    Context context;
    String TAG = "Map Manager";

    Timer timer;
    TimerTask task;
    public interface  MapService{
        public void drawLines();
    }

    public  MapManager(Context ctx, MapService serv){
        service = serv;
        context = ctx;
        timer = new Timer();
    }

    public  void myRun(){

            final Handler handler = new Handler();
            task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            service.drawLines();
                            Log.d(TAG,"Timer ok");
                        }
                    });
                }
            };
            timer.schedule(task,0,200);

    }
    public  void stop (){
        task.cancel();
        timer.cancel();
    }

    public  static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }

            return (dist);
        }
    }

}
