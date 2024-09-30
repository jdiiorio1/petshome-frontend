package com.proyecto.grupo10.petshome;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ServiciosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        ListView servicios;
        String items[] = {"patio abierto", "Caniles para dormir", "Arbol o gimnasio para gatos", "mas de 20 metros cuadrados", "paseador"};

        servicios = findViewById(R.id.lst_servicios);
        ArrayAdapter<String> arr;


        arr = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items);

        Animation animation = AnimationUtils.loadAnimation(ServiciosActivity.this,R.anim.right_to_left );
        servicios.startAnimation(animation);
        servicios.setAdapter(arr);
    }
}