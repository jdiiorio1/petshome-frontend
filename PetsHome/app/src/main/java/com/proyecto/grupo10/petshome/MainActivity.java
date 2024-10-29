package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.splashscreen.SplashScreen;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    boolean isReady = false;
    TextView mTvRegistrarse;
    ImageView mImgBackground;
    Button mbtnIngresar;
    EditText mNombre;
    EditText mPassword;
    Properties configProperties = new Properties();

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


        /***
         *
         * CARGO ARCHIVO PROPERTIES CON LA IP DE CADA UNO
         *
         */

        try {
            InputStream inputStream = this.getAssets().open("config.properties");
            configProperties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /**
         * PRUEBO QUE FUNCIONE LA INVOCACION A LA VARIABLE
         */
        Log.i("debug", "La URL obtenida del archivo properties es: " + configProperties.getProperty("url"));

        mNombre = findViewById(R.id.et_nombre);
        mPassword = findViewById(R.id.et_password);
        mbtnIngresar = findViewById(R.id.btn_ingresar);
        mTvRegistrarse = findViewById(R.id.tv_registrese);
        mImgBackground = findViewById(R.id.img_background);
        mImgBackground.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in));

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
                String email = mNombre.getText().toString();
                String password = mPassword.getText().toString();
                iniciarSesion(email, password);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Limpia los campos solo si es un nuevo inicio
        if (getIntent().getBooleanExtra("CLEAR_FIELDS", false)) {
            mNombre.setText("");
            mPassword.setText("");
            getIntent().removeExtra("CLEAR_FIELDS");
        }
    }

    private void iniciarSesion(String email, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = configProperties.getProperty("url") + "/usuario/findByEmail?email=" + email + "&password=" + password;
                    Log.i("debug", "URL: " + urlStr);
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String response = result.toString();
                        Log.i("Login Response", response);

                        JSONObject jsonResponse = new JSONObject(response);
                        int rol = jsonResponse.getInt("rol");
                        int idUsuario = jsonResponse.getInt("idUsuario");
                        String nombre = jsonResponse.getString("nombre");
                        String apellido = jsonResponse.getString("apellido");
                        String email = jsonResponse.getString("email");
                        String pass = jsonResponse.getString("password");

                        Intent homeIntent;
                        if (rol == 1) {
                            homeIntent = new Intent(MainActivity.this, MenuCuidadorActivity.class);
                        } else {
                            homeIntent = new Intent(MainActivity.this, MenuTutorActivity.class);
                        }

                        homeIntent.putExtra("idUsuario", idUsuario);
                        homeIntent.putExtra("nombre", nombre);
                        homeIntent.putExtra("apellido", apellido);
                        homeIntent.putExtra("email", email);
                        homeIntent.putExtra("pass", pass);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("Login Error", "Error de inicio de sesión: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void dismissSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isReady = true;
            }
        }, 1000);
    }
}
