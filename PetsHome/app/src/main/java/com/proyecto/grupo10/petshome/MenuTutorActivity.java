package com.proyecto.grupo10.petshome;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class MenuTutorActivity extends AppCompatActivity {

    CardView mBuscarCuidadores;
    CardView mMisMascotas;
    CardView mHistorialCuidados;
    CardView mMensajes;

    ImageView mEditarPerfil;
    ImageView mImgBackground;
    TextView mSaludo;

    String nombre, apellido, email, pass;
    Integer idUsuario;
    ActivityResultLauncher<Intent> resultlauncher;
    Properties configProperties = new Properties();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_tutor);

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
            idUsuario = extras.getInt("idUsuario");
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");

        }

        mEditarPerfil = findViewById(R.id.img_editar_perfil);

        mSaludo = findViewById(R.id.tv_saludo);
        mSaludo.setText(nombre + " " + apellido);
        mBuscarCuidadores = findViewById(R.id.cv_buscar_cuidadores);
        mMisMascotas = findViewById(R.id.cv_mis_mascotas);
        mHistorialCuidados = findViewById(R.id.cv_historial_cuidadores);
        mMensajes = findViewById(R.id.cv_mensajes);
        mImgBackground = findViewById(R.id.img_background);

        registerResult();

        mImgBackground.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in));

        mBuscarCuidadores.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mBuscarCuidadores.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBuscarCuidadores.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mMisMascotas.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mMisMascotas.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMisMascotas.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
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


        /**
         * Obtengo la imagen si esta cargada
         */
        cargarFotoSiExiste(idUsuario);
/*
        String imageUrl = configProperties.getProperty("url") + "/usuario/imagen/" + idUsuario;
        URL imageUrlObj = null;
        try {
            imageUrlObj = new URL(imageUrl);
            Log.i("debug", "URL: " + imageUrl);
            HttpURLConnection imageConnection = (HttpURLConnection) imageUrlObj.openConnection();
            imageConnection.setRequestMethod("GET");

            int imageResponseCode = imageConnection.getResponseCode();
            Log.i("debug", "El response code de la imagen es : " + imageResponseCode);

            if (imageResponseCode == HttpURLConnection.HTTP_OK) {
                InputStream imageInputStream = imageConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream);

                // Muestro la imagen

                mImgBackground.setImageBitmap(bitmap);
            } else {
                Log.e("debug", "Error al obtener la imagen: " + imageResponseCode);
            }

            imageConnection.disconnect();


        } catch (Exception e) {
            Log.i("debug", "Fallo la carga de imagen: " + e.getMessage());
            e.printStackTrace();
//            throw new RuntimeException(e);
        }

*/

        mMisMascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "Ingreso al cardview de mis macotas");
                Intent mascotasIntent = new Intent(MenuTutorActivity.this, MenuMascotaActivity.class);
                mascotasIntent.putExtra("idTutor", idUsuario);
                startActivity(mascotasIntent);
                finish();
            }
        });

        mBuscarCuidadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "Ingreso al cardview de buscar cuidador");
                Intent buscarCuidadorIntent = new Intent(MenuTutorActivity.this, BuscarCuidadorActivity.class);
                buscarCuidadorIntent.putExtra("idUsuario", idUsuario);
                startActivity(buscarCuidadorIntent);
                finish();
            }
        });

        mEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent perfilIntent = new Intent(MenuTutorActivity.this, EditarUsuarioActivity.class);
                perfilIntent.putExtra("idUsuario", idUsuario);
                perfilIntent.putExtra("nombre", nombre);
                perfilIntent.putExtra("apellido", apellido);
                perfilIntent.putExtra("email", email);
                perfilIntent.putExtra("pass", pass);
                perfilIntent.putExtra("esCuidador", false);

                startActivity(perfilIntent);
            }
        });

        mMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuTutorActivity.this, EnConstruccionActivity.class);
                startActivity(intent);
            }
        });

        mHistorialCuidados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuTutorActivity.this, HistorialCuidadosTutorActivity.class);
                intent.putExtra("idTutor", idUsuario);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido", apellido);
                intent.putExtra("email", email);
                intent.putExtra("pass", pass);
                startActivity(intent);
                finish();
            }
        });

        mImgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeryIntent = new Intent(Intent.ACTION_PICK);
                galeryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                resultlauncher.launch(galeryIntent);
            }
        });
    }

    private void registerResult() {

        resultlauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try {
                            Uri imageUri = o.getData().getData();
                            mImgBackground.setImageURI(imageUri);

                            Usuario updateUsuario = new Usuario("Nombre hardcode", apellido, pass);
                            File imageFile = createFileFromUri(imageUri);
                            if (imageFile != null) {
                                updateUsuario(idUsuario, updateUsuario, imageFile); // Llama a tu método de actualización
                            } else {
                                // Maneja el caso donde no se pudo crear el archivo
                                Log.i("debug", "carga de imagen fallida");

                            }


                            updateUsuario(idUsuario, updateUsuario, imageFile);



                        } catch(Exception e) {
                            Toast.makeText(MenuTutorActivity.this,"No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
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
                            Toast.makeText(MenuTutorActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "Error al cargar la imagen: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(MenuTutorActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void updateUsuario(Integer idUsuario, Usuario usuario, File imageFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    String boundary = "*****";
                    String lineEnd = "\r\n";
                    String urlStr = configProperties.getProperty("url") + "/usuario/" + idUsuario;
                    URL url = new URL(urlStr);
                    Log.e("debug", "URL update usuario con imagen: " + urlStr);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());

                    // Enviar datos del usuario como JSON
                    Gson gson = new Gson();
                    String usuarioJson = gson.toJson(usuario);
                    Log.e("debug", "Json del usuario: " + usuarioJson);
                    dos.writeBytes("--" + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"usuario\"" + lineEnd);
                    dos.writeBytes("Content-Type: application/json" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(usuarioJson + lineEnd);

                    // Enviar la imagen si existe
                    if (imageFile != null && imageFile.exists()) {
                        dos.writeBytes("--" + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"" + lineEnd);
                        dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
                        dos.writeBytes(lineEnd);

                        // Leer el archivo de imagen y escribirlo en el output stream
                        FileInputStream fileInputStream = new FileInputStream(imageFile);
                        int bytesRead;
                        byte[] buffer = new byte[1024];
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            dos.write(buffer, 0, bytesRead);
                        }
                        fileInputStream.close();
                        dos.writeBytes(lineEnd);
                    }

                    dos.writeBytes("--" + boundary + "--" + lineEnd);
                    dos.flush();
                    dos.close();

                    // Obtener la respuesta del servidor
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            Toast.makeText(MenuTutorActivity.this, "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(MenuTutorActivity.this, "Error al actualizar el usuario: " + responseCode, Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(MenuTutorActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
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

}