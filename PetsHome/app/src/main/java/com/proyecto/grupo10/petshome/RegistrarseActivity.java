package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.regex.Pattern;

public class RegistrarseActivity extends AppCompatActivity {

    ImageView mProfilePhoto;
    TextView mTvContrato;

    TextView mTituloPantalla;
    Button mBtnRegistrarse;

    EditText mNombre;
    EditText mApellido;
    EditText mEmail;
    EditText mPass;
    EditText mRePass;
    Switch mCuidador;
    CheckBox mTerminosCondiciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        mProfilePhoto = findViewById(R.id.img_profile_photo);
        mTvContrato = findViewById(R.id.tv_contrato);
        mTituloPantalla = findViewById(R.id.tv_registrarse);
        mBtnRegistrarse = findViewById(R.id.btn_registrar_usuario);
        mNombre = findViewById(R.id.et_nombre);
        mApellido = findViewById(R.id.et_apellido);
        mEmail = findViewById(R.id.et_email);
        mPass = findViewById(R.id.et_pass);
        mRePass = findViewById(R.id.et_repass);
        mCuidador = findViewById(R.id.switch_cuidador);
        mTerminosCondiciones = findViewById(R.id.check_terminos);



        Bundle extras = getIntent().getExtras();
        if (extras != null ) {

        }

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

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

        mBtnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    // Intent a la pagina de login correspondiente
                    Intent homeIntent;
                    if (mCuidador.isChecked()) {
                        Log.i("debug","Entro como cuidador");
                        homeIntent = new Intent(RegistrarseActivity.this, MenuCuidadorActivity.class);
                    } else {
                        homeIntent = new Intent(RegistrarseActivity.this, MenuTutorActivity.class);
                    }

                    homeIntent.putExtra("nombre", mNombre.getText().toString());
                    homeIntent.putExtra("apellido", mApellido.getText().toString());
                    homeIntent.putExtra("email", mEmail.getText().toString());
                    homeIntent.putExtra("pass", mPass.getText().toString());
                    homeIntent.putExtra("esCuidador", mCuidador.isChecked());

                    startActivity(homeIntent);

                }
            }
        });

    }

    public boolean validarCampos () {

        boolean error = true;
        if (TextUtils.isEmpty(mNombre.getText().toString())) {
            Toast.makeText(RegistrarseActivity.this, "El campo nombre de usuario es obligatorio", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (TextUtils.isEmpty(mEmail.getText().toString()) || !Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
            Toast.makeText(RegistrarseActivity.this, "Ingrese una direccion de correo valida", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (TextUtils.isEmpty(mPass.getText().toString()) || (mPass.getText().toString().length() < 8 )) {
            Toast.makeText(RegistrarseActivity.this, "La contraseña debe contener al menos 8 caracteres", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (TextUtils.isEmpty(mRePass.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Repita la contraseña", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (!mPass.getText().toString().equals(mRePass.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Las contraseñas deben ser iguales", Toast.LENGTH_LONG).show();
            error = false;
        }

        if (!mTerminosCondiciones.isChecked()) {
            Toast.makeText(getApplicationContext(), "Debe aceptar los terminos y condiciones", Toast.LENGTH_LONG).show();
            error = false;
        }


        return error;

    }
}