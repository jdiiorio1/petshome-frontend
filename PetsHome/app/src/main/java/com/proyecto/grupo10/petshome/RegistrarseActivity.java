package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegistrarseActivity extends AppCompatActivity {

    ImageView mProfilePhoto;
    TextInputEditText mEtNombre, mEtApellido, mEtEmail, mEtContrasena, mEtConfirmarContrasena;
    Switch mSwitchCuidador;
    Button mBtnRegistrar;
    CheckBox mTerminosCondiciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mEtNombre = findViewById(R.id.et_nombre);
        mEtApellido = findViewById(R.id.et_apellido);
        mEtEmail = findViewById(R.id.et_email);
        mEtContrasena = findViewById(R.id.et_pass);
        mEtConfirmarContrasena = findViewById(R.id.et_repass);
        mSwitchCuidador = findViewById(R.id.switch_cuidador);
        mBtnRegistrar = findViewById(R.id.btn_registrar_usuario);
        mTerminosCondiciones = findViewById(R.id.check_terminos);

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_out));

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
                    URL url = new URL("http://10.0.2.2:8081/usuario");  // Usa 10.0.2.2 si estás en un emulador
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
                            Toast.makeText(RegistrarseActivity.this, "El correo ya está registrado. Intente con otro.", Toast.LENGTH_SHORT).show();
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
}
