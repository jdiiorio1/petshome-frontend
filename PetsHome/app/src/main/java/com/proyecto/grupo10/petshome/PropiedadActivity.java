package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import com.google.android.gms.maps.model.LatLng;



public class PropiedadActivity extends AppCompatActivity {

    ImageView mProfilePhoto, mImgAtras;

    EditText mCalle, mNumero,mLocalidad, mCP, mDepartamento, mPiso;
    Spinner mTipoVivienda;

    String nombre, apellido, email, pass;

    Button mRegistrarVivienda, mValidarUbicacion;
    private MapView mMapView;
    private MapController mMapController;

    private int MAP_DEFAULT_ZOOM = 17;
    // Default map Latitude:
    private double MAP_DEFAULT_LATITUDE = -34.90445;
    // Default map Longitude:
    private double MAP_DEFAULT_LONGITUDE = -57.92529;
    private LocationManager locationManager;
    private Location locationGPS = new Location("");
    Double latitud, longitud;
    Properties configProperties = new Properties();
    Integer idCuidador;
    Boolean editar = false;
    ArrayAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propiedad);

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

        mProfilePhoto = findViewById(R.id.img_profile_photo);

        mCalle = findViewById(R.id.et_calle);
        mNumero = findViewById(R.id.et_numero);
        mCP = findViewById(R.id.et_cp);
        mLocalidad = findViewById(R.id.et_localidad);
        mDepartamento = findViewById(R.id.et_departamento);
        mPiso = findViewById(R.id.et_piso);
        mTipoVivienda = findViewById(R.id.sp_tipo_vivienda);
        mRegistrarVivienda = findViewById(R.id.btn_registrar_propiedad);
        mValidarUbicacion = findViewById(R.id.btn_validar_ubicacion);
        mImgAtras = findViewById(R.id.img_atras);


        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            idCuidador = extras.getInt("idCuidador");
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");


        }



        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tipo_vivienda,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        mTipoVivienda.setAdapter(adapter);

        mTipoVivienda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tipo = adapterView.getItemAtPosition(i).toString();
                if (tipo.equals("Departamento")) {
                    mDepartamento.setVisibility(View.VISIBLE);
                    mPiso.setVisibility(View.VISIBLE);
                } else {
                    mDepartamento.setVisibility(View.INVISIBLE);
                    mPiso.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        cargarDatosAlojamiento(idCuidador);

        mValidarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.setVisibility(View.VISIBLE);

                validarUbicacion();

            }
        });

        mRegistrarVivienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validarCampos()) {

                    new Thread(() -> {
                        try {
                            String metodo = "";
                            URL url;
                            if(editar) {
                                url = new URL(configProperties.getProperty("url") + "/alojamiento/" + idCuidador);
                                metodo = "PUT";
                                Log.i("debug", "editando la vivienda");
                                Log.i("debug", url.toString());
                            } else {
                                url = new URL(configProperties.getProperty("url") + "/alojamiento");
                                metodo = "POST";
                            }

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod(metodo);
                            Log.i("debug",conn.getRequestMethod().toString());
                            conn.setRequestProperty("Content-Type", "application/json; utf-8");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setDoOutput(true);

                            JSONObject json = new JSONObject();
                            json.put("calle", mCalle.getText().toString());
                            json.put("numero", mNumero.getText().toString());
                            json.put("cp", mCP.getText().toString());
                            json.put("localidad", mLocalidad.getText().toString());
                            json.put("departamento", mDepartamento.getText().toString());
                            json.put("piso", mPiso.getText().toString());
                            json.put("tipoAlojamiento", mTipoVivienda.getSelectedItem());

                            if (!editar)
                                json.put("idCuidador", idCuidador);

                            try (OutputStream os = conn.getOutputStream()) {
                                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                                os.write(input, 0, input.length);
                            }

                            int responseCode = conn.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(() -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PropiedadActivity.this);
                                    builder.setMessage("Se guardaron los cambios.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent cuidadorIntent = new Intent(PropiedadActivity.this, MenuCuidadorActivity.class);
                                                    Log.i("debug", "id del cuidador despues de actualizar o crear vivienda: " + idCuidador + "Nombre: " + nombre);
                                                    cuidadorIntent.putExtra("idUsuario", idCuidador);
                                                    cuidadorIntent.putExtra("nombre",nombre);
                                                    cuidadorIntent.putExtra("apellido", apellido);
                                                    cuidadorIntent.putExtra("email", email);
                                                    cuidadorIntent.putExtra("pass", pass);

                                                    startActivity(cuidadorIntent);
                                                    finish(); // Cerrar la actividad
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(PropiedadActivity.this, "Error en el registro.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(PropiedadActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();



                }
            }
        });

        //Donde muestra la imagen del mapa
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        mMapView = (MapView) findViewById(R.id.map_validar);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        // mMapView.setMinZoomLevel(new Double(MAP_DEFAULT_ZOOM));
       // mMapView.setMaxZoomLevel(new Double(MAP_DEFAULT_ZOOM));

        mMapController = (MapController)mMapView.getController();
        mMapController.setZoom(MAP_DEFAULT_ZOOM);



        mImgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Construir la URL con los parámetros email y contraseña

                            String urlStr = configProperties.getProperty("url") + "/usuario/"+idCuidador;
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
                                String apellido= jsonResponse.getString("apellido");
                                String email=jsonResponse.getString("email");
                                String pass=jsonResponse.getString("password");
                                Boolean mCuidador = null;

                                // redirijo al home del usuario segun el rol
                                Intent homeIntent;
                                homeIntent = new Intent(PropiedadActivity.this, MenuCuidadorActivity.class);
                                homeIntent.putExtra("idUsuario",idUsuario);
                                homeIntent.putExtra("nombre",nombre);
                                homeIntent.putExtra("apellido", apellido);
                                homeIntent.putExtra("email", email);
                                homeIntent.putExtra("pass", pass);

                                startActivity(homeIntent);
                                finish();

                            }

                            urlConnection.disconnect();

                        } catch (Exception e) {
                            e.printStackTrace();
                            // Mostrar un mensaje de error genérico en caso de excepción
                            runOnUiThread(() -> {
                                Toast.makeText(PropiedadActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).start();
            }
        });



    }

    public void cargarDatosAlojamiento(Integer idCuidador) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = configProperties.getProperty("url") + "/alojamiento/" + idCuidador ;
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

                        JSONObject jsonResponse = new JSONObject(response);

                        int numero = jsonResponse.getInt("numero");
                       // Integer piso = jsonResponse.getInt("tipoAlojamiento");

                        if ( jsonResponse.isNull("piso")) {
                            Log.i("debug", "el piso es null "  );
                        } else {
                            Log.i("debug", "el piso es :  " + jsonResponse.getInt("piso") );
                            mPiso.setText(String.valueOf(jsonResponse.getInt("piso")));

                        }


                        mCalle.setText(jsonResponse.getString("calle"));
                        mNumero.setText(String.valueOf(numero));
                        mCP.setText(jsonResponse.getString("cp"));
                        mLocalidad.setText(jsonResponse.getString("localidad"));
                        mDepartamento.setText(jsonResponse.getString("departamento"));
                        mTipoVivienda.setSelection(adapter.getPosition(jsonResponse.getString("tipoAlojamiento")));
                        editar = true;


                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(PropiedadActivity.this, "No hay datos de propiedad", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "Error al cargar propiedad: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(PropiedadActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();


    }

    public boolean validarCampos () {

        boolean error = true;
        if (TextUtils.isEmpty(mCalle.getText().toString())) {
            Toast.makeText(PropiedadActivity.this, "El campo Calle es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }

        if (TextUtils.isEmpty(mNumero.getText().toString()) ) {
            Toast.makeText(PropiedadActivity.this, "El campo Numero es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }

        if (TextUtils.isEmpty(mLocalidad.getText().toString())) {
            Toast.makeText(PropiedadActivity.this, "El campo Localidad es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }

        if (TextUtils.isEmpty(mCP.getText().toString()) ) {
            Toast.makeText(PropiedadActivity.this, "El campo Codigo Postal es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }


        return error;

    }

    public void addMarker (GeoPoint center){
        Marker marker = new Marker(mMapView);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.ic_ubicacion_cuidador));

        // Cargo la localizacion para medir la distancia con cada complejo
        locationGPS.setLatitude(marker.getPosition().getLatitude());
        locationGPS.setLongitude(marker.getPosition().getLongitude());
        Log.i("debug", "marcador de gps: " + locationGPS.getLatitude() );

        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
        //marker.setTitle("usted esta aqui");

    }


    private class GetLocationTask extends AsyncTask<String, Void, LatLng> {
        @Override
        protected LatLng doInBackground(String... addresses) {
            String address = addresses[0];
            try {
                address = address.replaceAll(" ", "+");
                URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + address + "&format=json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Obtener la respuesta del servidor
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                // Parsear la respuesta JSON
                JSONArray jsonArray = new JSONArray(response.toString());
                if (jsonArray.length() > 0) {
                    JSONObject location = jsonArray.getJSONObject(0);

                    latitud = location.getDouble("lat");
                    longitud = location.getDouble("lon");
                    Log.i("debug", "latitud obtenida: " + latitud.toString());
                    Log.i("debug", "longitud obtenida: " + longitud.toString());
                    return new LatLng(latitud, longitud);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(LatLng result) {
            // Aquí puedes manejar el resultado, por ejemplo, actualizar la interfaz de usuario
            if (result != null) {
                // Haz algo con la ubicación
                Log.i("debug", "ejecuto algo en el if");
                GeoPoint center = new GeoPoint(latitud,longitud);
                mMapController.animateTo(center);
                addMarker(center);
               // guardarComplejoDespuesDeUbicacion();
            } else {
                // Maneja el caso de que no se pueda obtener la ubicación
                Log.i("debug", "ejecuto algo en el else");
                //Toast.makeText(NewComplejoActivity.this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void validarUbicacion() {
        // Obtener la dirección completa
        String direccionCompleta = mCalle.getText().toString() + " " + mNumero.getText().toString() + ", " + mLocalidad.getText().toString() + ", " + "Buenos Aires" + " " + mCP.getText().toString();
        Log.i("debug", "Direccion completa: " + direccionCompleta);
        // Obtener la ubicación a partir de la dirección
        new GetLocationTask().execute(direccionCompleta);
    }

}