package com.proyecto.grupo10.petshome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HistorialCitasTutorAdapter extends RecyclerView.Adapter<HistorialCitasTutorAdapter.HistorialCitaTutorViewHolder>{


    private List<HistorialCitaTutor> items;
    private HistorialCitasTutorAdapter.OnClickListener onClickListener;

    public static class HistorialCitaTutorViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item


        public TextView mNombreCuidador;
        public TextView mfecha;
        public TextView mNombreMascota;


        ImageView foto;





        public HistorialCitaTutorViewHolder(View v) {
            super(v);

            mNombreCuidador = (TextView) v.findViewById(R.id.tv_nombre_cuidador);
            mNombreMascota = (TextView) v.findViewById(R.id.tv_nombre_mascota);
            mfecha = (TextView) v.findViewById(R.id.tv_fecha);
            foto = (ImageView) v.findViewById(R.id.img_foto_cuidador);




        }
    }

    public HistorialCitasTutorAdapter(List<HistorialCitaTutor> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Método para obtener el item en una posición específica
    public HistorialCitaTutor getItem(int position) {
        return items.get(position);
    }


    @Override
    public HistorialCitasTutorAdapter.HistorialCitaTutorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.historial_cita_tutor_cadview, viewGroup, false);
        return new HistorialCitasTutorAdapter.HistorialCitaTutorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistorialCitasTutorAdapter.HistorialCitaTutorViewHolder viewHolder, int i) {

        cargarFotoSiExiste(items.get(i).getIdCuidador(), items.get(i).getUrl(), viewHolder.foto, items.get(i).getFoto());


        viewHolder.mNombreCuidador.setText(items.get(i).getNombreCuidador());
        viewHolder.mNombreMascota.setText("Cuido de " + items.get(i).getNombreMascota() );
        viewHolder.mfecha.setText("Desde: " + items.get(i).getFechaInicio() + " hasta " + items.get(i).getFechaFin() );




        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(i, items.get(i));
                }
            }
        });

    }

    public void setOnClickListener(HistorialCitasTutorAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, HistorialCitaTutor model);
    }




    private void cargarFotoSiExiste(Integer idCuidador, String url, ImageView imageView, int fotoGenerica) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = url + "/usuario/imagen/" + idCuidador;
                    Log.i("debug", "URL Imagen en adapter: " + urlStr);
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int responseCode = urlConnection.getResponseCode();
                    Log.i("debug", "El response code de la imagen es : " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        Log.i("debug", "Se cargo la imagen del adapter");
                        InputStream imageInputStream = urlConnection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream);

                        imageView.setImageBitmap(bitmap);
                    } else {
                        Log.e("debug", "Error al cargar la imagen: " + responseCode);
                        imageView.setImageResource(fotoGenerica);
                    }

                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }






}
