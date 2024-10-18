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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;
import org.json.JSONObject;  // Importar para manejar JSON
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    boolean isReady = false;
    TextView mTvRegistrarse;
    ImageView mImgBackground;
    Button mbtnIngresar;
    EditText mNombre;
    EditText mPassword;

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
        mPassword = findViewById(R.id.et_password); // Nuevo campo para la contraseña
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
                iniciarSesion(email, password); // Llamamos a la función para iniciar sesión
            }
        });
    }

    private void iniciarSesion(String email, String password) {
        // Ejecutar la solicitud en un hilo separado para evitar bloquear el hilo principal
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construir la URL con los parámetros email y contraseña
                    String urlStr = "http://192.168.1.35:8081/usuario/findByEmail?email=" + email + "&password=" + password;
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Leer la respuesta del servidor
                        InputStream inputStream = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String response = result.toString();
                        Log.i("Login Response", response);

                        // Procesar el JSON de respuesta
                        JSONObject jsonResponse = new JSONObject(response);
                        int rol = jsonResponse.getInt("rol"); // Obtener el rol del usuario
                        int idUsuario = jsonResponse.getInt("idUsuario");
                        String nombre = jsonResponse.getString("nombre");
                        String apellido= jsonResponse.getString("apellido");
                        String email=jsonResponse.getString("email");
                        String pass=jsonResponse.getString("password");
                        Boolean mCuidador = null;


                        // Si el rol es =1 el usuario es cuidador, si es 0 es tutor
                        // redirijo al home del usuario segun el rol
                        Intent homeIntent;
                        if (rol == 1){
                            homeIntent = new Intent(MainActivity.this, MenuCuidadorActivity.class);
                        } else {
                            homeIntent = new Intent(MainActivity.this, MenuTutorActivity.class);
                        }

                        homeIntent.putExtra("idUsuario",idUsuario);
                        homeIntent.putExtra("nombre",nombre);
                        homeIntent.putExtra("apellido", apellido);
                        homeIntent.putExtra("email", email);
                        homeIntent.putExtra("pass", pass);

                        startActivity(homeIntent);


                    } else {
                        // Manejar error de autenticación
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("Login Error", "Error de inicio de sesión: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Mostrar un mensaje de error genérico en caso de excepción
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
