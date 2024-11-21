package com.proyecto.grupo10.petshome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuscarCuidadorActivity extends AppCompatActivity {

    private MapView mMapView;
    private MapController mMapController;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private TextView  mDistancia, mServicios;
    private Button mFiltros;
    private List cuidadores = new ArrayList();

    private LocationManager locationManager;
    private Location locationGPS = new Location("");


    // Default map zoom level:
    private int MAP_DEFAULT_ZOOM = 17;
    // Default map Latitude:
    private double MAP_DEFAULT_LATITUDE = -34.90445;
    // Default map Longitude:
    private double MAP_DEFAULT_LONGITUDE = -57.92529;
    private int distanciaInt = 100;
    private String[] servicios = {"Mascotas pequeñas (≤10 kg)", "Mascotas medianas (10-25 kg)", "Mascotas grandes (>25 kg)", "Paseos", "Limpieza/baño", "Cuidado nocturno", "Ofrece alimentacion",
    "Actualización fotos/videos", "Experiencia con cachorros", "Experiencia con mascotas ancianas", "Experiencia con cuidados especiales", "Conocimientos en primeros auxilios", "Disponible fines de semana",
    "Disponible dias festivos", "Servicio 24 horas"};
    private boolean[] seleccionados;


    Integer idUsuario;
    Properties configProperties = new Properties();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_cuidador);

        /***
         *
         * CARGO ARCHIVO PROPERTIES CON LA IP DE CADA UNO
         *
         */

        try {
            InputStream inputStream = this.getAssets().open("config.properties");
            configProperties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /**
         * PRUEBO QUE FUNCIONE LA INVOCACION A LA VARIABLE
         */
        Log.i("debug", "La URL obtenida del archivo properties es: " + configProperties.getProperty("url"));

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            idUsuario = extras.getInt("idUsuario");
        }

        /**
         * Inicializo los controles de pantalla
         */
        mFiltros = findViewById(R.id.btn_filtro);
        mDistancia = findViewById(R.id.tv_filtro_distancia);
        mServicios = findViewById(R.id.tv_filtro_servicios);
        mDistancia.setText("10");


        /**
         * Cargo los listener de los filtros
         */
        mFiltros.setOnClickListener(filtrosOnClickListener);


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

        addMarker(new GeoPoint(-34.903648,-57.9197016), "Marta Minujin");
        addMarker(new GeoPoint(-34.9134263,-57.9294459), "Majin Bu");
        addMarker(new GeoPoint(-34.9144812,-57.9388078), "Bratt Pitt");
        addMarker(new GeoPoint(-34.9136027,-57.9426325), "Bruce Wayne");
        addMarker(new GeoPoint(-34.9066324,-57.9436054), "Batman");
        addMarker(new GeoPoint(-34.9171751,-57.9071399), "Mata Perros");
