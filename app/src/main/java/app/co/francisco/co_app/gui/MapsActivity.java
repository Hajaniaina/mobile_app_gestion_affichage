package app.co.francisco.co_app.gui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.co.francisco.co_app.R;
import app.co.francisco.co_app.connectivity.Connectivity;
import app.co.francisco.co_app.manager.MapManager;

public class MapsActivity extends FragmentActivity implements MapManager.MapService, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleApiClient.ConnectionCallbacks {

    FloatingActionButton btn_quit;
    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    TextView distance;

    private ProgressDialog progress;
    Timer timer;
    TimerTask task;
    private float ZOOM_CAMERA = 17;
    private static final float UPDATE_DELTA_T_MAX_MS = (30.0f);
    Location current_location, last_location=null;
    Location debut = null;

    MapManager manager;

    List<Polyline> mapPolylineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }

    @Override
    protected void onResume() {
        verif_connection();
        manager.myRun();
        super.onResume();
    }

    @Override
    protected void onStop() {
        //manager.stop();
        //timer.cancel();
        ///task.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //manager.stop();
        //timer.cancel();
        //task.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        manager.stop();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu,menu);
        return true;
    }

    void init(){
        progress = new ProgressDialog(this, R.style.InfoDialogStyle);
        progress.setMessage(getString(R.string.progress_map_string));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);
        distance = (TextView)findViewById(R.id.distance);
        btn_quit = (FloatingActionButton)findViewById(R.id.quite_map);

        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        manager = new MapManager(this,this);

        progress.show();
        progress.dismiss();
        timer = new Timer();

        verif_connection();
    }
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(getApplicationContext(),"ok connexion",Toast.LENGTH_LONG).show();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(),"erreur de la connexion",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(getApplicationContext(),"Location change",Toast.LENGTH_LONG).show();
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Debut");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.showInfoWindow();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_CAMERA));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    private void drawMapLine (LatLng startPoint, LatLng endPoint) {

        PolylineOptions opt = new PolylineOptions();

        //opt.width(3);
        opt.clickable(true);
        opt.width(5);
        opt.geodesic(true);
        opt.color(Color.rgb(0, 160, 255));
        opt.add(startPoint, endPoint);

        mapPolylineList.add(mMap.addPolyline(opt));

    }

    private boolean save_parcours(){
        return  true;
    }

    @Override
    public void drawLines() {
        Location location = mMap.getMyLocation();
        if(location!=null){
            //Toast.makeText(getApplicationContext(),"( "+location.getLatitude() +"  , "+location.getLongitude() + " )",Toast.LENGTH_LONG).show();
        }
        if(last_location==null){
            last_location = location;
            debut = location;
        }
        current_location = location;
        //Toast.makeText(getApplicationContext(),"  calcul :  = "+ UPDATE_DELTA_T_MAX_MS,Toast.LENGTH_LONG).show();
        if(current_location!=null && last_location!=null){
            if(current_location.getTime()-last_location.getTime() > UPDATE_DELTA_T_MAX_MS){
                LatLng curent = new LatLng(current_location.getLatitude(),current_location.getLongitude());
                LatLng last  = new LatLng(last_location.getLatitude(),last_location.getLongitude());
                last_location = current_location;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curent, ZOOM_CAMERA));
                drawMapLine(last,curent);
                /*Calcul distance*/
                double distance_km = manager.distance(current_location.getLatitude(),current_location.getLongitude(),debut.getLatitude(),debut.getLongitude(),"K");
                //Toast.makeText(getApplicationContext(), "Distance : " + distance + "  Km ",Toast.LENGTH_LONG).show();
                String dist = distance_km+"";
                dist = dist.substring(0,3);
                distance.setText(""+dist + " Km ");
            }
        }

    }
}
