package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class EditarUsuarioActivity extends AppCompatActivity {


    ImageView mProfilePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        mProfilePhoto = findViewById(R.id.img_profile_photo);

        mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mProfilePhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);
    }
}