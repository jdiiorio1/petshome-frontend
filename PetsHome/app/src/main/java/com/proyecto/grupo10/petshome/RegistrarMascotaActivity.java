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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RegistrarMascotaActivity extends AppCompatActivity {


    ImageView mProfilePhoto, mImgAtras;
    EditText mNombre;
    EditText mEdad;
    EditText mRaza;
    Spinner mEspecie;
    EditText mCuidadoEspecial;

    TextView mTituloPantalla;
    Button mTextBoton, mBtnBorrarMascota;
    Boolean editar = false;
    Integer idTutor, idMascota;

    String especie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_mascota);



        mNombre = findViewById(R.id.et_nombre);
        mEdad = findViewById(R.id.et_edad);
        mEspecie = findViewById(R.id.sp_especie);
        mRaza = findViewById(R.id.et_raza);
        mCuidadoEspecial = findViewById(R.id.et_cuidado_especial);
        mTituloPantalla = findViewById(R.id.tv_titulo_editar_mascota);
        mTextBoton = findViewById(R.id.btn_registrar_mascota);
        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mBtnBorrarMascota = findViewById(R.id.btn_borrar_mascota);
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
        mEspecie.setAdapter(adapter);

        /**
         * boton atras
         */

        mImgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            mNombre.setText(extras.getString("nombre"));
            mEdad.setText(extras.getString("edad"));
            mRaza.setText(extras.getString("raza"));
            mEspecie.setSelection(adapter.getPosition(extras.getString("especie")));
            //mEspecie.setText(extras.getString("especie"));
            mCuidadoEspecial.setText(extras.getString("cuidadoEspecial"));

            idMascota = extras.getInt("idMascota");
            idTutor = extras.getInt("idTutor");
            if (!mNombre.getText().toString().isEmpty()) {
                mTextBoton.setText("GUARDAR CAMBIOS");
                mTituloPantalla.setText("Edita los datos de " + mNombre.getText().toString());
                mBtnBorrarMascota.setVisibility(View.VISIBLE);
                editar = true;
            }
        }


        mEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                especie = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mTextBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(() -> {
                    try {
                        String metodo = "";
                        URL url;
                        if(editar) {
                            url = new URL("http://192.168.1.183:8081/mascota/" + idMascota);
                            metodo = "PUT";
                            Log.i("debug", "editando la mascota");
                            Log.i("debug", url.toString());
                        } else {
                            url = new URL("http://192.168.1.183:8081/mascota");
                            metodo = "POST";
                        }

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod(metodo);
                        Log.i("debug",conn.getRequestMethod().toString());
                        conn.setRequestProperty("Content-Type", "application/json; utf-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);

                        JSONObject json = new JSONObject();
                        json.put("nombre", mNombre.getText().toString());
                        json.put("edad", mEdad.getText().toString());
                        json.put("raza", mRaza.getText().toString());
                        json.put("especie", especie);
                        json.put("cuidadoEspecial", mCuidadoEspecial.getText().toString());
                        json.put("idTutor", idTutor);

                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarMascotaActivity.this);
                                builder.setMessage("Se guardaron los cambios.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent mascotaIntent = new Intent(RegistrarMascotaActivity.this, MenuMascotaActivity.class);
                                                Log.i("debug", "id Tutor en Registrar luego de crear o modificar: " + idTutor);
                                                mascotaIntent.putExtra("idTutor", idTutor);
                                                startActivity(mascotaIntent);
                                                finish(); // Cerrar la actividad
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(RegistrarMascotaActivity.this, "Error en el registro.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarMascotaActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();


            }
        });


        mBtnBorrarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogoConfirmacion = new AlertDialog.Builder(RegistrarMascotaActivity.this);
                dialogoConfirmacion.setTitle("BORRAR MASCOTA");

                dialogoConfirmacion.setMessage("¿Seguro desea borrar el registro de " + mNombre.getText().toString() + "?");
                dialogoConfirmacion.setCancelable(false);
                dialogoConfirmacion.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        borrarMascota();
                        Toast.makeText(RegistrarMascotaActivity.this, "MASCOTA BORRADA", Toast.LENGTH_SHORT).show();
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

    private void borrarMascota() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construir la URL con los parámetros email y contraseña
                    String urlStr = "http://192.168.1.183:8081/mascota/delete/" + idMascota ;
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("DELETE");
                    urlConnection.setDoOutput(true);

                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Mascota eliminada
                        // Manejar error de autenticación
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarMascotaActivity.this, "Datos de la mascota borrados", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "se elimino la mascota con ID: " + idMascota);

                        Intent mascotaIntent = new Intent(RegistrarMascotaActivity.this, MenuMascotaActivity.class);
                        mascotaIntent.putExtra("idTutor", idTutor);
                        startActivity(mascotaIntent);
                        finish(); // Cerrar la actividad


                    } else {
                        // Error al eliminar la mascota
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarMascotaActivity.this, "No se pudo borrar el registro", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "error al borrar la mascota: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Mostrar un mensaje de error genérico en caso de excepción
                    runOnUiThread(() -> {
                        Toast.makeText(RegistrarMascotaActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

}