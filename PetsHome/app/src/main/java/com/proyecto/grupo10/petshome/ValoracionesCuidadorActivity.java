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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ValoracionesCuidadorActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    Integer idCuidador;
    String nombre, apellido, email, pass;
    Uri fotoPerfil;
    List valoraciones = new ArrayList();
    Properties configProperties = new Properties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoraciones_cuidador);

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



        listarValoracionesDelCuidador(idCuidador, new ValoracionesCuidadorActivity.ValoracionesCallback() {

            public void onValoracionesListReceived(List<Valoracion> valoracion) {
                Log.i("debug", "entro luego de cargar la lista con la cantidad: " + valoracion.size());

                // Obtener el Recycler
                recycler = (RecyclerView) findViewById(R.id.reciclador_valoraciones_cuidador);
                recycler.setHasFixedSize(true);

                // Usar un administrador para LinearLayout
                lManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(lManager);

                adapter = new ValoracionAdapter( ValoracionesCuidadorActivity.this, valoraciones);

                recycler.setAdapter(adapter);

                // int resId = R.anim.layout_animation_rotate_in;
                int resId = R.anim.layout_animation;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(ValoracionesCuidadorActivity.this, resId);
                recycler.setLayoutAnimation(animation);



            }

        }) ;


    }

    private void listarValoracionesDelCuidador(int idCuidador, ValoracionesCuidadorActivity.ValoracionesCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String urlStr = "";
                    urlStr = configProperties.getProperty("url") + "/cita/cardview/valoraciones/" + idCuidador;


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
                        int idTutor = jsonObject.getInt("idUsuario");
                        String nombreMascota = jsonObject.getString("nombreMascota");
                        String nombreUsuario = jsonObject.getString("nombreUsuario");
                        String apellido = jsonObject.getString("apellido");
                        String comentario = jsonObject.getString("comentario");
                        int puntuacion = jsonObject.getInt("puntuacion");
                        String fechaFin = jsonObject.getString("fechaFin");
                        // String estado = jsonObject.getString("estado");


                        int fotoTutor = getResources().getIdentifier("cuidador1", "drawable", getPackageName());


                            valoraciones.add(new Valoracion(idTutor, nombreUsuario, nombreMascota, apellido, puntuacion, fechaFin, comentario, fotoTutor, configProperties.getProperty("url")));



                    }

                    runOnUiThread(() -> callback.onValoracionesListReceived(valoraciones));

                } else {
                    runOnUiThread(() -> {
                        valoraciones.clear();
                        adapter.notifyDataSetChanged();
                        recycler.requestLayout();


                    });
                    Log.e("debug", "no hay datos para mostrar: " + responseCode);
                    valoraciones.clear();
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

    public interface ValoracionesCallback {
        void onValoracionesListReceived(List<Valoracion> valoraciones);
    }

    @Override
    public void onBackPressed() {

        Intent homeIntent = new Intent(ValoracionesCuidadorActivity.this, MenuCuidadorActivity.class);
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