package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RegistrarServicioActivity extends AppCompatActivity {

    ImageView mProfilePhoto, mImgAtras;
    EditText mDescripcion;
    Spinner mTipoServicio;

    TextView mTituloPantalla;
    Button mTextBoton, mBtnBorrarServicio;
    Boolean editar = false;
    Integer idCuidador, idServicio;
    Properties configProperties = new Properties();

    String tipoServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_servicio);

        try {
            InputStream inputStream = this.getAssets().open("config.properties");
            configProperties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mDescripcion= findViewById(R.id.et_descripcion);
        mTipoServicio=findViewById(R.id.tipoServicio);
        mTituloPantalla = findViewById(R.id.tv_titulo_editar_servicio);
        mTextBoton = findViewById(R.id.btn_registrar_servicio);
        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mBtnBorrarServicio = findViewById(R.id.btn_borrar_servicio);
        mImgAtras = findViewById(R.id.img_atras);

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tipo_especie,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        mTipoServicio.setAdapter(adapter);

        mImgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mascotaIntent = new Intent(RegistrarServicioActivity.this, ServiciosActivity.class);
                mascotaIntent.putExtra("idCuidador", idCuidador);
                startActivity(mascotaIntent);
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            mTipoServicio.setSelection(adapter.getPosition(extras.getString("tipoServicio")));
            mDescripcion.setText(extras.getString("descripcion"));
            idServicio = extras.getInt("idServicio");
            idCuidador = extras.getInt("idCuidador");
            if (!mTipoServicio.toString().isEmpty()) {
                mTextBoton.setText("GUARDAR CAMBIOS");
                mTituloPantalla.setText(mTipoServicio.getTooltipText().toString());
                mBtnBorrarServicio.setVisibility(View.VISIBLE);
                editar = true;
            }
        }

        mTipoServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipoServicio = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                mTextBoton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new Thread(() -> {
                            try {
                                String metodo = "";
                                URL url;
                                if (editar) {
                                    url = new URL(configProperties.getProperty("url") + "/servicio/" + idServicio);
                                    metodo = "PUT";
                                    Log.i("debug", "editando servicio");
                                    Log.i("debug", url.toString());
                                } else {
                                    url = new URL(configProperties.getProperty("url") + "/servicio");
                                    metodo = "POST";
                                }

                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod(metodo);
                                Log.i("debug", conn.getRequestMethod().toString());
                                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                                conn.setRequestProperty("Accept", "application/json");
                                conn.setDoOutput(true);

                                JSONObject json = new JSONObject();
                                json.put("tipoServicio", tipoServicio);
                                json.put("descripcion", mDescripcion);
                                json.put("idCuidador", idCuidador);

                                try (OutputStream os = conn.getOutputStream()) {
                                    byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                                    os.write(input, 0, input.length);
                                }

                                int responseCode = conn.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    runOnUiThread(() -> {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarServicioActivity.this);
                                        builder.setMessage("Se guardaron los cambios.")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent servicioIntent = new Intent(RegistrarServicioActivity.this, MenuMascotaActivity.class);
                                                        Log.i("debug", "id Cuidador en Registrar luego de crear o modificar: " + idCuidador);
                                                        servicioIntent.putExtra("idCuidador", idCuidador);
                                                        startActivity(servicioIntent);
                                                        finish(); // Cerrar la actividad
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        Toast.makeText(RegistrarServicioActivity.this, "Error en el registro.", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(RegistrarServicioActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }).start();


                    }
                });


                mBtnBorrarServicio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialogoConfirmacion = new AlertDialog.Builder(RegistrarServicioActivity.this);
                        dialogoConfirmacion.setTitle("BORRAR Servicio");

                        dialogoConfirmacion.setMessage("¿Seguro desea borrar el registro del servicio");
                        dialogoConfirmacion.setCancelable(false);
                        dialogoConfirmacion.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                borrarServicio();
                                Toast.makeText(RegistrarServicioActivity.this, "SERVICIO BORRADA", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogoConfirmacion.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alertDialog = dialogoConfirmacion.create();
                        alertDialog.show();
                    }
                });

            }
        });
    }

            private void borrarServicio() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Construir la URL con los parámetros email y contraseña
                            String urlStr = configProperties.getProperty("url") + "/servicio/delete/" + idCuidador ;
                            URL url = new URL(urlStr);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("DELETE");
                            urlConnection.setDoOutput(true);

                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                // Mascota eliminada
                                // Manejar error de autenticación
                                runOnUiThread(() -> {
                                    Toast.makeText(RegistrarServicioActivity.this, "Datos del servicio borrados", Toast.LENGTH_SHORT).show();
                                });

                                Intent mascotaIntent = new Intent(RegistrarServicioActivity.this, MenuMascotaActivity.class);
                                mascotaIntent.putExtra("idCuidador", idCuidador);
                                startActivity(mascotaIntent);
                                finish(); // Cerrar la actividad


                            } else {
                                // Error al eliminar la mascota
                                runOnUiThread(() -> {
                                    Toast.makeText(RegistrarServicioActivity.this, "No se pudo borrar el registro", Toast.LENGTH_SHORT).show();
                                });
                                Log.e("debug", "error al borrar la mascota: " + responseCode);
                            }

                            urlConnection.disconnect();

                        } catch (Exception e) {
                            e.printStackTrace();
                            // Mostrar un mensaje de error genérico en caso de excepción
                            runOnUiThread(() -> {
                                Toast.makeText(RegistrarServicioActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).start();
            }

        }