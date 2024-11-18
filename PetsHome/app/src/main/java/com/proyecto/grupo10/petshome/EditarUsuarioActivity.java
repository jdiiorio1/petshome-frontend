package com.proyecto.grupo10.petshome;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import okhttp3.*;

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
import java.util.Properties;

public class EditarUsuarioActivity extends AppCompatActivity {

    EditText mNombre;
    EditText mApellido;
    TextView mActualizarFoto;
    EditText mPass;
    EditText mRePass;
    Button mGuardarCambios;
    Boolean esCuidador;
    Integer usuarioId;
    String email;

    ImageView mProfilePhoto;
    ActivityResultLauncher<Intent> resultlauncher;
    Uri fotoPerfil;
    File archivoImagen;
    Properties configProperties = new Properties();
    private final OkHttpClient client = new OkHttpClient(); // Cliente HTTP para las solicitudes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

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

        mNombre = findViewById(R.id.et_nombre);
        mApellido = findViewById(R.id.et_apellido);
        mActualizarFoto = findViewById(R.id.tv_actualizar_foto);
        mPass = findViewById(R.id.et_pass);
        mRePass = findViewById(R.id.et_repass);
        mGuardarCambios = findViewById(R.id.btn_registrar_usuario);
        registerResult();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            usuarioId = extras.getInt("idUsuario"); // ID del usuario para la actualización
            mNombre.setText(extras.getString("nombre"));
            mApellido.setText(extras.getString("apellido"));
            mPass.setText(extras.getString("pass"));
            mRePass.setText(extras.getString("pass"));
            esCuidador = extras.getBoolean("esCuidador");
        }

        mProfilePhoto = findViewById(R.id.img_profile_photo);
        /**
         * Obtengo la imagen si esta cargada
         */
        cargarFotoSiExiste(usuarioId);
        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(() -> mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_in)), 300);

        mActualizarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "Entro a la galeria");
                Intent galeryIntent = new Intent(Intent.ACTION_PICK);
                galeryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                Log.i("debug", "Antes de guardar la URI");
                resultlauncher.launch(galeryIntent);
            }
        });

        //mGuardarCambios.setOnClickListener(view -> actualizarUsuario());
        mGuardarCambios.setOnClickListener(view -> updateUsuario());
    }

    private void registerResult() {

        resultlauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try {

                            Log.i("debug", "guardo la URI de la imagen");
                            fotoPerfil = o.getData().getData();
                            mProfilePhoto.setImageURI(fotoPerfil);

                            Log.i("debug", "La Uri de la imagen al cargarse la pagina es: " + fotoPerfil.toString());




 /*                           if (imageFile != null) {
                                updateUsuario(idUsuario, updateUsuario, imageFile); // Llama a tu método de actualización
                            } else {
                                // Maneja el caso donde no se pudo crear el archivo
                                Log.i("debug", "carga de imagen fallida");

                            }


                            updateUsuario(idUsuario, updateUsuario, imageFile);

*/

                        } catch(Exception e) {
                            Log.i("debug", "fallo la carga de la URI");
                            Toast.makeText(EditarUsuarioActivity.this,"No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void actualizarUsuario() {
        String nombre = mNombre.getText().toString().trim();
        String apellido = mApellido.getText().toString().trim();

        String password = mPass.getText().toString().trim();
        String repassword = mRePass.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(EditarUsuarioActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repassword)){
            Toast.makeText(EditarUsuarioActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir el cuerpo de la solicitud JSON
        String jsonBody = String.format("{\"nombre\":\"%s\",\"apellido\":\"%s\",\"password\":\"%s\"}", nombre, apellido, password);
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        // Crear solicitud PUT para actualizar usuario
        String apiURL = configProperties.getProperty("url") + "/usuario/" + usuarioId; // URL de actualización
        Request request = new Request.Builder()
                .url(apiURL)
                .put(body)
                .build();

        // Ejecutar la solicitud en un hilo separado
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(EditarUsuarioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(EditarUsuarioActivity.this, "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show();
                        Intent homeIntent;
                        if (esCuidador) {
                            homeIntent = new Intent(EditarUsuarioActivity.this, MenuCuidadorActivity.class);
                        } else {
                            homeIntent = new Intent(EditarUsuarioActivity.this, MenuTutorActivity.class);
                        }
                        homeIntent.putExtra("idUsuario", usuarioId);
                        homeIntent.putExtra("nombre", nombre);
                        homeIntent.putExtra("apellido", apellido);
                        homeIntent.putExtra("pass", password);
                        homeIntent.putExtra("esCuidador", esCuidador);
                        startActivity(homeIntent);
                    });
                } else if (response.code() == 400) { // Error de email duplicado
                    String errorMessage = response.body() != null ? response.body().string() : "Error, el email ya existe";
                    runOnUiThread(() -> mostrarErrorDialog(errorMessage));
                } else {
                    runOnUiThread(() -> Toast.makeText(EditarUsuarioActivity.this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    private void mostrarErrorDialog(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar edicion de perfil")
                .setMessage("¿Está seguro de que quiere regresar al menú?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Construir la URL con los parámetros email y contraseña

                                String urlStr = configProperties.getProperty("url") + "/usuario/" + usuarioId;
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
                                    String apellido = jsonResponse.getString("apellido");
                                    String email = jsonResponse.getString("email");
                                    String pass = jsonResponse.getString("password");
                                    Boolean mCuidador = null;
                                    Intent homeIntent;
                                    if (rol == 1) {
                                        mCuidador = true;
                                        homeIntent = new Intent(EditarUsuarioActivity.this, MenuCuidadorActivity.class);
                                    } else {
                                        mCuidador = false;
                                        homeIntent = new Intent(EditarUsuarioActivity.this, MenuTutorActivity.class);
                                    }
                                    homeIntent.putExtra("idUsuario", idUsuario);
                                    homeIntent.putExtra("nombre", nombre);
                                    homeIntent.putExtra("apellido", apellido);
                                    homeIntent.putExtra("email", email);
                                    homeIntent.putExtra("pass", pass);
                                    homeIntent.putExtra("esCuidador", mCuidador);

                                    startActivity(homeIntent);
                                    finish();

                                }

                                urlConnection.disconnect();

                            } catch (Exception e) {
                                e.printStackTrace();
                                // Mostrar un mensaje de error genérico en caso de excepción
                                runOnUiThread(() -> {
                                });
                            }
                        }
                    }).start();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void updateUsuario() {

        String nombre = mNombre.getText().toString().trim();
        String apellido = mApellido.getText().toString().trim();

        String password = mPass.getText().toString().trim();
        String repassword = mRePass.getText().toString().trim();


        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(EditarUsuarioActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repassword)){
            Toast.makeText(EditarUsuarioActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        Usuario usuario = new Usuario(nombre, apellido, password);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    String boundary = "*****";
                    String lineEnd = "\r\n";
                    String urlStr = configProperties.getProperty("url") + "/usuario/" + usuarioId;
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

                    if (fotoPerfil != null) {
                        Log.i("debug", "La Uri de la imagen es: " + fotoPerfil.toString());
                        // Enviar la imagen si existe
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
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            Toast.makeText(EditarUsuarioActivity.this, "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(EditarUsuarioActivity.this, "Error al actualizar el usuario: " + responseCode, Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(EditarUsuarioActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();

                        Intent homeIntent;
                        if (esCuidador) {
                            homeIntent = new Intent(EditarUsuarioActivity.this, MenuCuidadorActivity.class);
                        } else {
                            homeIntent = new Intent(EditarUsuarioActivity.this, MenuTutorActivity.class);
                        }
                        homeIntent.putExtra("idUsuario", usuarioId);
                        homeIntent.putExtra("nombre", nombre);
                        homeIntent.putExtra("apellido", apellido);
                        homeIntent.putExtra("email", email);
                        homeIntent.putExtra("pass", password);
                        homeIntent.putExtra("esCuidador", esCuidador);
                        startActivity(homeIntent);
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
                            Toast.makeText(EditarUsuarioActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("debug", "Error al cargar la imagen: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(EditarUsuarioActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }



}