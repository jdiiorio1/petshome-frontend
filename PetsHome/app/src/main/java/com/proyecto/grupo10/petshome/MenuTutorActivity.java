package com.proyecto.grupo10.petshome;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuTutorActivity extends AppCompatActivity {

    CardView mBuscarCuidadores;
    CardView mMisMascotas;
    CardView mHistorialCuidados;
    CardView mMensajes;

    ImageView mEditarPerfil;
    ImageView mImgBackground;
    TextView mSaludo;

    String nombre, apellido, email, pass;
    Integer idUsuario;
    ActivityResultLauncher<Intent> resultlauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_tutor);


        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            idUsuario = extras.getInt("idUsuario");
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");

        }

        mEditarPerfil = findViewById(R.id.img_editar_perfil);

        mSaludo = findViewById(R.id.tv_saludo);
        mSaludo.setText(nombre + " " + apellido);
        mBuscarCuidadores = findViewById(R.id.cv_buscar_cuidadores);
        mMisMascotas = findViewById(R.id.cv_mis_mascotas);
        mHistorialCuidados = findViewById(R.id.cv_historial_cuidadores);
        mMensajes = findViewById(R.id.cv_mensajes);
        mImgBackground = findViewById(R.id.img_background);

        registerResult();

        mImgBackground.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in));

        mBuscarCuidadores.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mBuscarCuidadores.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBuscarCuidadores.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
            }
        }, 300);

        mMisMascotas.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in_out));
        mMisMascotas.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMisMascotas.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out_in));
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


        mMisMascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "Ingreso al cardview de mis macotas");
                Intent mascotasIntent = new Intent(MenuTutorActivity.this, MenuMascotaActivity.class);
                mascotasIntent.putExtra("idTutor", idUsuario);
                startActivity(mascotasIntent);
                finish();
            }
        });

        mBuscarCuidadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "Ingreso al cardview de buscar cuidador");
                Intent buscarCuidadorIntent = new Intent(MenuTutorActivity.this, BuscarCuidadorActivity.class);
                startActivity(buscarCuidadorIntent);
                finish();
            }
        });

        mEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent perfilIntent = new Intent(MenuTutorActivity.this, EditarUsuarioActivity.class);
                perfilIntent.putExtra("nombre", nombre);
                perfilIntent.putExtra("apellido", apellido);
                perfilIntent.putExtra("email", email);
                perfilIntent.putExtra("pass", pass);

                startActivity(perfilIntent);
            }
        });

        mMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuTutorActivity.this, EnConstruccionActivity.class);
                startActivity(intent);
            }
        });

        mHistorialCuidados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuTutorActivity.this, EnConstruccionActivity.class);
                startActivity(intent);
            }
        });

        mImgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeryIntent = new Intent(Intent.ACTION_PICK);
                galeryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                resultlauncher.launch(galeryIntent);
            }
        });
    }

    private void registerResult() {

        resultlauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try {
                            Uri imageUri = o.getData().getData();
                            mImgBackground.setImageURI(imageUri);

                        } catch(Exception e) {
                            Toast.makeText(MenuTutorActivity.this,"No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}