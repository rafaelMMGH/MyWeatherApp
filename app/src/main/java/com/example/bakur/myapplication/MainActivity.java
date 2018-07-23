package com.example.bakur.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakur.myapplication.Common.Common;
import com.example.bakur.myapplication.Helper.Helper;
import com.example.bakur.myapplication.Model.Main;
import com.example.bakur.myapplication.Model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity implements LocationListener, SwipeRefreshLayout.OnRefreshListener {

    TextView txtCity,txtTime, txtCelsius, txtHumidity, txtDescription;
    ImageView imageView;
    LinearLayout linearLayoutUp,linearLayoutDown;
    SwipeRefreshLayout sr;
    Animation uptodown,downtoup;

    LocationManager locationManager;
    String provider;
    double lat, lng;
    boolean isFirstTime = true;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int My_Permission = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WelcomeActivity.wA.finish();

        final View view = findViewById(R.id.content);

        txtCity = (TextView) findViewById(R.id.txtCity);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);

        imageView = (ImageView) findViewById(R.id.imageView);

        linearLayoutUp = (LinearLayout) findViewById(R.id.linearLayoutUp);
        linearLayoutDown = (LinearLayout) findViewById(R.id.linearLayoutDown);
        sr = (SwipeRefreshLayout) findViewById(R.id.sr);
        sr.setOnRefreshListener(this);

        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        linearLayoutUp.setAnimation(uptodown);
        linearLayoutDown.setAnimation(downtoup);


        //Obtener Coordenadas
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, My_Permission);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("TAG", "No location recognized");

        String LastUpdated = (String.format("Last Updated: %s", Common.getDateNow()));
        Snackbar.make(view,LastUpdated,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, My_Permission);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        String LastUpdated = (String.format("Last Updated: %s", Common.getDateNow()));
        final View view = findViewById(R.id.content);
        Snackbar.make(view,LastUpdated,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        if (isFirstTime){
            new getWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));
            isFirstTime = false;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRefresh() {
        new getWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));
        sr.setRefreshing(false);
    }

    private class getWeather extends AsyncTask<String,Void,String>{
        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Por favor espera...");
            pd.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return  stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.contains("Error: Not found city")){
                pd.dismiss();
                return;
            }
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            pd.dismiss();

            Picasso.get().load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon())).into(imageView);

            txtCity.setText(String.format("%s, %s", openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            txtDescription.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("%d",openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("%s - %s",Common.unixTineStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTineStampToDateTime(openWeatherMap.getSys().getSunset())));
            txtCelsius.setText(String.format("%.1f ℃",openWeatherMap.getMain().getTemp()));

        }
    }
}
