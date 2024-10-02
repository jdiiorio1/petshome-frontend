package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditarUsuarioActivity extends AppCompatActivity {

    EditText mNombre;
    EditText mApellido;
    EditText mEmail;
    EditText mPass;
    EditText mRePass;
    Button mGuardarCambios;
    Boolean esCuidador;



    ImageView mProfilePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        mNombre = findViewById(R.id.et_nombre);
        mApellido = findViewById(R.id.et_apellido);
        mEmail = findViewById(R.id.et_email);
        mPass = findViewById(R.id.et_pass);
        mRePass = findViewById(R.id.et_repass);
        mGuardarCambios = findViewById(R.id.btn_registrar_usuario);

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            mNombre.setText(extras.getString("nombre"));
            mApellido.setText(extras.getString("apellido"));
            mEmail.setText(extras.getString("email"));
            mPass.setText(extras.getString("pass"));
            mRePass.setText(extras.getString("pass"));
            esCuidador = extras.getBoolean("esCuidador");


        }

        mProfilePhoto = findViewById(R.id.img_profile_photo);

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent homeIntent;

                if (esCuidador) {
                    Log.i("debug","Entro como cuidador");
                    homeIntent = new Intent(EditarUsuarioActivity.this, MenuCuidadorActivity.class);
                } else {
                    homeIntent = new Intent(EditarUsuarioActivity.this, MenuTutorActivity.class);
                }

                homeIntent.putExtra("nombre", mNombre.getText().toString());
                homeIntent.putExtra("apellido", mApellido.getText().toString());
                homeIntent.putExtra("email", mEmail.getText().toString());
                homeIntent.putExtra("pass", mPass.getText().toString());


                startActivity(homeIntent);
            }
        });
    }
}