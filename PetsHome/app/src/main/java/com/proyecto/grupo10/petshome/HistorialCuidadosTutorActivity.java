package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistorialCuidadosTutorActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    Integer idTutor;
    String nombre, apellido, email, pass;
    Uri fotoPerfil;
    List citas = new ArrayList();
    Properties configProperties = new Properties();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_cuidados_tutor);

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
            idTutor = extras.getInt("idTutor");
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");

        }

        listarHistorialCuidados(idTutor, new HistorialCallback() {


            @Override
            public void onHistorialListReceived(List<HistorialCitaTutor> items) {

                Log.i("debug", "entro luego de cargar la lista con la cantidad: " + items.size());

                // Obtener el Recycler
                recycler = (RecyclerView) findViewById(R.id.reciclador_historial_tutor);
                recycler.setHasFixedSize(true);

                // Usar un administrador para LinearLayout
                lManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(lManager);

                final HistorialCitasTutorAdapter adapter = new HistorialCitasTutorAdapter(items);

                recycler.setAdapter(adapter);

                // int resId = R.anim.layout_animation_rotate_in;
                int resId = R.anim.layout_animation;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(HistorialCuidadosTutorActivity.this, resId);
                recycler.setLayoutAnimation(animation);
                adapter.notifyDataSetChanged();

                adapter.setOnClickListener(new HistorialCitasTutorAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, HistorialCitaTutor model) {

                        showRatingPopup(model.idCita, idTutor);
                        /*
                        Intent valorarIntent = new Intent(HistorialCuidadosTutorActivity.this, EnConstruccionActivity.class);
                        startActivity(valorarIntent);
                        finish();*/
                    }
                });



/*
                if (items.isEmpty()) {
                    Log.i("debug", "La lista deberia estar vacia: " + items.size());
                    mImgSinMascotas.setVisibility(View.VISIBLE);
                    mTvSinMascota.setVisibility(View.VISIBLE);
                }
*/

            }
        });



    }


    private void listarHistorialCuidados(int idTutor, HistorialCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String urlStr = configProperties.getProperty("url") + "/cita/cardview/tutor/" + idTutor;
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
                        int idCita = jsonObject.getInt("idCita");
                        String nombreCuidador = jsonObject.getString("nombreCuidador");
                        String fechaInicio = jsonObject.getString("fechaInicio");
                        String fechaFin = jsonObject.getString("fechaFin");
                        int idCuidador = jsonObject.getInt("idCuidador");


                        int fotoCuidador = getResources().getIdentifier("cuidador1", "drawable", getPackageName());

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        LocalDate hoy = LocalDate.now();
                        Log.i("debug", "La fecha actual es:" + hoy.toString());

                        if (LocalDate.parse(fechaFin).isBefore(hoy)) {
                            Log.i("debug", "La fecha fin es menor a la de hoy");
                            Log.i("debug", "Mascota: " + nombreMascota + ", Cuidador: " + nombreCuidador + ", FotoID: " + fotoCuidador);
                            citas.add(new HistorialCitaTutor(idCita, nombreCuidador, nombreMascota, LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin), idMascota, fotoCuidador, configProperties.getProperty("url"), idCuidador));

                        }





                    }
