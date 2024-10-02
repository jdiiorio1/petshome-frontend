package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class RegistrarseActivity extends AppCompatActivity {

    ImageView mProfilePhoto;
    TextInputEditText mEtNombre, mEtApellido, mEtEmail, mEtContrasena, mEtConfirmarContrasena;
    Switch mSwitchCuidador;
    TextView mTvContrato;
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
        mTvContrato = findViewById(R.id.tv_contrato);
        mBtnRegistrar = findViewById(R.id.btn_registrar_usuario);

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_out));
        mTerminosCondiciones = findViewById(R.id.check_terminos);



        Bundle extras = getIntent().getExtras();
        if (extras != null ) {

        }

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_in));
            }
        }, 300);

        mTvContrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popUpView = inflater.inflate(R.layout.contratopopup, null);

                // Crear la ventana emergente
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

                // Mostrar la ventana emergente
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

        boolean error = true;
        if (TextUtils.isEmpty(mEtNombre.getText().toString())) {
            Toast.makeText(RegistrarseActivity.this, "El campo nombre de usuario es obligatorio", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (TextUtils.isEmpty(mEtEmail.getText().toString()) || !Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString()).matches()) {
            Toast.makeText(RegistrarseActivity.this, "Ingrese una direccion de correo valida", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (TextUtils.isEmpty(mEtContrasena.getText().toString()) || (mEtContrasena.getText().toString().length() < 8 )) {
            Toast.makeText(RegistrarseActivity.this, "La contraseña debe contener al menos 8 caracteres", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (TextUtils.isEmpty(mEtConfirmarContrasena.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Repita la contraseña", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (!mEtContrasena.getText().toString().equals(mEtConfirmarContrasena.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Las contraseñas deben ser iguales", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (!mTerminosCondiciones.isChecked()) {
            Toast.makeText(getApplicationContext(), "Debe aceptar los terminos y condiciones", Toast.LENGTH_LONG).show();
            error = false;
        }


        return error;
    }

    private void registrarUsuario() {
        String nombre = mEtNombre.getText().toString().trim();
        String apellido = mEtApellido.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();
        String contrasena = mEtContrasena.getText().toString().trim();
        String confirmarContrasena = mEtConfirmarContrasena.getText().toString().trim();
        int rol = mSwitchCuidador.isChecked() ? 1 : 0;
        if (validarCampos()) {

            // Lógica para registrar el usuario
            new Thread(() -> {
                try {
                    URL url = new URL("https://api.petshome.com.ar/usuario");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    JSONObject json = new JSONObject();
                    json.put("nombre", nombre);
                    json.put("apellido", apellido);
                    json.put("email", email);
                    json.put("contraseña", contrasena);
                    json.put("rol", rol);

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarseActivity.this);
                            builder.setMessage("Registro exitoso. Puedes iniciar sesión.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish(); // Cerrar la actividad
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(RegistrarseActivity.this, "Error en el registro.", Toast.LENGTH_SHORT).show();
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
