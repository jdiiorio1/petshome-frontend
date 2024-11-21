package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RegistrarseActivity extends AppCompatActivity {

    ImageView mProfilePhoto;
    TextInputEditText mEtNombre, mEtApellido, mEtEmail, mEtContrasena, mEtConfirmarContrasena;
    Switch mSwitchCuidador;
    Button mBtnRegistrar;
    CheckBox mTerminosCondiciones;
    TextView mTvContrato;
    Properties configProperties = new Properties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

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

        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mEtNombre = findViewById(R.id.et_nombre);
        mEtApellido = findViewById(R.id.et_apellido);
        mTvContrato = findViewById(R.id.tv_contrato);
        mEtEmail = findViewById(R.id.et_email);
        mEtContrasena = findViewById(R.id.et_pass);
        mEtConfirmarContrasena = findViewById(R.id.et_repass);
        mSwitchCuidador = findViewById(R.id.switch_cuidador);
        mBtnRegistrar = findViewById(R.id.btn_registrar_usuario);
        mTerminosCondiciones = findViewById(R.id.check_terminos);

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_out));

        mTvContrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popUpView = inflater.inflate(R.layout.contratopopup, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });


        mBtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });


    }

    public boolean validarCampos () {
        boolean valido = true;

        if (TextUtils.isEmpty(mEtNombre.getText().toString())) {
            Toast.makeText(RegistrarseActivity.this, "El campo nombre es obligatorio", Toast.LENGTH_LONG).show();
            valido = false;
        }
        if (TextUtils.isEmpty(mEtApellido.getText().toString())) {
            Toast.makeText(RegistrarseActivity.this, "El campo apellido es obligatorio", Toast.LENGTH_LONG).show();
            valido = false;
        }
        if (TextUtils.isEmpty(mEtEmail.getText().toString()) || !Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString()).matches()) {
            Toast.makeText(RegistrarseActivity.this, "Ingrese un correo válido", Toast.LENGTH_LONG).show();
            valido = false;
        }
        if (TextUtils.isEmpty(mEtContrasena.getText().toString()) || mEtContrasena.getText().toString().length() < 8) {
            Toast.makeText(RegistrarseActivity.this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_LONG).show();
            valido = false;
        }

        if (!mEtContrasena.getText().toString().equals(mEtConfirmarContrasena.getText().toString())) {
            Toast.makeText(RegistrarseActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            valido = false;
        }
        if (!mTerminosCondiciones.isChecked()) {
            Toast.makeText(RegistrarseActivity.this, "Debe aceptar los términos y condiciones", Toast.LENGTH_LONG).show();
            valido = false;
        }

        return valido;
    }
/*
    private void registrarUsuario() {
        if (validarCampos()) {
            String nombre = mEtNombre.getText().toString().trim();
            String apellido = mEtApellido.getText().toString().trim();
            String email = mEtEmail.getText().toString().trim();
            String contrasena = mEtContrasena.getText().toString().trim();
            int rol = mSwitchCuidador.isChecked() ? 1 : 0;

            new Thread(() -> {
                try {
                    // Ajuste de la URL para localhost
                    URL url = new URL(configProperties.getProperty("url") + "/usuario");  // Usa 10.0.2.2 si estás en un emulador
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    // Creación del objeto JSON con los datos
                    JSONObject json = new JSONObject();
                    json.put("nombre", nombre);
                    json.put("apellido", apellido);
                    json.put("email", email);
                    json.put("password", contrasena);
                    json.put("rol", rol);

                    // Envío de la solicitud
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    // Verificar respuesta
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {  // Usuario creado correctamente (201)
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarseActivity.this);
                            builder.setMessage("Registro exitoso. Puedes iniciar sesión.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();  // Cierra la actividad
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        });
                    } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {  // Email ya existente (409)
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarseActivity.this, "El correo ya está registrado. Intente con otro.", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarseActivity.this, "error de conexion.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegistrarseActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

 */

    public void registrarUsuario() {

        Usuario usuario = new Usuario();
        if (validarCampos()) {
            String nombre = mEtNombre.getText().toString().trim();
            String apellido = mEtApellido.getText().toString().trim();
            String email = mEtEmail.getText().toString().trim();
            String contrasena = mEtContrasena.getText().toString().trim();
            int rol = mSwitchCuidador.isChecked() ? 1 : 0;
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPassword(contrasena);
            usuario.setRol(rol);

        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection urlConnection = null;
                try {

                    String metodo = "";
                    URL url;

                    url = new URL(configProperties.getProperty("url") + "/usuario");
                    metodo = "POST";

                    Log.i("debug", url.toString());

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
                    String usuarioJson = gson.toJson(usuario);
                    Log.e("debug", "Json de la mascota: " + usuarioJson);
                    dos.writeBytes("--" + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"usuario\"" + lineEnd);
                    dos.writeBytes("Content-Type: application/json" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(usuarioJson + lineEnd);



                    // Enviar la imagen si existe
/*
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
*/
                    dos.writeBytes("--" + boundary + "--" + lineEnd);
                    dos.flush();
                    dos.close();

                    // Obtener la respuesta del servidor
                    int responseCode = urlConnection.getResponseCode();
                    Log.i("debug", "El response code al creares: " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarseActivity.this);
                            builder.setMessage("Registro exitoso. Puedes iniciar sesión.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();  // Cierra la actividad
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarseActivity.this, "Error al crear el usuario: " + responseCode, Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(RegistrarseActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();

                    }
                }
            }
        }).start();

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Volver al login")
                .setMessage("¿Esta seguro que quiere volver al login?")
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
}