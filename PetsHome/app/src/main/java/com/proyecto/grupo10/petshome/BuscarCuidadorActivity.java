package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class BuscarCuidadorActivity extends AppCompatActivity {

    private MapView mMapView;
    private MapController mMapController;

    private LocationManager locationManager;
    private Location locationGPS = new Location("");


    // Default map zoom level:
    private int MAP_DEFAULT_ZOOM = 16;
    // Default map Latitude:
    private double MAP_DEFAULT_LATITUDE = -34.90445;
    // Default map Longitude:
    private double MAP_DEFAULT_LONGITUDE = -57.92529;
    private int distanciaInt = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_cuidador);




        //Donde muestra la imagen del mapa
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        mMapView = (MapView) findViewById(R.id.osmmap);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mMapController = (MapController)mMapView.getController();
        mMapController.setZoom(MAP_DEFAULT_ZOOM);

        GeoPoint center = new GeoPoint(MAP_DEFAULT_LATITUDE,MAP_DEFAULT_LONGITUDE);
        mMapController.animateTo(center);

        addMarker(center);

    }


    public void addMarker (GeoPoint center){
        Marker marker = new Marker(mMapView);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_mi_ubicacion));

        // Cargo la localizacion para medir la distancia con cada complejo
        locationGPS.setLatitude(marker.getPosition().getLatitude());
        locationGPS.setLongitude(marker.getPosition().getLongitude());
        Log.i("logReserva", "marcador de gps: " + locationGPS.getLatitude() );

        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
        marker.setTitle("usted esta aqui");

    }

    
}