/*
                    if (citas.isEmpty()) {
                        Log.i("debug", "deberia mostar la imagen de sin mascota");
                        runOnUiThread(() -> {
                            mImgSinMascotas.setVisibility(View.VISIBLE);
                            mTvSinMascota.setVisibility(View.VISIBLE);
                        });
                    }
*/
                    runOnUiThread(() -> callback.onHistorialListReceived(citas));

                } else {
                    runOnUiThread(() -> {
                        Log.e("Login Error", "No hay citas para mostrar");
                    });
                    Log.e("Login Error", "Error de inicio de sesión: " + responseCode);
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Log.e("Login Error", "Error al conectar con el servidor");

                });
            }
        });

        // Shutdown the executor if no longer needed (optional)
        // executor.shutdown();
    }


    public interface HistorialCallback {
        void onHistorialListReceived(List<HistorialCitaTutor> historialCitas);
    }


    public void showRatingPopup(int idCita, int idTutor) {

        cargarValoracion(idCita);


    }




    public void cargarValoracion(Integer idCita) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String urlStr = configProperties.getProperty("url") + "/valoracion/" + idCita;
                Log.i("debug", "URL: " + urlStr);
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();

                Log.i("debug", "responseCode: " + responseCode);
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


                    JSONObject  jsonResponse = new JSONObject(response);
                    runOnUiThread(() -> {
                        try {

                            if (jsonResponse.has("puntuacion") )
                                Log.i("debug", "puntuacion cargada: " + jsonResponse.getString("puntuacion") + " Comentario obtenido: " + jsonResponse.getString("comentario"));
                            AlertDialog.Builder builder = new AlertDialog.Builder(HistorialCuidadosTutorActivity.this);
                            builder.setTitle("Valoracion otorgada")
                                    .setMessage(Html.fromHtml( jsonResponse.getString("comentario") + "<br><br> Puntuacion: " + jsonResponse.getString("puntuacion")))
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); // Cerrar el diálogo
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel(); // Cerrar el diálogo
                                        }
                                    });

                            // Crear y mostrar el diálogo
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                } else {
                    runOnUiThread(() -> {
                        Log.e("debug", "No hay valoracion");
                        /**
                         * llamo al popup para acrgar la valoracion
                         *
                         */
                        // Crear un nuevo diálogo
                        final Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.pop_up_valoracion);
                        dialog.setTitle("Valora la cita");

                        // Obtener referencias a los elementos del layout
                        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
                        EditText editTextComment = dialog.findViewById(R.id.editTextComment);
                        Button buttonSubmit = dialog.findViewById(R.id.buttonSubmit);

                        // Configurar el RatingBar
                        ratingBar.setNumStars(5);
                        ratingBar.setStepSize(1);
                        ratingBar.setRating(0);


                        // Configurar el botón de enviar
                        buttonSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                float rating = ratingBar.getRating();
                                String comment = editTextComment.getText().toString();

                                Log.i("debug", "La valoración que le puse es:" + rating);
                                Log.i("debug", "El comentario que le puse es:" + comment);

                                new Thread(() -> {
                                    try {
                                        String metodo = "";
                                        URL url;

                                        url = new URL(configProperties.getProperty("url") + "/valoracion");
                                        metodo = "POST";


                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestMethod(metodo);
                                        Log.i("debug",conn.getRequestMethod().toString());
                                        conn.setRequestProperty("Content-Type", "application/json; utf-8");
                                        conn.setRequestProperty("Accept", "application/json");
                                        conn.setDoOutput(true);

                                        Log.i("debug", "se valora con los datos idCita: " + idCita + " idTutor: " + idTutor + " puntuacion: " + Math.round(rating) + " comentario: " + comment);
                                        JSONObject json = new JSONObject();
                                        json.put("idCita", idCita);
                                        json.put("comentario", comment);
                                        json.put("puntuacion", Math.round(rating));
                                        json.put("idTutor", idTutor);




                                        try (OutputStream os = conn.getOutputStream()) {
                                            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                                            os.write(input, 0, input.length);
                                        }

                                        int responseCode = conn.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            runOnUiThread(() -> {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(HistorialCuidadosTutorActivity.this);
                                                builder.setMessage("Se guardaron los cambios.")
                                                        .setCancelable(false)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {

                                                                Log.i("debug", "cerro la puntuacion");

                                                            }
                                                        });
                                                AlertDialog alert = builder.create();
                                                alert.show();
                                            });
                                        } else {
                                            runOnUiThread(() -> {
                                                Toast.makeText(HistorialCuidadosTutorActivity.this, "Error en el registro.", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    } catch (Exception e) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(HistorialCuidadosTutorActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }).start();




                                dialog.dismiss(); // Cerrar el diálogo
                            }
                        });

                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.show(); // Mostrar el diálogo



                    });
                    Log.e("debug", "Error al cargar la valoracion: " + responseCode);
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Log.e("debug", "Error al cargar datos del servidor");
                });
            }
        });

        // Shutdown the executor if no longer needed (optional)
        // executor.shutdown();
    }

    public void onBackPressed() {

        Intent homeIntent = new Intent(HistorialCuidadosTutorActivity.this, MenuTutorActivity.class);
        homeIntent.putExtra("idUsuario", idTutor);
        homeIntent.putExtra("nombre", nombre);
        homeIntent.putExtra("apellido", apellido);
        homeIntent.putExtra("email", email);
        homeIntent.putExtra("pass", pass);
        homeIntent.putExtra("esCuidador", false);
        startActivity(homeIntent);
        finish();

    }


}