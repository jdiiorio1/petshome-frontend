package com.proyecto.grupo10.petshome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BuscarCuidadorActivity extends AppCompatActivity {

    private MapView mMapView;
    private MapController mMapController;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private TextView mFechaInicio, mFechaFin, mDistancia, mServicios;


    private LocationManager locationManager;
    private Location locationGPS = new Location("");


    // Default map zoom level:
    private int MAP_DEFAULT_ZOOM = 17;
    // Default map Latitude:
    private double MAP_DEFAULT_LATITUDE = -34.90445;
    // Default map Longitude:
    private double MAP_DEFAULT_LONGITUDE = -57.92529;
    private int distanciaInt = 100;
    private String[] servicios = {"patio abierto", "Caniles para dormir", "Arbol o gimnasio para gatos", "mas de 20 metros cuadrados", "paseador"};
    private boolean[] seleccionados;
    List cuidadores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_cuidador);

        /**
         * Inicializo los controles de pantalla
         */
        mFechaInicio = findViewById(R.id.tv_filtro_inicio);
        mFechaFin = findViewById(R.id.tv_filtro_fin);
        mDistancia = findViewById(R.id.tv_filtro_distancia);
        mServicios = findViewById(R.id.tv_filtro_servicios);

        /**
         * Cargo los listener de los filtros
         */
        mFechaInicio.setOnClickListener(calendarioOnClickListener);
        mFechaFin.setOnClickListener(calendarioOnClickListener);

        /**
         * Hardcodeo de los servicios
         */
        seleccionados = new boolean[servicios.length];
        mServicios.setOnClickListener(v -> mostrarSeleccion());


        //Donde muestra la imagen del mapa
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        mMapView = (MapView) findViewById(R.id.osmmap);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
       // mMapView.setMinZoomLevel(new Double(MAP_DEFAULT_ZOOM));
        mMapView.setMaxZoomLevel(new Double(MAP_DEFAULT_ZOOM));

        mMapController = (MapController)mMapView.getController();
        mMapController.setZoom(MAP_DEFAULT_ZOOM);


        /**
         * cargo la ubicacion del tutor, en este caso esta en la UTN
         */
        GeoPoint center = new GeoPoint(MAP_DEFAULT_LATITUDE,MAP_DEFAULT_LONGITUDE);
        mMapController.animateTo(center);
        addMarker(center);

        /**
         * Armo un conjunto de posiciones latitud, longitud hardcodeadas en el mapa
         * para mostrar la ubicacion de los cuidadores que son de prueba
         */
        addMarker(new GeoPoint(-34.903648,-57.9197016), "Marta Minujin");
        addMarker(new GeoPoint(-34.9134263,-57.9294459), "Majin Bu");
        addMarker(new GeoPoint(-34.9144812,-57.9388078), "Bratt Pitt");
        addMarker(new GeoPoint(-34.9136027,-57.9426325), "Bruce Wayne");
        addMarker(new GeoPoint(-34.9066324,-57.9436054), "Batman");
        addMarker(new GeoPoint(-34.9171751,-57.9071399), "Mata Perros");




        /***
         * Configuracion del recycler de los cuidadores
         * cargo la lista y configuro la animacion
         */
        cuidadores = new ArrayList();

        cuidadores.add(new Cuidador("Marta Minujin","300 m","17","4.5", new GeoPoint(-34.903648,-57.9197016)));
        cuidadores.add(new Cuidador("Majin Bu","420 m","2","3", new GeoPoint(-34.9134263,-57.9294459)));
        cuidadores.add(new Cuidador("Bratt Pitt","500 m","399","5", new GeoPoint(-34.9144812,-57.9388078)));
        cuidadores.add(new Cuidador("Bruce Wayne","500 m","26","4.2",new GeoPoint(-34.9136027,-57.9426325)));
        cuidadores.add(new Cuidador("Batman","500 m","26","4.2", new GeoPoint(-34.9066324,-57.9436054)));
        cuidadores.add(new Cuidador("Mata Perros","1.2 km","13","1", new GeoPoint(-34.9171751,-57.9071399)));

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recicladorCuidadores);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(lManager);

        final CuidadorAdapter adapter = new CuidadorAdapter(cuidadores);

        recycler.setAdapter(adapter);

        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recycler);

        /**
         * al moverme entre las tarjetas de los cuidadores, el mapa se posiciona en sus marcadores
         */
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View cardviewCentrada = linearSnapHelper.findSnapView(lManager);
                    if (cardviewCentrada != null) {
                        int posicion = recyclerView.getChildAdapterPosition(cardviewCentrada);
                        Cuidador cuidadorCentrado= (Cuidador) adapter.getItem(posicion);

                        mMapController.animateTo(cuidadorCentrado.getUbicacion());
                        //Toast.makeText(BuscarCuidadorActivity.this, "Cuidador Centrado: " + cuidadorCentrado.getNombre(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // int resId = R.anim.layout_animation_rotate_in;
        int resId = R.anim.layout_animation_rotate_in;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(BuscarCuidadorActivity.this, resId);
        recycler.setLayoutAnimation(animation);
        adapter.notifyDataSetChanged();





    }


    public void addMarker (GeoPoint center){
        Marker marker = new Marker(mMapView);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_mi_ubicacion));

        // Cargo la localizacion para medir la distancia con cada complejo
        locationGPS.setLatitude(marker.getPosition().getLatitude());
        locationGPS.setLongitude(marker.getPosition().getLongitude());
        Log.i("debug", "marcador de gps: " + locationGPS.getLatitude() );

        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
        //marker.setTitle("usted esta aqui");

    }

    public void addMarker (GeoPoint center, String nombre){
        Marker marker = new Marker(mMapView);
        marker.setPosition(center);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_ubicacion_cuidador2));
        // mMapView.getOverlays().clear();
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();




        //marker.getInfoWindow().getView().setBackground(ContextCompat.getDrawable(Home.this, R.drawable.input_button));
        //marker.getInfoWindow().getView().setBackgroundColor(Color.parseColor("#378a1e"));

        marker.setTitle(nombre);

    }


    /**
     * Selector de fecha
     */

    public View.OnClickListener calendarioOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final TextView textView = (TextView) v;
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mAnio = c.get(Calendar.YEAR);
            int mMes = c.get(Calendar.MONTH);
            int mSemana = c.get(Calendar.WEEK_OF_YEAR);
            int mDia = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(BuscarCuidadorActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {


                            //tvCalendario.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            textView.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mAnio, mMes, mDia);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);



            datePickerDialog.show();

        }
    };

    private void mostrarSeleccion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtra por Servicios")
                .setMultiChoiceItems(servicios, seleccionados, (dialog, which, isChecked) -> {
                    seleccionados[which] = isChecked;
                })
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    // Manejar los elementos seleccionados
                    StringBuilder seleccionadosString = new StringBuilder("Servicios " );
                    int cant = 0;



                    for (int i = 0; i < seleccionados.length; i++) {
                        if (seleccionados[i]) {
                            cant++;
                            //seleccionadosString.append(servicios[i]).append(" | ");
                        }
                    }

                    if (cant > 0) {
                        seleccionadosString.append("(" + cant + ")");
                    }

                    /*
                    // Eliminar la última coma y espacio
                    if (seleccionadosString.length() > 15) {
                        seleccionadosString.setLength(seleccionadosString.length() - 2);
                    } else {
                        seleccionadosString.append("ninguno");
                    }
                    */

                    mServicios.setText(seleccionadosString.toString());
                })
                .setNegativeButton("Cancelar", null);
        builder.create().show();
    }


}