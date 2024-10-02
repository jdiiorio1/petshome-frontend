package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PropiedadActivity extends AppCompatActivity {

    ImageView mProfilePhoto;

    EditText mCalle, mNumero,mLocalidad, mCP, mDepartamento, mPiso, mTipoVivienda;

    String nombre, apellido, email, pass;

    Button mRegistrarVivienda;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propiedad);



        mProfilePhoto = findViewById(R.id.img_profile_photo);

        mCalle = findViewById(R.id.et_calle);
        mNumero = findViewById(R.id.et_numero);
        mCP = findViewById(R.id.et_cp);
        mLocalidad = findViewById(R.id.et_localidad);
        mDepartamento = findViewById(R.id.et_departamento);
        mPiso = findViewById(R.id.et_piso);
        mTipoVivienda = findViewById(R.id.et_tipo_vivienda);
        mRegistrarVivienda = findViewById(R.id.btn_registrar_propiedad);


        Bundle extras = getIntent().getExtras();
        if (extras != null ) {

            mCalle.setText(extras.getString("calle"));
            mNumero.setText(extras.getString("numero"));
            mCP.setText(extras.getString("cp"));
            mLocalidad.setText(extras.getString("localidad"));
            mDepartamento.setText(extras.getString("departamento"));
            mPiso.setText(extras.getString("piso"));
            mTipoVivienda.setText(extras.getString("tipoVivienda"));
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");

        }



        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mRegistrarVivienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validarCampos()) {
                    Intent propiedadIntent = new Intent(PropiedadActivity.this, MenuCuidadorActivity.class);
                    propiedadIntent.putExtra("calle", mCalle.getText().toString());
                    propiedadIntent.putExtra("numero", mNumero.getText().toString());
                    propiedadIntent.putExtra("localidad", mLocalidad.getText().toString());
                    propiedadIntent.putExtra("cp", mCP.getText().toString());
                    propiedadIntent.putExtra("departamento", mDepartamento.getText().toString());
                    propiedadIntent.putExtra("piso", mPiso.getText().toString());
                    propiedadIntent.putExtra("tipoVivienda", mTipoVivienda.getText().toString());
                    propiedadIntent.putExtra("nombre", nombre);
                    propiedadIntent.putExtra("apellido", apellido);
                    propiedadIntent.putExtra("email", email);
                    propiedadIntent.putExtra("pass", pass);

                    startActivity(propiedadIntent);

                }
            }
        });


    }

    public boolean validarCampos () {

        boolean error = true;
        if (TextUtils.isEmpty(mCalle.getText().toString())) {
            Toast.makeText(PropiedadActivity.this, "El campo Calle es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }

        if (TextUtils.isEmpty(mNumero.getText().toString()) ) {
            Toast.makeText(PropiedadActivity.this, "El campo Numero es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }

        if (TextUtils.isEmpty(mLocalidad.getText().toString())) {
            Toast.makeText(PropiedadActivity.this, "El campo Localidad es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }

        if (TextUtils.isEmpty(mCP.getText().toString()) ) {
            Toast.makeText(PropiedadActivity.this, "El campo Codigo Postal es obligatorio", Toast.LENGTH_SHORT).show();
            error = false;
        }


        return error;

    }
}