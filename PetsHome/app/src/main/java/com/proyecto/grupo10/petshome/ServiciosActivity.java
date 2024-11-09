package com.proyecto.grupo10.petshome;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class ServiciosActivity extends AppCompatActivity {

    FloatingActionButton mFabServicio;

    ImageView imgSinServicio, imgAtras;

    TextView mTvSinServicio;

    private RecyclerView recycler;

    private RecyclerView.LayoutManager lManager;

    Integer idCuidador;

    List servicios = new ArrayList();

    Properties configProperties = new Properties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        try {
            InputStream inputStream = this.getAssets().open("config.properties");
            configProperties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mFabServicio = findViewById(R.id.fab_add_servicio);
        imgSinServicio = findViewById(R.id.img_sin_servicios);
        mTvSinServicio=findViewById(R.id.tv_mensaje_sin_servicio);
        imgAtras = findViewById(R.id.img_atras);

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            idCuidador = extras.getInt("idCuidador");
        }

        listarServiciosCuidador(idCuidador, new ServiciosCallback(){

            @Override
            public void onServiciosListReceived(List<Servicio> items) {
                recycler = (RecyclerView) findViewById(R.id.reciclador);
                recycler.setHasFixedSize(true);
                lManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(lManager);

                final ServicioAdapter adapter = new ServicioAdapter(items);
                recycler.setAdapter(adapter);

                int resId=R.anim.layout_animation;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(ServiciosActivity.this, resId);
                recycler.setLayoutAnimation(animation);
                adapter.notifyDataSetChanged();

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recycler);

                adapter.setOnClickListener(new ServicioAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, Servicio model) {
                        Intent servicioIntent = new Intent(ServiciosActivity.this, RegistrarServicioActivity.class);
                        servicioIntent.putExtra("descripcion",model.getDescripcion());
                        servicioIntent.putExtra("tipoServicio",model.getTipoServicio());
                        servicioIntent.putExtra("idCuidador", idCuidador);
                        servicioIntent.putExtra("idServicio", model.getIdServicio());
                        startActivity(servicioIntent);
                        finish();
                    }
                });

                if (items.isEmpty()){
                    imgSinServicio.setVisibility(View.VISIBLE);
                    mTvSinServicio.setVisibility(View.VISIBLE);
                }

            }
        });

        mFabServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrarServicioIntent =  new Intent(ServiciosActivity.this, RegistrarServicioActivity.class);
                registrarServicioIntent.putExtra("idCuidador", idCuidador);
                startActivity(registrarServicioIntent);
            }
        });

        imgAtras.setOnClickListener(new View.OnClickListener() {
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
                                homeIntent = new Intent(ServiciosActivity.this, MenuCuidadorActivity.class);
                                homeIntent.putExtra("idUsuario",idUsuario);
                                homeIntent.putExtra("nombre",nombre);
                                homeIntent.putExtra("apellido", apellido);
                                homeIntent.putExtra("email", email);
                                homeIntent.putExtra("pass", pass);

                                startActivity(homeIntent);


                            }

                            urlConnection.disconnect();

                        } catch (Exception e) {
                            e.printStackTrace();
                            // Mostrar un mensaje de error genérico en caso de excepción
                            runOnUiThread(() -> {
                                Toast.makeText(ServiciosActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).start();
            }
        });

    }

    private void listarServiciosCuidador (int idCuidador, ServiciosCallback callback){
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

                    if (servicios.isEmpty()){
                        runOnUiThread(() -> {
                            imgSinServicio.setVisibility(View.VISIBLE);
                            mTvSinServicio.setVisibility(View.VISIBLE);
                        });
                    }

                    runOnUiThread(() -> callback.onServiciosListReceived(servicios));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ServiciosActivity.this, "No hay servicios para mostrar", Toast.LENGTH_SHORT).show();
                    });
                }

                urlConnection.disconnect();

            } catch (Exception e){
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ServiciosActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int posicion = viewHolder.getAdapterPosition();
            Servicio servicioAborrar= (Servicio) servicios.get(posicion);
            switch (direction) {
                case ItemTouchHelper.LEFT:

                    confirmarEliminarServicio(servicioAborrar.getIdServicio(), posicion);


                    Log.i("debug", "Se elimina de izquierda a derecha la mascota ID: " + servicioAborrar.getIdServicio());

                case ItemTouchHelper.RIGHT:

                    confirmarEliminarServicio(servicioAborrar.getIdServicio(), posicion);


                    Log.i("debug", "Se elimina de derecha a izquierdala mascota ID: " + servicioAborrar.getIdServicio());

            }
        }
    };

    private void confirmarEliminarServicio (int idServicioBorrar, int posicionLista) {

        AlertDialog.Builder dialogoConfirmacion = new AlertDialog.Builder(ServiciosActivity.this);
        dialogoConfirmacion.setTitle("BORRAR SERVICIO");

        dialogoConfirmacion.setMessage("Se borrara su servicio, ¿Desea continuar?");
        dialogoConfirmacion.setCancelable(false);
        dialogoConfirmacion.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                borrarServicio(idServicioBorrar, posicionLista);
                Toast.makeText(ServiciosActivity.this, "SERVICIO BORRADA", Toast.LENGTH_SHORT).show();
            }
        });
        dialogoConfirmacion.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                startActivity(getIntent());

            }
        });
        AlertDialog alertDialog = dialogoConfirmacion.create();
        alertDialog.show();
    }

    private void borrarServicio(int idABorrar, int posicionLista) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Elimina la mascota con el ID pasado por parametro
                    String urlStr = configProperties.getProperty("url") + "/servicio/delete/" + idABorrar ;
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("DELETE");
                    urlConnection.setDoOutput(true);

                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Mascota eliminada
                        // Manejar error de autenticación
                        runOnUiThread(() -> {
                            Toast.makeText(ServiciosActivity.this, "Datos del servicio borrados", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "se elimino el servicio con ID: " + idABorrar);
                        // mascotas.remove(posicionLista);
                        finish();
                        startActivity(getIntent());


                    } else {
                        // Error al eliminar la mascota
                        runOnUiThread(() -> {
                            Toast.makeText(ServiciosActivity.this, "No se pudo borrar el registro", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "error al borrar el servicio: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Mostrar un mensaje de error genérico en caso de excepción
                    runOnUiThread(() -> {
                        Toast.makeText(ServiciosActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    public  interface ServiciosCallback{
        void onServiciosListReceived(List<Servicio> servicios);
    }
}