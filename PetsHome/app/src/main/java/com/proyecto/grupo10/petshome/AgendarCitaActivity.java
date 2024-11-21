package com.proyecto.grupo10.petshome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgendarCitaActivity extends AppCompatActivity {

    Properties configProperties = new Properties();
    private TextView mFechaInicio, mFechaFin, mServicios;
    private Button mAgendarCita;
    Integer idTutor, idCuidador;
    private boolean[] seleccionados;
    private List mascotasArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

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

        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            idTutor = extras.getInt("idTutor");
            idCuidador = extras.getInt("idCuidador");
            Log.i("debug", "el id del tutor es: " + idTutor);
        }

        /**
         * Inicializo los controles de pantalla
         */
        mFechaInicio = findViewById(R.id.tv_filtro_inicio);
        mFechaFin = findViewById(R.id.tv_filtro_fin);
        mAgendarCita = findViewById(R.id.btn_agendar_cita);
        mServicios = findViewById(R.id.tv_filtro_mascota);

        cargarMascotas(idTutor);





        /**
         * Cargo los listener de los filtros
         */
        mFechaInicio.setOnClickListener(calendarioOnClickListener);
        mFechaFin.setOnClickListener(calendarioOnClickListener);

        List<String> idsMascotas = new ArrayList<>();

        mAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("debug", "Fecha Inicio:" + mFechaInicio.getText().toString() + " Fecha Fin:" + mFechaFin.getText().toString() + " Mascotas:" + mServicios.getText().toString());

                // Dividir el String en partes usando " | "
                String[] partes = mServicios.getText().toString().split(" \\| "); // Espacio antes y después del '|'

                // Iterar sobre las partes
                for (String parte : partes) {
                    // Dividir cada parte en el carácter '-'
                    String[] subPartes = parte.split("-");
                    if (subPartes.length > 0) {
                        // Agregar el primer elemento (el número) a la lista
                        idsMascotas.add(subPartes[0]);
                        Log.i("debug", "El id de mascota es: "+ subPartes[0] );

                        try {
                            int idMascota = Integer.parseInt(subPartes[0]);
                            Log.i("debug", "El id de mascota en entero es: "+ idMascota );
                            registrarCita(idMascota);
                        } catch (NumberFormatException e) {
                            Log.e("debug", "Error al convertir el ID de mascota: " + subPartes[0], e);
                        }
                    }
                }

                //registrarCita();

            }
        });

        mServicios.setOnClickListener(v -> mostrarSeleccion(mascotasArray));


    }


    /**
     * Selector de fecha
     */

    public View.OnClickListener calendarioOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final TextView textView = (TextView) v;
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mAnio = c.get(Calendar.YEAR);
            int mMes = c.get(Calendar.MONTH);
            int mSemana = c.get(Calendar.WEEK_OF_YEAR);
            int mDia = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(AgendarCitaActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {


                            //tvCalendario.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            textView.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mAnio, mMes, mDia);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);



            datePickerDialog.show();

        }
    };

    private void mostrarSeleccion(List<String> listaMascotas) {
        Log.i("debug", "Llego a mostrar seleccion: " + listaMascotas.toString() );
        String[] mascotas = listaMascotas.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mascotas a agregar a la cita")
                .setMultiChoiceItems(mascotas, seleccionados, (dialog, which, isChecked) -> {
                    seleccionados[which] = isChecked;
                })
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    // Manejar los elementos seleccionados
                    StringBuilder seleccionadosString = new StringBuilder("" );
                    int cant = 0;



                    for (int i = 0; i < seleccionados.length; i++) {
                        if (seleccionados[i]) {
                            cant++;
                            seleccionadosString.append(mascotas[i]).append(" | ");
                        }
                    }
/*
                    if (cant > 0) {
                        seleccionadosString.append("(" + cant + ")");
                    }
*/

                    // Eliminar la última coma y espacio
                    if (seleccionadosString.length() > 5) {
                        seleccionadosString.setLength(seleccionadosString.length() - 2);
                    } else {
                        seleccionadosString.append("ninguno");
                    }

                    Log.i("debug", "mascotas seleccionadas: " + seleccionadosString.toString());
                    mServicios.setText(seleccionadosString.toString());
                })
                .setNegativeButton("Cancelar", null);
        builder.create().show();
    }


    private void cargarMascotas(int idTutor) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = configProperties.getProperty("url") + "/mascota/mascotas/" + idTutor;
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
                        Log.i("debug", response);

                        JSONArray jsonArray = new JSONArray(response); // Cambia a JSONArray
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i); // Obtener cada objeto JSON

                            String nombre = jsonObject.getString("nombre");
                            int idMascota = jsonObject.getInt("idMascota");

                            Log.i("debug", "Mascota: " + nombre );
                            mascotasArray.add((String) String.valueOf(idMascota) + "-" + nombre);

                        }

                        Log.i("debug", "Lista mascotas: " + mascotasArray.toString() );

                        /**
                         * Selecciono mascotas
                         */
                        seleccionados = new boolean[mascotasArray.size()];
                        Log.i("debug", "Seleccionados: " + seleccionados.toString() );



                    } else {
                        runOnUiThread(() -> {
                            Log.i("debug", "Error al obtener mascota");
                        });
                        Log.e("Login Error", "Error de conexion: " + responseCode);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Log.i("debug", "Error de la mascota");
                    });
                }
            }
        }).start();
    }


    public void registrarCita(int idMascota) {

            String estado = "confirmado";
            LocalDate fechainicio = LocalDate.parse(mFechaInicio.getText().toString());
            LocalDate fechaFin = LocalDate.parse(mFechaFin.getText().toString());
        Log.i("debug","Id que llega para cargar" + idMascota);


        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection urlConnection = null;
                try {

                    String metodo = "";
                    URL url;
                        url = new URL(configProperties.getProperty("url") + "/cita");
                        Log.i("debug", "url" + url);
                        metodo = "POST";


                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(metodo);
                    Log.i("debug",conn.getRequestMethod().toString());
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    Log.i("debug", "idcuidador:" + idCuidador + " id mascota:" + idMascota + "fecha inicio:" + fechainicio.toString() + " fecha fin " + fechaFin.toString());

                    JSONObject json = new JSONObject();
                    json.put("estado", "confirmado");
                    json.put("idCuidador", idCuidador);
                    json.put("idMascota", idMascota);
                    json.put("fechaInicio", fechainicio);
                    json.put("fechaFin", fechaFin);

                    Log.i("debug", "JSON enviado: " + json.toString());




                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgendarCitaActivity.this);
                            builder.setMessage("Se guardaron los cambios.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent tutorIntent = new Intent(AgendarCitaActivity.this, BuscarCuidadorActivity.class);

                                            tutorIntent.putExtra("idUsuario", idCuidador);


                                            startActivity(tutorIntent);
                                            finish(); // Cerrar la actividad
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        });
                    } else {
                        runOnUiThread(() -> {
                            Log.i("debug", "error en el registro");

                            Log.i("debug", "Código de respuesta: " + responseCode);

                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Log.i("debug", "error en la consulta");
                    });
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                        Log.i("debug", "error ");
                    }
                }
            }
        }).start();

    }


}