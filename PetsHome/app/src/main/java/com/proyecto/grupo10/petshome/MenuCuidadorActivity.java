package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuCuidadorActivity extends AppCompatActivity {

    CardView mMisServicios;
    CardView mAgenda;
    CardView mHistorialCuidados;
    CardView mMensajes;

    CardView mPropiedad;
    CardView mValoraciones;

    ImageView mEditarPerfil;
    ImageView mImgBackground;
    TextView mSaludo;

    String nombre, apellido, email, pass, calle, numero, cp, localidad, departamento, piso, tipoVivienda;
    Integer idUsuario;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cuidador);

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");
            idUsuario = extras.getInt("idUsuario");


            calle = extras.getString("calle");
            numero = extras.getString("numero");
            cp = extras.getString("cp");
            localidad = extras.getString("localidad");
            departamento = extras.getString("departamento");
            piso = extras.getString("piso");
            tipoVivienda = extras.getString("tipoVivienda");


        }

        mSaludo = findViewById(R.id.tv_saludo);
        mSaludo.setText("Hola " + nombre + " " + apellido);

        mMisServicios = findViewById(R.id.cv_mis_servicios);
        mAgenda = findViewById(R.id.cv_agenda);
        mHistorialCuidados = findViewById(R.id.cv_historial);
        mMensajes = findViewById(R.id.cv_mensajes);

        mPropiedad = findViewById(R.id.cv_propiedad);
        mValoraciones = findViewById(R.id.cv_valoraciones);
        mEditarPerfil = findViewById(R.id.img_editar_perfil);
        mImgBackground = findViewById(R.id.img_background);
        mImgBackground.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in));

        mMisServicios.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));





        mMisServicios.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMisServicios.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mAgenda.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mAgenda.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAgenda.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
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

        mPropiedad.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mPropiedad.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPropiedad.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mValoraciones.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mValoraciones.postDelayed(new Runnable() {
            @Override
            public void run() {
                mValoraciones.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mPropiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent propiedadIntent = new Intent(MenuCuidadorActivity.this, PropiedadActivity.class);
                propiedadIntent.putExtra("calle", calle);
                propiedadIntent.putExtra("numero", numero);
                propiedadIntent.putExtra("localidad", localidad);
                propiedadIntent.putExtra("cp", cp);
                propiedadIntent.putExtra("departamento",departamento);
                propiedadIntent.putExtra("piso", piso);
                propiedadIntent.putExtra("tipoVivienda", tipoVivienda);
                propiedadIntent.putExtra("nombre", nombre);
                propiedadIntent.putExtra("apellido", apellido);
                propiedadIntent.putExtra("email", email);
                propiedadIntent.putExtra("pass", pass);


                startActivity(propiedadIntent);
            }
        });

        mMisServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviciosIntent = new Intent(MenuCuidadorActivity.this, ServiciosActivity.class);

                startActivity(serviciosIntent);
            }
        });

        mEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent perfilIntent = new Intent(MenuCuidadorActivity.this, EditarUsuarioActivity.class);
                perfilIntent.putExtra("nombre", nombre);
                perfilIntent.putExtra("apellido", apellido);
                perfilIntent.putExtra("email", email);
                perfilIntent.putExtra("pass", pass);
                perfilIntent.putExtra("esCuidador", true);

                startActivity(perfilIntent);
            }
        });

        mAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCuidadorActivity.this, EnConstruccionActivity.class);
                startActivity(intent);
            }
        });

        mHistorialCuidados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCuidadorActivity.this, EnConstruccionActivity.class);
                startActivity(intent);
            }
        });

        mMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCuidadorActivity.this, EnConstruccionActivity.class);
                startActivity(intent);
            }
        });


    }
}