package app.co.francisco.co_app.gui;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firetrap.permissionhelper.helper.PermissionHelper;
import com.google.android.gms.maps.SupportMapFragment;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.co.francisco.co_app.My_Profile_Activity;
import app.co.francisco.co_app.R;
import app.co.francisco.co_app.connectivity.Connectivity;
import app.co.francisco.co_app.manager.App_manager;
import app.co.francisco.co_app.manager.PositionManager;
import app.co.francisco.co_app.utils.Permission.LoadPermission;
import app.co.francisco.co_app.utils.Preferences;

public class MainActivity extends AppCompatActivity implements App_manager.app_services {


    private static final String TAG = "Main Activity" ;
    Timer timer;
    TimerTask task;
    ImageView camera;
    ImageView bluetooth;
    ImageView connexion_wifi;
    ImageView connexion_mobile;
    ImageView musique;
    ImageView gps;
    TextView heure_actuelle,date_actuelle;
    App_manager manager;
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    Preferences pref ;
    public  static MainActivity activity;
    public  static Context context;

    BluetoothAdapter bluetoothAdapter;
    private SupportMapFragment mapFrag;

    private ProgressDialog progress;
    private PermissionHelper.PermissionBuilder permissionRequest;
    private PositionManager posManager;
    private NotificationCompat.Builder notif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(true);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.quitter,Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        //verif_connection();
        statut_modules_on_create();
        manager.my_run();

        if (!PositionManager.isLocationEnabled(getApplicationContext())) {
            showLocationAlert();
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothAdapter.getDefaultAdapter().enable();
        }
        if (Connectivity.isConnected(getApplicationContext())) {
            if (progress != null) {
                progress.setMessage(getString(R.string.verifier_connection));
            }
        }

