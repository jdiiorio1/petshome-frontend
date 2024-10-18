package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MenuMascotaActivity extends AppCompatActivity {

    FloatingActionButton mFabMascota;
    ImageView mImgSinMascotas;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    String nombre, edad, raza, especie, cuidadoEspecial;
    Integer idTutor, idMascota;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_mascostas);


        mFabMascota = findViewById(R.id.fab_add_mascota);
        mImgSinMascotas = findViewById(R.id.img_sin_mascotas);

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            idTutor = extras.getInt("idTutor");

        }

        // Cargar mascotas del tutor
        List mascotas = listarMascotasdeTutor(idTutor);


        mFabMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrarMascotaIntent = new Intent(MenuMascotaActivity.this, RegistrarMascotaActivity.class);
                registrarMascotaIntent.putExtra("idTutor", idTutor);

                startActivity(registrarMascotaIntent);

            }
        });





        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lManager);

        final MascotaAdapter adapter = new MascotaAdapter(mascotas);

        recycler.setAdapter(adapter);

        // int resId = R.anim.layout_animation_rotate_in;
        int resId = R.anim.layout_animation;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(MenuMascotaActivity.this, resId);
        recycler.setLayoutAnimation(animation);
        adapter.notifyDataSetChanged();

        adapter.setOnClickListener(new MascotaAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Mascota model) {
                Intent mascotaIntent = new Intent(MenuMascotaActivity.this, RegistrarMascotaActivity.class);
                mascotaIntent.putExtra("nombre", model.getNombre());
                mascotaIntent.putExtra("edad", model.getEdad());
                mascotaIntent.putExtra("raza", model.getRaza());
                mascotaIntent.putExtra("especie", model.getEspecie());
                mascotaIntent.putExtra("cuidadoEspecial", model.getCuidadoEspecial());
                mascotaIntent.putExtra("idTutor", idTutor);
                mascotaIntent.putExtra("idMascota", model.getIdMascota());
                startActivity(mascotaIntent);

            }
        });



    }



    private List listarMascotasdeTutor(int idTutor) {

        List items = new ArrayList();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construir la URL con los parámetros email y contraseña
                    String urlStr = "http://192.168.1.35:8081/mascota/mascotas/" + idTutor ;
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
                        Log.i("debug", response);

                        // Procesar el JSON de respuesta
                        JSONArray jsonArray = new JSONArray(response); // Cambia a JSONArray
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i); // Obtener cada objeto JSON
                            idMascota = jsonObject.getInt("idMascota");
                            edad = jsonObject.getString("edad");
                            nombre = jsonObject.getString("nombre");
                            especie = jsonObject.getString("especie");
                            raza = jsonObject.getString("raza");
                            cuidadoEspecial = jsonObject.getString("cuidadoEspecial");


                            Log.i("debug", "Mascota: " + nombre + ", Especie: " + especie + ", Edad: " + edad);

                            items.add(new Mascota(idMascota, nombre, especie, raza, edad.toString(), cuidadoEspecial ));
                        }
                        if (items.isEmpty()) {
                            mImgSinMascotas.setVisibility(View.VISIBLE);
                        }



                    } else {
                        // Manejar error de autenticación
                        runOnUiThread(() -> {
                            Toast.makeText(MenuMascotaActivity.this, "No hay mascotas para mostrar", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("Login Error", "Error de inicio de sesión: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Mostrar un mensaje de error genérico en caso de excepción
                    runOnUiThread(() -> {
                        Toast.makeText(MenuMascotaActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();


        return items;

    }






}