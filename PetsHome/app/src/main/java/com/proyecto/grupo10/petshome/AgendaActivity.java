package com.proyecto.grupo10.petshome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CalendarView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgendaActivity extends AppCompatActivity {

    com.applandeo.materialcalendarview.CalendarView calendarView;
    Calendar calendar;
    Integer idCuidador;
    String nombre, apellido, email, pass;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    List citas = new ArrayList();
    //CitaAdapter adapter;
    List<CalendarDay> calendarDays = new ArrayList<>();
    Properties configProperties = new Properties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

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
            idCuidador = extras.getInt("idUsuario");
            nombre = extras.getString("nombre");
            apellido = extras.getString("apellido");
            email = extras.getString("email");
            pass = extras.getString("pass");

        }

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador_agenda);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lManager);

        adapter = new CitaAdapter(AgendaActivity.this, citas);

        recycler.setAdapter(adapter);

        // int resId = R.anim.layout_animation_rotate_in;
        int resId = R.anim.layout_animation;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(AgendaActivity.this, resId);
        recycler.setLayoutAnimation(animation);







        calendarView = findViewById(R.id.calendario);
        calendar = Calendar.getInstance();

        calendarDays = new ArrayList<>();




        calendarView.setOnCalendarDayClickListener(new OnCalendarDayClickListener() {
            @Override
            public void onClick(@NonNull CalendarDay calendarDay) {
                Calendar clickedDayCalendar = calendarDay.getCalendar();
                citas.clear();
                recycler.requestLayout();

                Date date = new Date(clickedDayCalendar.getTime().toString());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String fechaFormateada = formatter.format(date);

                Log.i("debug", "toque la fecha:" + fechaFormateada);

                listarCitasDelCuidador(idCuidador, fechaFormateada, new CitasCallback() {

                    public void onCitasListReceived(List<Cita> citas) {

                        Log.i("debug", "entro luego de cargar la lista con la cantidad: " + citas.size());

                        // Actualiza la lista de citas en el adaptador

                       // adapter.updateCitas(citas);

                        //citas.clear();
                        adapter.notifyDataSetChanged();
                        recycler.requestLayout();




                    }

                }) ;

            }
        });


        // Cargar citas del cuidador
        Log.i("debug", "cuidador id: " + idCuidador );
        // mascotas =
        listarCitasDelCuidador(idCuidador, "", new CitasCallback() {

            public void onCitasListReceived(List<Cita> citas) {
                Log.i("debug", "entro luego de cargar la lista con la cantidad: " + citas.size());

                adapter.notifyDataSetChanged();


            }

        }) ;





    }


    private void listarCitasDelCuidador(int idCuidador, String fechaclick, CitasCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String urlStr = "";
                if (fechaclick.isEmpty()) {
                    urlStr = configProperties.getProperty("url") + "/cita/cardview/all/" + idCuidador;
                } else {
                    urlStr = configProperties.getProperty("url") + "/cita/cardview/" + idCuidador + "?fecha=" + fechaclick;
                }

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
                    citas.clear();
                    JSONArray jsonArray = new JSONArray(response); // Cambia a JSONArray
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i); // Obtener cada objeto JSON
                        int idMascota = jsonObject.getInt("idMascota");
                        String nombreMascota = jsonObject.getString("nombreMascota");
                        String nombreUsuario = jsonObject.getString("nombreUsuario");
                        String cuidadoEspecial = jsonObject.getString("cuidadoEspecial");
                        int edad = jsonObject.getInt("idUsuario");
                        String fechaInicio = jsonObject.getString("fechaInicio");
                        String fechaFin = jsonObject.getString("fechaFin");
                       // String estado = jsonObject.getString("estado");


                        int fotoMascota = getResources().getIdentifier("gato", "drawable", getPackageName());

                        if(fechaclick.isEmpty()) {
                            List<LocalDate> fechas = obtenerFechasIntermedias(fechaInicio, fechaFin);

                            // Imprimir las fechas intermedias
                            for (LocalDate fecha : fechas) {
                                Log.i("debug", "Fechas Ocupadas: " + fecha);

                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(fecha.getYear(), fecha.getMonthValue()-1, fecha.getDayOfMonth()); // Establecer la fecha
                                CalendarDay calendarDay1 = new CalendarDay(calendar1); // Crear CalendarDay
                                calendarDay1.setImageResource(R.drawable.ic_patita);
                                calendarDay1.setLabelColor(R.color.white);
                                calendarDay1.setBackgroundResource(R.color.verde_claro);
                                calendarDays.add(calendarDay1);
                            }
                            calendarView.setCalendarDays(calendarDays);

                        }

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            LocalDate hoy = LocalDate.now();
                            Log.i("debug", "La fecha actual es:" + hoy.toString());

                       // if (!fechaFin.isEmpty()) {
                       //     if (LocalDate.parse(fechaFin).isAfter(hoy) || !fechaclick.isEmpty()) {
                                Log.i("debug", "Cita IdCuidador: " + idCuidador + ", fechaInicio: " + fechaInicio + ", FechaFin: " + fechaFin);
                                citas.add(new Cita(idCuidador, nombreUsuario, nombreMascota, LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin), idMascota, cuidadoEspecial, fotoMascota, configProperties.getProperty("url")));
                       //     }
                       // }

                    }



                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged(); // Asegúrate de notificar al adaptador aquí
                        recycler.requestLayout();
                        callback.onCitasListReceived(citas);

                    });
                } else {
                    runOnUiThread(() -> {
                        citas.clear();
                        adapter.notifyDataSetChanged();
                        recycler.requestLayout();


                    });
                    Log.e("debug", "no hay datos para mostrar: " + responseCode);
                    citas.clear();
                    adapter.notifyDataSetChanged();
                    recycler.requestLayout();

                }

                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {

                });
            }
        });

        // Shutdown the executor if no longer needed (optional)
        // executor.shutdown();
    }

    public interface CitasCallback {
        void onCitasListReceived(List<Cita> citas);
    }

    public static List<LocalDate> obtenerFechasIntermedias(String fechaInicio, String fechaFin) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate inicio = LocalDate.parse(fechaInicio, formatter);
        LocalDate fin = LocalDate.parse(fechaFin, formatter);

        List<LocalDate> fechasIntermedias = new ArrayList<>();
        fechasIntermedias.add(inicio);


        // Validar si las fechas son iguales
        if (inicio.isEqual(fin)) {
           // fechasIntermedias.add(inicio);
            return fechasIntermedias;
        }

        // Iterar desde la fecha de inicio hasta la fecha de fin
        for (LocalDate date = inicio.plusDays(1); date.isBefore(fin); date = date.plusDays(1)) {
            fechasIntermedias.add(date);
        }

        fechasIntermedias.add(fin);

        return fechasIntermedias;
    }


    @Override
    public void onBackPressed() {

        Intent homeIntent = new Intent(AgendaActivity.this, MenuCuidadorActivity.class);
        homeIntent.putExtra("idUsuario", idCuidador);
        homeIntent.putExtra("nombre", nombre);
        homeIntent.putExtra("apellido", apellido);
        homeIntent.putExtra("email", email);
        homeIntent.putExtra("pass", pass);
        homeIntent.putExtra("esCuidador", true);
        startActivity(homeIntent);
        finish();

    }




}