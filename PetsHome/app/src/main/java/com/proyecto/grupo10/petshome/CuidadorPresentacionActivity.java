package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuidadorPresentacionActivity extends AppCompatActivity {

    TextView mNombre;
    ImageView mProfilePhoto;
    String nombreCuidador;
    Integer idCuidador, idTutor;

    private Button mContactar;
    List servicios = new ArrayList();
    List valoraciones = new ArrayList();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerServicios;

    private RecyclerView.LayoutManager lManagerServicios;
    private RecyclerView recyclerValoraciones;

    private RecyclerView.LayoutManager lManagerValoraciones;

    private RecyclerView recyclerComentarios;

    private RecyclerView.LayoutManager lManagerComentarios;
    Properties configProperties = new Properties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidador_presentacion);

        mNombre = findViewById(R.id.tv_nombre_cuidador);
        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mContactar = findViewById(R.id.btn_contactar);

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
            Log.i("debug", "el nombre del cuidador recibido es: " + extras.getString("nombre"));
            mNombre.setText(extras.getString("nombre").toUpperCase() + " " + extras.getString("apellido").toUpperCase());
            idCuidador = extras.getInt("idCuidador");
            idTutor = extras.getInt("idTutor");


        }
        cargarFotoSiExiste(idCuidador);

        listarServiciosCuidador(idCuidador, new ServiciosActivity.ServiciosCallback(){

            @Override
            public void onServiciosListReceived(List<Servicio> items) {
                recyclerServicios = (RecyclerView) findViewById(R.id.reciclador_servicios);
                recyclerServicios.setHasFixedSize(true);
                lManagerServicios = new LinearLayoutManager(getApplicationContext());
                recyclerServicios.setLayoutManager(lManagerServicios);

                final ServicioAdapter adapter = new ServicioAdapter(items);
                recyclerServicios.setAdapter(adapter);

                int resId=R.anim.layout_animation;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(CuidadorPresentacionActivity.this, resId);
                recyclerServicios.setLayoutAnimation(animation);
                adapter.notifyDataSetChanged();



            }
        });

        listarValoracionesDelCuidador(idCuidador, new ValoracionesCuidadorActivity.ValoracionesCallback() {

            public void onValoracionesListReceived(List<Valoracion> valoracion) {
                Log.i("debug", "entro luego de cargar la lista con la cantidad: " + valoracion.size());

                // Obtener el Recycler
                recyclerValoraciones = (RecyclerView) findViewById(R.id.reciclador_comentarios);
                recyclerValoraciones.setHasFixedSize(true);

                // Usar un administrador para LinearLayout
                lManagerValoraciones = new LinearLayoutManager(getApplicationContext());
                recyclerValoraciones.setLayoutManager(lManagerValoraciones);

                adapter = new ValoracionAdapter( CuidadorPresentacionActivity.this, valoraciones);

                recyclerValoraciones.setAdapter(adapter);

                // int resId = R.anim.layout_animation_rotate_in;
                int resId = R.anim.layout_animation;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(CuidadorPresentacionActivity.this, resId);
                recyclerValoraciones.setLayoutAnimation(animation);

            }

        }) ;

        mContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CuidadorPresentacionActivity.this, AgendarCitaActivity.class);
                intent.putExtra("idCuidador", idCuidador);
                intent.putExtra("idTutor", idTutor);
                startActivity(intent);
                finish();
            }
        });


    }


    private void cargarFotoSiExiste(Integer idUsuario) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = configProperties.getProperty("url") + "/usuario/imagen/" + idUsuario;
                    Log.i("debug", "URL Imagen: " + urlStr);
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int responseCode = urlConnection.getResponseCode();
                    Log.i("debug", "El response code de la imagen es : " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        Log.i("debug", "Entre al body porque cargo la imagen");
                        InputStream imageInputStream = urlConnection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream);

                        // Muestro la imagen

                        mProfilePhoto.setImageBitmap(bitmap);

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(CuidadorPresentacionActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "Error al cargar la imagen: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(CuidadorPresentacionActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void listarServiciosCuidador (int idCuidador, ServiciosActivity.ServiciosCallback callback){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            try {
                String urlStr = configProperties.getProperty("url") + "/servicio/servicios/" + idCuidador;
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                int reponseCode = urlConnection.getResponseCode();

                if (reponseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    String response = result.toString();

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int idServicio = jsonObject.getInt("idServicio");
                        String tipoServicio = jsonObject.getString("tipoServicio");
                        String descripcion = jsonObject.getString("descripcion");

                        servicios.add(new Servicio(idServicio, descripcion, tipoServicio));
                    }


                    runOnUiThread(() -> callback.onServiciosListReceived(servicios));
                } else {
                    runOnUiThread(() -> {

                        Log.i("debug", "no hay servicios para mostrar");
                    });
                }

                urlConnection.disconnect();

            } catch (Exception e){
                e.printStackTrace();
                runOnUiThread(() -> {
                    Log.i("debug", "Error al conectar con el servidor");

                });
            }
        });
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
                       // adapter.notifyDataSetChanged();
                       // recyclerValoraciones.requestLayout();


                    });
                    Log.e("debug", "no hay datos para mostrar: " + responseCode);
                    valoraciones.clear();
                   // adapter.notifyDataSetChanged();
                   // recyclerValoraciones.requestLayout();

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

        Intent buscarCuidadorIntent = new Intent(CuidadorPresentacionActivity.this, BuscarCuidadorActivity.class);
        buscarCuidadorIntent.putExtra("idUsuario", idTutor);
        startActivity(buscarCuidadorIntent);
        finish();

    }


}