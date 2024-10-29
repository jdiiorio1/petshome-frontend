package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import okhttp3.*;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class EditarUsuarioActivity extends AppCompatActivity {

    EditText mNombre;
    EditText mApellido;
    EditText mEmail;
    EditText mPass;
    EditText mRePass;
    Button mGuardarCambios;
    Boolean esCuidador;
    Integer usuarioId;

    ImageView mProfilePhoto;
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
        mEmail = findViewById(R.id.et_email);
        mPass = findViewById(R.id.et_pass);
        mRePass = findViewById(R.id.et_repass);
        mGuardarCambios = findViewById(R.id.btn_registrar_usuario);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioId = extras.getInt("idUsuario"); // ID del usuario para la actualización
            mNombre.setText(extras.getString("nombre"));
            mApellido.setText(extras.getString("apellido"));
            mEmail.setText(extras.getString("email"));
            mPass.setText(extras.getString("pass"));
            mRePass.setText(extras.getString("pass"));
            esCuidador = extras.getBoolean("esCuidador");
        }

        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(() -> mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_in)), 300);

        mGuardarCambios.setOnClickListener(view -> actualizarUsuario());
    }

    private void actualizarUsuario() {
        String nombre = mNombre.getText().toString().trim();
        String apellido = mApellido.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPass.getText().toString().trim();
        String repassword = mRePass.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(EditarUsuarioActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repassword)){
            Toast.makeText(EditarUsuarioActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construir el cuerpo de la solicitud JSON
        String jsonBody = String.format("{\"nombre\":\"%s\",\"apellido\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", nombre, apellido, email, password);
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
                        homeIntent.putExtra("email", email);
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

}