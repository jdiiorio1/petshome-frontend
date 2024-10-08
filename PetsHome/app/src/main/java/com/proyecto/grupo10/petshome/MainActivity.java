package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreen.Companion.*;


public class MainActivity extends AppCompatActivity {

    boolean isReady = false;
    TextView mTvRegistrarse;
    ImageView mImgBackground;
    Button mbtnIngresar;

    EditText mNombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splash = SplashScreen.installSplashScreen(this);
        View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isReady) {
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                dismissSplashScreen();
                return false;
            }
        });
        setContentView(R.layout.activity_main);

        mNombre = findViewById(R.id.et_nombre);
        mbtnIngresar = findViewById(R.id.btn_ingresar);
        mTvRegistrarse = findViewById(R.id.tv_registrese);
        mImgBackground = findViewById(R.id.img_background);
        mImgBackground.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in));

        mTvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
                startActivity(intent);
            }
        });

        mbtnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("debug", "el valor del campo nombre es: " + mNombre.getText());
                Intent homeIntent;
                if ("cuidador".equals(mNombre.getText().toString())) {
                    Log.i("debug","Entro como cuidador");
                    homeIntent = new Intent(MainActivity.this, MenuCuidadorActivity.class);
                } else {
                    homeIntent = new Intent(MainActivity.this, MenuTutorActivity.class);
                }

                homeIntent.putExtra("usuario", mNombre.getText());
                startActivity(homeIntent);
            }
        });




    }

    private void dismissSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isReady = true;
            }
        },1000);
    }
}