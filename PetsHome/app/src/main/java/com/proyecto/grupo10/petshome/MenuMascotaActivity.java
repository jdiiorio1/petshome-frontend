package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MenuMascotaActivity extends AppCompatActivity {

    FloatingActionButton mFabMascota;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_mascostas);

        mFabMascota = findViewById(R.id.fab_add_mascota);
        mFabMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrarMascotaIntent = new Intent(MenuMascotaActivity.this, RegistrarMascotaActivity.class);
                startActivity(registrarMascotaIntent);
            }
        });
    }
}