package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MenuMascotaActivity extends AppCompatActivity {

    FloatingActionButton mFabMascota;

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    String nombre, edad, raza, especie, cuidadoEspecial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_mascostas);

        // Inicializar mascotas de prueba
        List items = new ArrayList();

        items.add(new Mascota("Feliz", "Gato", "Blanco y negro", "9 meses", "No" ));
        items.add(new Mascota("Charly", "Perro", "Caniche", "4 años", "No se lleva con otras mascotas" ));
        items.add(new Mascota("Sultan", "Perro", "Salchicha", "12 años", "Come alimento humedo solamente" ));

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            nombre = extras.getString("nombre");
            edad = extras.getString("edad");
            raza = extras.getString("raza");
            especie = extras.getString("especie");
            cuidadoEspecial = extras.getString("cuidadoEspecial");


            for (int i =0 ; i < items.size() ; i++ ) {
                Mascota masc = (Mascota) items.get(i);
                if (nombre.equals(masc.getNombre())) {
                    Log.i("debug", "existe una mascota con ese nombre");

                    items.remove(i);
                }

            }
            items.add(new Mascota(nombre, especie, raza, edad, cuidadoEspecial ));

        }

        mFabMascota = findViewById(R.id.fab_add_mascota);
        mFabMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrarMascotaIntent = new Intent(MenuMascotaActivity.this, RegistrarMascotaActivity.class);
                startActivity(registrarMascotaIntent);

            }
        });





        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lManager);

        final MascotaAdapter adapter = new MascotaAdapter(items);

        recycler.setAdapter(adapter);

        // int resId = R.anim.layout_animation_rotate_in;
        int resId = R.anim.layout_animation;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(MenuMascotaActivity.this, resId);
        recycler.setLayoutAnimation(animation);
        adapter.notifyDataSetChanged();

        adapter.setOnClickListener(new MascotaAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Mascota model) {
                Intent mascotaIntent = new Intent(MenuMascotaActivity.this, RegistrarMascotaActivity.class);
                mascotaIntent.putExtra("nombre", model.getNombre());
                mascotaIntent.putExtra("edad", model.getEdad());
                mascotaIntent.putExtra("raza", model.getRaza());
                mascotaIntent.putExtra("especie", model.getEspecie());
                mascotaIntent.putExtra("cuidadoEspecial", model.getCuidadoEspecial());

                startActivity(mascotaIntent);

            }
        });



    }
}