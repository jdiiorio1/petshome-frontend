package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class MenuCuidadorActivity extends AppCompatActivity {

    CardView mMisServicios;
    CardView mAgenda;
    CardView mHistorialCuidados;
    CardView mMensajes;

    CardView mPropiedad;
    CardView mValoraciones;

    ImageView mEditarPerfil;
    ImageView mImgBackground;
    TextView mSaludo;

    String nombre, apellido, email, pass, calle, numero, cp, localidad, departamento, piso, tipoVivienda;
    Integer idUsuario;
    Properties configProperties = new Properties();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cuidador);

        /***
         * CARGO ARCHIVO PROPERTIES CON LA IP DE CADA UNO
         */

        try {
            InputStream inputStream = this.getAssets().open("config.properties");
            configProperties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");
            idUsuario = extras.getInt("idUsuario");


            calle = extras.getString("calle");
            numero = extras.getString("numero");
            cp = extras.getString("cp");
            localidad = extras.getString("localidad");
            departamento = extras.getString("departamento");
            piso = extras.getString("piso");
            tipoVivienda = extras.getString("tipoVivienda");


        }

        mSaludo = findViewById(R.id.tv_saludo);
        mSaludo.setText("Hola " + nombre + " " + apellido);

        mMisServicios = findViewById(R.id.cv_mis_servicios);
        mAgenda = findViewById(R.id.cv_agenda);
        mHistorialCuidados = findViewById(R.id.cv_historial);
        mMensajes = findViewById(R.id.cv_mensajes);

        mPropiedad = findViewById(R.id.cv_propiedad);
        mValoraciones = findViewById(R.id.cv_valoraciones);
        mEditarPerfil = findViewById(R.id.img_editar_perfil);
        mImgBackground = findViewById(R.id.img_background);
        mImgBackground.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in));
        /**
         * Obtengo la imagen si esta cargada
         */
        cargarFotoSiExiste(idUsuario);

        mMisServicios.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));





        mMisServicios.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMisServicios.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mAgenda.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mAgenda.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAgenda.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);


        mHistorialCuidados.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mHistorialCuidados.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHistorialCuidados.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mMensajes.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mMensajes.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMensajes.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mPropiedad.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mPropiedad.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPropiedad.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mValoraciones.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mValoraciones.postDelayed(new Runnable() {
            @Override
            public void run() {
                mValoraciones.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mPropiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent propiedadIntent = new Intent(MenuCuidadorActivity.this, PropiedadActivity.class);
                propiedadIntent.putExtra("idCuidador", idUsuario);
                propiedadIntent.putExtra("nombre", nombre);
                propiedadIntent.putExtra("apellido", apellido);
                propiedadIntent.putExtra("pass", pass);
                propiedadIntent.putExtra("email", email);
                propiedadIntent.putExtra("esCuidador", true);


                startActivity(propiedadIntent);
            }
        });

        mMisServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviciosIntent = new Intent(MenuCuidadorActivity.this, ServiciosActivity.class);
                serviciosIntent.putExtra("idCuidador",idUsuario);
                serviciosIntent.putExtra("nombre", nombre);
                serviciosIntent.putExtra("apellido", apellido);
                serviciosIntent.putExtra("pass", pass);
                serviciosIntent.putExtra("email", email);
                serviciosIntent.putExtra("esCuidador", true);
                startActivity(serviciosIntent);
            }
        });

        mEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent perfilIntent = new Intent(MenuCuidadorActivity.this, EditarUsuarioActivity.class);
                perfilIntent.putExtra("idUsuario", idUsuario);
                perfilIntent.putExtra("nombre", nombre);
                perfilIntent.putExtra("apellido", apellido);
                perfilIntent.putExtra("email", email);
                perfilIntent.putExtra("pass", pass);
                perfilIntent.putExtra("esCuidador", true);

                startActivity(perfilIntent);
            }
        });

        mAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCuidadorActivity.this, AgendaActivity.class);
                intent.putExtra("idCuidador", idUsuario);
                intent.putExtra("idUsuario", idUsuario);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido", apellido);
                intent.putExtra("email", email);
                intent.putExtra("pass", pass);
                startActivity(intent);
                finish();
            }
        });

        mHistorialCuidados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCuidadorActivity.this, HistorialCitasCuidadorActivity.class);
                intent.putExtra("idCuidador", idUsuario);
                intent.putExtra("idUsuario", idUsuario);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido", apellido);
                intent.putExtra("email", email);
                intent.putExtra("pass", pass);
                startActivity(intent);
                finish();
            }
        });

        mMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCuidadorActivity.this, EnConstruccionActivity.class);
                startActivity(intent);
            }
        });

        mValoraciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCuidadorActivity.this, ValoracionesCuidadorActivity.class);
                intent.putExtra("idCuidador", idUsuario);
                intent.putExtra("idUsuario", idUsuario);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido", apellido);
                intent.putExtra("email", email);
                intent.putExtra("pass", pass);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Está seguro de que quiere cerrar su sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    super.onBackPressed();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("CLEAR_FIELDS", true);  // Indica que se deben limpiar los campos
                    startActivity(intent);
                    finish(); // Finaliza la actividad actual para que MainActivity sea una nueva instancia
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
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

                        mImgBackground.setImageBitmap(bitmap);

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(MenuCuidadorActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "Error al cargar la imagen: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(MenuCuidadorActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }


}