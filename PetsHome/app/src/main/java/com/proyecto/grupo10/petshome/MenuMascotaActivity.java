package com.proyecto.grupo10.petshome;

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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
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
    ImageView mImgSinMascotas, mImgAtras;
    TextView mTvSinMascota;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    String nombre, edad, raza, especie, cuidadoEspecial;
    Integer idTutor, idMascota;
    List mascotas = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_mascostas);


        mFabMascota = findViewById(R.id.fab_add_mascota);
        mImgSinMascotas = findViewById(R.id.img_sin_mascotas);
        mTvSinMascota = findViewById(R.id.tv_mensaje_sin_mascota);
        mImgAtras = findViewById(R.id.img_atras);

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            idTutor = extras.getInt("idTutor");

        }

        // Cargar mascotas del tutor
        Log.i("debug", "tutor id en menuMascota: " + idTutor );
       // mascotas =
        listarMascotasdeTutor(idTutor, new MascotasCallback() {


            @Override
            public void onMascotasListReceived(List<Mascota> items) {

                Log.i("debug", "entro luego de cargar la lista con la cantidad: " + items.size());

                // Obtener el Recycler
                recycler = (RecyclerView) findViewById(R.id.reciclador);
                recycler.setHasFixedSize(true);

                // Usar un administrador para LinearLayout
                lManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(lManager);

                final MascotaAdapter adapter = new MascotaAdapter(items);

                recycler.setAdapter(adapter);

                // int resId = R.anim.layout_animation_rotate_in;
                int resId = R.anim.layout_animation;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(MenuMascotaActivity.this, resId);
                recycler.setLayoutAnimation(animation);
                adapter.notifyDataSetChanged();

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recycler);

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
                        finish();

                    }
                });

                if (items.isEmpty()) {
                    Log.i("debug", "La lista deberia estar vacia: " + items.size());
                    mImgSinMascotas.setVisibility(View.VISIBLE);
                    mTvSinMascota.setVisibility(View.VISIBLE);
                }
            }
        });


        mFabMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrarMascotaIntent = new Intent(MenuMascotaActivity.this, RegistrarMascotaActivity.class);
                registrarMascotaIntent.putExtra("idTutor", idTutor);

                startActivity(registrarMascotaIntent);

            }
        });



/*

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




        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycler);

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

*/

        mImgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuTutorIntent = new Intent(MenuMascotaActivity.this, MenuTutorActivity.class);
                menuTutorIntent.putExtra("idUsuario", idTutor);
                startActivity(menuTutorIntent);
                finish();
            }
        });

    }



    private void listarMascotasdeTutor(int idTutor,  MascotasCallback callback) {

       // List items = new ArrayList();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construir la URL con los parámetros email y contraseña
                    String urlStr = "https://api.petshome.com.ar/mascota/mascotas/" + idTutor ;
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

                            int fotoMascota = getResources().getIdentifier(especie.toLowerCase(), "drawable", getPackageName());

                            if (fotoMascota == 0) {
                                fotoMascota = getResources().getIdentifier("gato", "drawable", getPackageName());
                            }


                            Log.i("debug", "Mascota: " + nombre + ", Especie: " + especie + ", FotoID: " + fotoMascota);

                           // items.add(new Mascota(idMascota, nombre, especie, raza, edad.toString(), cuidadoEspecial, fotoMascota ));
                            mascotas.add(new Mascota(idMascota, nombre, especie, raza, edad.toString(), cuidadoEspecial, fotoMascota ));
                            // Llamar al callback con la lista de mascotas


                            //runOnUiThread(() -> callback.onMascotasListReceived(items));

                        }
                        if (mascotas.isEmpty()) {
                            Log.i("debug", "deberia mostar la imagen de sin mascota");
                            mImgSinMascotas.setVisibility(View.VISIBLE);
                            mTvSinMascota.setVisibility(View.VISIBLE);
                        }
                        runOnUiThread(() -> callback.onMascotasListReceived(mascotas));
                      /*  if (items.isEmpty()) {
                            mImgSinMascotas.setVisibility(View.VISIBLE);
                            mTvSinMascota.setVisibility(View.VISIBLE);
                        }
*/


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


      //  return items;

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int posicion = viewHolder.getAdapterPosition();
            Mascota mascotaAborrar= (Mascota) mascotas.get(posicion);
            switch (direction) {
                case ItemTouchHelper.LEFT:

                    confirmarEliminarMascota(mascotaAborrar.getIdMascota(), posicion);


                    Log.i("debug", "Se elimina de izquierda a derecha la mascota ID: " + mascotaAborrar.getIdMascota());

                case ItemTouchHelper.RIGHT:

                    confirmarEliminarMascota(mascotaAborrar.getIdMascota(), posicion);


                    Log.i("debug", "Se elimina de derecha a izquierdala mascota ID: " + mascotaAborrar.getIdMascota());

            }
        }
    };


    private void confirmarEliminarMascota (int idMascotaBorrar, int posicionLista) {

        AlertDialog.Builder dialogoConfirmacion = new AlertDialog.Builder(MenuMascotaActivity.this);
        dialogoConfirmacion.setTitle("BORRAR MASCOTA");

        dialogoConfirmacion.setMessage("Se borrara su mascota, ¿Desea continuar?");
        dialogoConfirmacion.setCancelable(false);
        dialogoConfirmacion.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                borrarMascota(idMascotaBorrar, posicionLista);
                Toast.makeText(MenuMascotaActivity.this, "MASCOTA BORRADA", Toast.LENGTH_SHORT).show();
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

    private void borrarMascota(int idABorrar, int posicionLista) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Elimina la mascota con el ID pasado por parametro
                    String urlStr = "https://api.petshome.com.ar/mascota/delete/" + idABorrar ;
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("DELETE");
                    urlConnection.setDoOutput(true);

                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Mascota eliminada
                        // Manejar error de autenticación
                        runOnUiThread(() -> {
                            Toast.makeText(MenuMascotaActivity.this, "Datos de la mascota borrados", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "se elimino la mascota con ID: " + idABorrar);
                       // mascotas.remove(posicionLista);
                        finish();
                        startActivity(getIntent());


                    } else {
                        // Error al eliminar la mascota
                        runOnUiThread(() -> {
                            Toast.makeText(MenuMascotaActivity.this, "No se pudo borrar el registro", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "error al borrar la mascota: " + responseCode);
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
    }

    public interface MascotasCallback {
        void onMascotasListReceived(List<Mascota> mascotas);
    }




}