*/

        /**
         * Obtengo la lista de todos los cuidadores
         */
        listarCuidadores( new CuidadoresCallback() {
            @Override
            public void onCuidadoresListReceived(List<Cuidador> cuidadores) {

                Log.i("debug", "entro luego de cargar cuidadores: " + cuidadores.size());


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


                adapter.setOnClickListener(new CuidadorAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, Cuidador model) {

                        Intent cuidadorIntent = new Intent(BuscarCuidadorActivity.this, CuidadorPresentacionActivity.class);
                        cuidadorIntent.putExtra("nombre", model.getNombre());
                        cuidadorIntent.putExtra("idCuidador", model.getIdCuidador());
                        cuidadorIntent.putExtra("apellido", model.getApellido());
                        cuidadorIntent.putExtra("idTutor", idUsuario);

                        startActivity(cuidadorIntent);
                        finish();

                    }
                });


            }
        });



    }


    public void addMarker (GeoPoint center){
        Marker marker = new Marker(mMapView);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_mi_ubicacion));

        // Cargo la localizacion para medir la distancia con cada cuidaor
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

    public View.OnClickListener filtrosOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {



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

    @Override
    public void onBackPressed() {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                // Construir la URL con los parámetros email y contraseña
                                String urlStr = configProperties.getProperty("url")+"/usuario/" + idUsuario;
                                URL url = new URL(urlStr);
                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestMethod("GET");

                                int responseCode = urlConnection.getResponseCode();

                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    // Leer la respuesta del servidor
                                    InputStream inputStream = urlConnection.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    StringBuilder result = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        result.append(line);
                                    }
                                    String response = result.toString();
                                    Log.i("Login Response", response);

                                    // Procesar el JSON de respuesta
                                    JSONObject jsonResponse = new JSONObject(response);
                                    int rol = jsonResponse.getInt("rol"); // Obtener el rol del usuario
                                    int idUsuario = jsonResponse.getInt("idUsuario");
                                    String nombre = jsonResponse.getString("nombre");
                                    String apellido = jsonResponse.getString("apellido");
                                    String email = jsonResponse.getString("email");
                                    String pass = jsonResponse.getString("password");
                                    Boolean mCuidador = false;
                                    Intent homeIntent;
                                    homeIntent = new Intent(BuscarCuidadorActivity.this, MenuTutorActivity.class);
                                    homeIntent.putExtra("idUsuario", idUsuario);
                                    homeIntent.putExtra("nombre", nombre);
                                    homeIntent.putExtra("apellido", apellido);
                                    homeIntent.putExtra("email", email);
                                    homeIntent.putExtra("pass", pass);
                                    homeIntent.putExtra("esCuidador", mCuidador);

                                    startActivity(homeIntent);

                                }

                                urlConnection.disconnect();

                            } catch (Exception e) {
                                e.printStackTrace();
                                // Mostrar un mensaje de error genérico en caso de excepción
                                runOnUiThread(() -> {
                                });
                            }
                        }
                    }).start();
    }

    private void listarCuidadores( BuscarCuidadorActivity.CuidadoresCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String urlStr = configProperties.getProperty("url") + "/cuidador/cardview";
                Log.i("debug", "URL: " + urlStr);
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    String response = result.toString();
                    Log.i("debug", response);

                    JSONArray jsonArray = new JSONArray(response); // Cambia a JSONArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i); // Obtener cada objeto JSON
                        int idCuidador = jsonObject.getInt("idCuidador");
                        String nombre = jsonObject.getString("nombre");
                        String apellido = jsonObject.getString("apellido");
                        double latitud = jsonObject.getDouble("latitud");
                        double longitud = jsonObject.getDouble("longitud");
                        double puntuacion = jsonObject.getDouble("puntuacion");
                        int comentarios = jsonObject.getInt("comentarios");


                        int fotoGenerica  = getResources().getIdentifier("cuidador1", "drawable", getPackageName());


                        // Armo la posicion del complejo como Location para medir la distancia con la locacion del GPS
                        Location markerLocation = new Location("");
                        GeoPoint cuidadorPoint = new GeoPoint(latitud, longitud);


                        markerLocation.setLatitude(cuidadorPoint.getLatitude());
                        markerLocation.setLongitude(cuidadorPoint.getLongitude());

                        Float distancia = markerLocation.distanceTo(locationGPS)/1000;
                        Log.i("debug", "cuidador: " + nombre + " distancia: " + distancia + " km");
                        NumberFormat formatter = new DecimalFormat("0.00");
                        String dist = formatter.format(distancia).toString();

                        // comparo la distancia seleccionada por el usuario con la del complejo
                        // si es menor agrego el marcador en el mapa y la cardview en la vista



                        Log.i("debug", "Cardview Cuidador: " + nombre + ", latitud: " + latitud + ", longitud: " + longitud);

                        cuidadores.add(new Cuidador(idCuidador, nombre, apellido, new GeoPoint(latitud,longitud),dist + " Km.",  String.valueOf(comentarios), String.valueOf(puntuacion), fotoGenerica, configProperties.getProperty("url")));



                        /**
                         * Agrego los marcadores en el mapa con la direccion del cuidador
                         */
                        addMarker(new GeoPoint(latitud,longitud), nombre);

                    }



                    runOnUiThread(() -> callback.onCuidadoresListReceived(cuidadores));

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(BuscarCuidadorActivity.this, "No hay cuidadores para mostrar", Toast.LENGTH_SHORT).show();
                    });
                    Log.e("Login Error", "Error de inicio de sesión: " + responseCode);
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(BuscarCuidadorActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Shutdown the executor if no longer needed (optional)
        // executor.shutdown();
    }

    public interface CuidadoresCallback {
        void onCuidadoresListReceived(List<Cuidador> cuidadores);
    }




}