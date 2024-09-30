package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegistrarMascotaActivity extends AppCompatActivity {


    ImageView mProfilePhoto;
    EditText mNombre;
    EditText mEdad;
    EditText mRaza;
    EditText mEspecie;
    EditText mCuidadoEspecial;

    TextView mTituloPantalla;
    TextView mTextBoton;
    Boolean editar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_mascota);

        mNombre = findViewById(R.id.et_nombre);
        mEdad = findViewById(R.id.et_edad);
        mEspecie = findViewById(R.id.et_especie);
        mRaza = findViewById(R.id.et_raza);
        mCuidadoEspecial = findViewById(R.id.et_cuidado_especial);
        mTituloPantalla = findViewById(R.id.tv_titulo_editar_mascota);
        mTextBoton = findViewById(R.id.btn_registrar_mascota);
        mProfilePhoto = findViewById(R.id.img_profile_photo);

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);



        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            mNombre.setText(extras.getString("nombre"));
            mEdad.setText(extras.getString("edad"));
            mRaza.setText(extras.getString("raza"));
            mEspecie.setText(extras.getString("especie"));
            mCuidadoEspecial.setText(extras.getString("cuidadoEspecial"));
            mTituloPantalla.setText("Edita los datos de " + mNombre.getText().toString());
            mTextBoton.setText("GUARDAR CAMBIOS");
            editar = true;
        }


    }
}