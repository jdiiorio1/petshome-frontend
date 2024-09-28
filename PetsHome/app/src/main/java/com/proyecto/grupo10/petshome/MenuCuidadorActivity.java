package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.animation.AnimationUtils;

public class MenuCuidadorActivity extends AppCompatActivity {

    CardView mMisServicios;
    CardView mAgenda;
    CardView mHistorialCuidados;
    CardView mMensajes;

    CardView mPropiedad;
    CardView mValoraciones;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cuidador);

        mMisServicios = findViewById(R.id.cv_mis_servicios);
        mAgenda = findViewById(R.id.cv_agenda);
        mHistorialCuidados = findViewById(R.id.cv_historial);
        mMensajes = findViewById(R.id.cv_mensajes);

        mPropiedad = findViewById(R.id.cv_propiedad);
        mValoraciones = findViewById(R.id.cv_valoraciones);

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

    }
}