        super.onResume();
    }

    @Override
    protected void onStart() {
        //verif_connection();
        statut_modules_on_create();
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (progress != null) {
            progress.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        pref.set_preference("musique_value","0","musique");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.my_rotation_screen:
                return true;
            case R.id.quitter:
                System.exit(0);
                return  true;
            case R.id.my_profile:
                Intent intent = new Intent().setClass(getApplicationContext(),My_Profile_Activity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public void setPermissionRequest(PermissionHelper.PermissionBuilder permissionRequest) {
        this.permissionRequest = permissionRequest;
    }
    public void showLocationAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(getString(R.string.location_settings_string));
        alertDialog.setMessage(getString(R.string.location_rationale_string));

        alertDialog.setPositiveButton(getString(R.string.action_settings), new DialogInterface.OnClickListener() {

            public void onClick (DialogInterface dialog, int which) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    void showStocage_externe_alert(){

    }

    void init( boolean first_init)
    {
        showLocationAlert();


        camera = (ImageView)findViewById(R.id.camera) ;
        bluetooth =(ImageView)findViewById(R.id.bluetooth) ;
        connexion_wifi = (ImageView)findViewById(R.id.connexion_wifi) ;
        connexion_mobile = (ImageView)findViewById(R.id.connexion_mobile);
        musique = (ImageView)findViewById(R.id.musique);
        gps = (ImageView)findViewById(R.id.gps);
        date_actuelle = (TextView)findViewById(R.id.date_actuelle);
        heure_actuelle = (TextView)findViewById(R.id.heure_actuelle);

        Date now = new Date();
        DateFormat dateformatter = DateFormat.getDateInstance(DateFormat.SHORT);
        String formattedDate = dateformatter.format(now);
        DateFormat timeformatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        String formattedTime = timeformatter.format(now);

        date_actuelle.setText(formattedDate);
        heure_actuelle.setText(formattedTime);


        manager = new App_manager(this,this);
        pref = new Preferences(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        posManager = new PositionManager( this,this);

        activity = this;
        context = this;

        progress = new ProgressDialog(this, R.style.InfoDialogStyle);
        progress.setMessage(getString(R.string.progress_map_string));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);

        timer = new Timer();

        progress.show();
        progress.dismiss();
        //verif_connection();
        all_lestenere();
        statut_modules_on_create();

        //mapFrag.getMapAsync(this);
        //manager.my_run();
        onOrientationlistener();
    }


    private OrientationEventListener mOrientationListener;
    private boolean orientation = false;
    public void onOrientationlistener () {

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (mOrientationListener.canDetectOrientation() == true) {
                    mOrientationListener.enable();
                    if( MainActivity.this.orientation )
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                } else {
                    mOrientationListener.disable();
                }
            }
        };

        mOrientationListener.onOrientationChanged(getRequestedOrientation());
        if( !orientation ) {
            orientation = true;
        }
    }


    public MainActivity getActivity(){
        return activity;
    }
    public  Context getContextMain(){
        return context;
    }
    public void all_lestenere(){
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),CameraActivity.class);
                startActivity(intent);

            }
        });
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent().setClass(getApplicationContext(),BleutoothActivity.class);
                //startActivity(intent);
                action_bleutooth();
            }
        });

        connexion_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new Connectivity().etatWifi(getApplicationContext())){
                    new Connectivity().activate_connection_wifi(getApplicationContext(),true);
                    pref.set_preference("wifi_value","1","wifi_connexion");
                    Toast.makeText(getApplicationContext(),"Connexion", Toast.LENGTH_LONG).show();
                }else{
                    pref.set_preference("wifi_value","0","wifi_connexion");
                    new Connectivity().activate_connection_wifi(getApplicationContext(),false);
                    Toast.makeText(getApplicationContext(),"Deconnexion", Toast.LENGTH_LONG).show();
                }
            }
        });

        connexion_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Connectivity().activate_connection_mobile(getApplicationContext());
            }
        });

        musique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),PlayListeActivity.class);
                startActivity(intent);
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });

        heure_actuelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),CalandarActivity.class);
                startActivity(intent);
            }
        });
        date_actuelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),CalandarActivity.class);
                startActivity(intent);
            }
        });

    }

    /*verifier la connection*/
    public void verif_connection(){
        final Handler handler = new Handler();
         task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!new Connectivity().isConnected(getApplicationContext())){
                            progress.setMessage(getString(R.string.verifier_connection));
                            progress.show();
                        }else{
                            progress.dismiss();
                        }
                    }
                });
            }
        };
         timer.schedule(task,0,500);
    }
    void statut_modules_on_create(){
        /*pour bleuTooth*/
        if(bluetoothAdapter.isEnabled()){
            pref.set_preference("bleutooth_value","1","bleutooth");
        }else {
            pref.set_preference("bleutooth_value","0","bleutooth");
        }
        /*Pour le wifi*/
        if(new Connectivity().etatWifi(getApplicationContext())){
            pref.set_preference("wifi_value","1","wifi_connexion");
        }else{
            pref.set_preference("wifi_value","0","wifi_connexion");
        }
    }
    void action_bleutooth(){
        if (bluetoothAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                //Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
                // Possibilité 1 :
                Intent activeBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(activeBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
                // ou Possibilité 2:
                //bluetoothAdapter.enable();
            }
            else
            {
                bluetoothAdapter.disable();
                Toast.makeText(getApplicationContext(), "Bluetooth desactivé", Toast.LENGTH_SHORT).show();
                pref.set_preference("bleutooth_value","0","bleutooth");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;
        if (resultCode == RESULT_OK)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth activé", Toast.LENGTH_SHORT).show();
            pref.set_preference("bleutooth_value","1","bleutooth");
        }
        else {
            Toast.makeText(getApplicationContext(), "Bluetooth non activé !", Toast.LENGTH_SHORT).show();
            pref.set_preference("bleutooth_value","0","bleutooth");
        }
    }

    @Override
    public void service_camera() {

    }

    @Override
    public void service_bleutooth() {
        statut_modules_on_create();
        String value_bleutooth = pref.get_preference("bleutooth_value","bleutooth");
        switch (value_bleutooth){
            case "1":
                bluetooth.setImageResource(R.drawable.ic_bluetooth_connected);
                Drawable draw = bluetooth.getDrawable();
                draw.setColorFilter(getResources().getColor(R.color.colorAppGrey), PorterDuff.Mode.SRC_ATOP);
                Log.d(TAG,"changement de couleur en bleu");
                return;
            case "0":
                bluetooth.setImageResource(R.drawable.bluetooth);
                Drawable draw2 = bluetooth.getDrawable();
                draw2.setColorFilter(getResources().getColor(R.color.colorAppblack), PorterDuff.Mode.SRC_ATOP);
                Log.d(TAG,"changement de couleur en noire");
                return;
        }
    }

    @Override
    public void service_wifi() {
        String value_wifi = pref.get_preference("wifi_value","wifi_connexion");
        switch (value_wifi){
            case "1":
                connexion_wifi.setImageResource(R.drawable.ic_signal_wifi_activated);
                Drawable draw = connexion_wifi.getDrawable();
                draw.setColorFilter(getResources().getColor(R.color.colorAppGrey), PorterDuff.Mode.SRC_ATOP);
                return;
            case "0":
                connexion_wifi.setImageResource(R.drawable.connexion);
                Drawable draw2 = connexion_wifi.getDrawable();
                draw2.setColorFilter(getResources().getColor(R.color.colorAppblack), PorterDuff.Mode.SRC_ATOP);
                return;
            default:
                return;
        }
    }

    @Override
    public void service_musique() {
        String value_musique = pref.get_preference("musique_value","musique");
        Log.d(TAG,"musique :" +value_musique);
        switch (value_musique){
            case "1":
                musique.setImageResource(R.drawable.ic_musique_marche);
                Drawable draw = musique.getDrawable();
                draw.setColorFilter(getResources().getColor(R.color.colorAppGrey), PorterDuff.Mode.SRC_ATOP);
                return;
            case "0":
                musique.setImageResource(R.drawable.musique);
                Drawable draw2 = musique.getDrawable();
                draw2.setColorFilter(getResources().getColor(R.color.colorAppblack), PorterDuff.Mode.SRC_ATOP);
                return;
            default:
                return;
        }
    }

    @Override
    public void service_mobile_connexion() {
        if(new Connectivity().isConnected(getApplicationContext())){
            if(new Connectivity().isConnectedMobile(getApplicationContext())){
                connexion_mobile.setImageResource(R.drawable.ic_signal_connection_mobile_connected);
                Drawable draw  = connexion_mobile.getDrawable();
                draw.setColorFilter(getResources().getColor(R.color.colorAppGrey), PorterDuff.Mode.SRC_ATOP);
            }else{
                connexion_mobile.setImageResource(R.drawable.ic_signal_connection_mobile_desactivated);
            }
        }
    }
}
