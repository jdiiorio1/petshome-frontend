package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.applandeo.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistorialCitasCuidadorActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    Integer idCuidador;
    String nombre, apellido, email, pass;
    Uri fotoPerfil;
    List citas = new ArrayList();
    Properties configProperties = new Properties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_citas_cuidador);

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
            idCuidador = extras.getInt("idUsuario");
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");

        }

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador_historial_cuidador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lManager);

        adapter = new CitaAdapter(HistorialCitasCuidadorActivity.this, citas);

        recycler.setAdapter(adapter);

        // int resId = R.anim.layout_animation_rotate_in;
        int resId = R.anim.layout_animation;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(HistorialCitasCuidadorActivity.this, resId);
        recycler.setLayoutAnimation(animation);

        listarCitasDelCuidador(idCuidador, "", new HistorialCitasCuidadorActivity.CitasCallback() {

            public void onCitasListReceived(List<Cita> citas) {
                Log.i("debug", "entro luego de cargar la lista con la cantidad: " + citas.size());



            }

        }) ;

    }

    private void listarCitasDelCuidador(int idCuidador, String fechaclick, HistorialCitasCuidadorActivity.CitasCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String urlStr = "";
                if (fechaclick.isEmpty()) {
                    urlStr = configProperties.getProperty("url") + "/cita/cardview/all/" + idCuidador;
                } else {
                    urlStr = configProperties.getProperty("url") + "/cita/cardview/" + idCuidador + "?fecha=" + fechaclick;
                }

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
                        int idMascota = jsonObject.getInt("idMascota");
                        String nombreMascota = jsonObject.getString("nombreMascota");
                        String nombreUsuario = jsonObject.getString("nombreUsuario");
                        String cuidadoEspecial = jsonObject.getString("cuidadoEspecial");
                        int edad = jsonObject.getInt("idUsuario");
                        String fechaInicio = jsonObject.getString("fechaInicio");
                        String fechaFin = jsonObject.getString("fechaFin");
                        // String estado = jsonObject.getString("estado");


                        int fotoMascota = getResources().getIdentifier("gato", "drawable", getPackageName());

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        LocalDate hoy = LocalDate.now();
                        Log.i("debug", "La fecha actual es:" + hoy.toString());

                        if (LocalDate.parse(fechaFin).isBefore(hoy)) {
                            Log.i("debug", "La fecha fin es menor a la de hoy");

                            citas.add(new Cita(idCuidador, nombreUsuario, nombreMascota, LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin), idMascota, cuidadoEspecial, fotoMascota, configProperties.getProperty("url")));

                        }

                    }

                    runOnUiThread(() -> callback.onCitasListReceived(citas));

                } else {
                    runOnUiThread(() -> {
                        citas.clear();
                        adapter.notifyDataSetChanged();
                        recycler.requestLayout();


                    });
                    Log.e("debug", "no hay datos para mostrar: " + responseCode);
                    citas.clear();
                    adapter.notifyDataSetChanged();
                    recycler.requestLayout();

                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {

                });
            }
        });

        // Shutdown the executor if no longer needed (optional)
        // executor.shutdown();
    }

    public interface CitasCallback {
        void onCitasListReceived(List<Cita> citas);
    }

    @Override
    public void onBackPressed() {

        Intent homeIntent = new Intent(HistorialCitasCuidadorActivity.this, MenuCuidadorActivity.class);
        homeIntent.putExtra("idUsuario", idCuidador);
        homeIntent.putExtra("nombre", nombre);
        homeIntent.putExtra("apellido", apellido);
        homeIntent.putExtra("email", email);
        homeIntent.putExtra("pass", pass);
        homeIntent.putExtra("esCuidador", true);
        startActivity(homeIntent);
        finish();

    }


}