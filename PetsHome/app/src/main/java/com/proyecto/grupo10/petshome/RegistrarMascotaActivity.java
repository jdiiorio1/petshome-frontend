package com.proyecto.grupo10.petshome;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class RegistrarMascotaActivity extends AppCompatActivity {


    ImageView mProfilePhoto, mImgAtras, mCargarFoto;
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
    File archivoImagen;
    Uri fotoPerfil;
    ActivityResultLauncher<Intent> resultlauncher;
    Properties configProperties = new Properties();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_mascota);

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



        /**
         * Adaptador para el spinner de especie
         */
        mEspecie = findViewById(R.id.sp_especie);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tipo_especie,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        mEspecie.setAdapter(adapter);


        mNombre = findViewById(R.id.et_nombre);
        mEdad = findViewById(R.id.et_edad);

        mRaza = findViewById(R.id.et_raza);
        mCuidadoEspecial = findViewById(R.id.et_cuidado_especial);
        mTituloPantalla = findViewById(R.id.tv_titulo_editar_mascota);
        mTextBoton = findViewById(R.id.btn_registrar_mascota);
        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mCargarFoto = findViewById(R.id.img_subir_foto);
        mBtnBorrarMascota = findViewById(R.id.btn_borrar_mascota);
        mImgAtras = findViewById(R.id.img_atras);

        registerResult();

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

        /**
         * Obtengo la imagen si esta cargada
         */
        cargarFotoSiExiste(idMascota);


        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);



        /**
         * boton atras
         */

        mImgAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mascotaIntent = new Intent(RegistrarMascotaActivity.this, MenuMascotaActivity.class);
                mascotaIntent.putExtra("idTutor", idTutor);
                startActivity(mascotaIntent);
                finish();
            }
        });





        // Listener de cargar foto para la mascota
        mCargarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "Entro a la galeria");
                Intent galeryIntent = new Intent(Intent.ACTION_PICK);
                galeryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                Log.i("debug", "Antes de guardar la URI");
                resultlauncher.launch(galeryIntent);
            }
        });


        mEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                especie = adapterView.getItemAtPosition(i).toString();
                if (!editar) {
                    int fotoMascota = getResources().getIdentifier(especie.toLowerCase(), "drawable", getPackageName());
                    mProfilePhoto.setImageResource(fotoMascota);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mTextBoton.setOnClickListener(view -> updateMascota());
 /*       mTextBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(() -> {
                    try {
                        String metodo = "";
                        URL url;
                        if(editar) {
                            url = new URL(configProperties.getProperty("url") + "/mascota/" + idMascota);
                            metodo = "PUT";
                            Log.i("debug", "editando la mascota");
                            Log.i("debug", url.toString());
                        } else {
                            url = new URL(configProperties.getProperty("url") + "/mascota");
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

*/
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
                    String urlStr = configProperties.getProperty("url") + "/mascota/delete/" + idMascota ;
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

    private void cargarFotoSiExiste(Integer idMascota) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = configProperties.getProperty("url") + "/mascota/imagen/" + idMascota;
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


                        // Guarda el bitmap en un archivo y obtiene la URI
                        fotoPerfil = saveBitmapToFile(bitmap);
                        if (fotoPerfil != null) {
                            Log.i("debug", "URI de la imagen guardada: " + fotoPerfil.toString());
                        }

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarMascotaActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "Error al cargar la imagen: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(RegistrarMascotaActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        try {
            // Crea un archivo temporal en el almacenamiento externo
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Guarda el bitmap en el archivo
            out.flush();
            out.close();
            return Uri.fromFile(file); // Devuelve la URI del archivo
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerResult() {

        resultlauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try {
                            fotoPerfil = o.getData().getData();
                            mProfilePhoto.setImageURI(fotoPerfil);

                            Log.i("debug", "La Uri de la imagen al cargarse la pagina es: " + fotoPerfil.toString());
/*
                            Usuario updateUsuario = new Usuario("Nombre hardcode", apellido, pass);
                            File imageFile = createFileFromUri(imageUri);
                            if (imageFile != null) {
                                updateUsuario(idUsuario, updateUsuario, imageFile); // Llama a tu método de actualización
                            } else {
                                // Maneja el caso donde no se pudo crear el archivo
                                Log.i("debug", "carga de imagen fallida");

                            }


                            updateUsuario(idUsuario, updateUsuario, imageFile);

*/

                        } catch(Exception e) {
                            Toast.makeText(RegistrarMascotaActivity.this,"No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private InputStream getInputStreamFromUri(Uri uri) {
        try {
            return getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File createFileFromUri(Uri uri) {
        InputStream inputStream = getInputStreamFromUri(uri);
        if (inputStream == null) {
            return null; // Maneja el caso donde no se pudo obtener el InputStream
        }

        // Crea un archivo temporal en el almacenamiento interno
        File file = new File(getCacheDir(), "temp_image.png"); // Cambia el nombre y la extensión según sea necesario

        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Maneja el error
        } finally {
            try {
                inputStream.close(); // Asegúrate de cerrar el InputStream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file; // Devuelve el archivo creado
    }

    /**
     * Update mascota
     */
    private void updateMascota() {

        Mascota mascota = new Mascota(mNombre.getText().toString(), especie, mRaza.getText().toString(), mEdad.getText().toString(), mCuidadoEspecial.getText().toString(), idTutor );
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection urlConnection = null;
                try {

                    String metodo = "";
                    URL url;
                    if(editar) {
                        url = new URL(configProperties.getProperty("url") + "/mascota/" + idMascota);
                        metodo = "PUT";
                        Log.i("debug", "editando la mascota");
                        Log.i("debug", url.toString());
                    } else {
                        url = new URL(configProperties.getProperty("url") + "/mascota");
                        metodo = "POST";
                        Log.i("debug", "Nueva mascota");
                        Log.i("debug", url.toString());
                    }
                    String boundary = "*****";
                    String lineEnd = "\r\n";

                    Log.i("debug", "URL update mascota con imagen: " + url.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod(metodo);
                    urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());

                    // Enviar datos del usuario como JSON
                    Gson gson = new Gson();
                    String mascotaJson = gson.toJson(mascota);
                    Log.e("debug", "Json de la mascota: " + mascotaJson);
                    dos.writeBytes("--" + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"mascota\"" + lineEnd);
                    dos.writeBytes("Content-Type: application/json" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(mascotaJson + lineEnd);



                    // Enviar la imagen si existe

                    if (fotoPerfil != null) {
                        Log.i("debug", "La Uri de la imagen es: " + fotoPerfil.toString());
                        archivoImagen = createFileFromUri(fotoPerfil);

                        if (archivoImagen != null && archivoImagen.exists()) {
                            dos.writeBytes("--" + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"" + archivoImagen.getName() + "\"" + lineEnd);
                            dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
                            dos.writeBytes(lineEnd);

                            // Leer el archivo de imagen y escribirlo en el output stream
                            FileInputStream fileInputStream = new FileInputStream(archivoImagen);
                            int bytesRead;
                            byte[] buffer = new byte[1024];
                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                dos.write(buffer, 0, bytesRead);
                            }
                            fileInputStream.close();
                            dos.writeBytes(lineEnd);
                        }


                    }

                    dos.writeBytes("--" + boundary + "--" + lineEnd);
                    dos.flush();
                    dos.close();

                    // Obtener la respuesta del servidor
                    int responseCode = urlConnection.getResponseCode();
                    Log.i("debug", "El response code al crear/modificar es: " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
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
                            Toast.makeText(RegistrarMascotaActivity.this, "Error al actualizar La mascota: " + responseCode, Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(RegistrarMascotaActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();

                    }
                }
            }
        }).start();



    }


}