package com.proyecto.grupo10.petshome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private List<Mascota> items;
    private MascotaAdapter.OnClickListener onClickListener;




    public static class MascotaViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        //public ImageView imagenCancha;
        //public TextView nombreCancha;

        public TextView nombreMascota;
        public TextView raza;
        public TextView especie;
        public TextView edadMascota;

        public TextView cuidadoEspecial;

        ImageView foto;




        public MascotaViewHolder(View v) {
            super(v);

            nombreMascota = (TextView) v.findViewById(R.id.tv_nombre_mascota);
            raza = (TextView) v.findViewById(R.id.tv_raza);
            especie = (TextView) v.findViewById(R.id.tv_especie);
            edadMascota = (TextView) v.findViewById(R.id.tv_edad_mascota);
            cuidadoEspecial = (TextView) v.findViewById(R.id.tv_cuidado_especial);
            foto = (ImageView) v.findViewById(R.id.img_foto_mascota);



        }
    }

    public MascotaAdapter(List<Mascota> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public MascotaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mascota_cardview, viewGroup, false);
        return new MascotaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MascotaViewHolder viewHolder, int i) {

        cargarFotoSiExiste(items.get(i).getIdMascota(), items.get(i).getUrl(), viewHolder.foto, items.get(i).getFoto());

        viewHolder.nombreMascota.setText("Nombre:  " + items.get(i).getNombre());
        viewHolder.edadMascota.setText("Edad: " + items.get(i).getEdad());
        viewHolder.raza.setText("Raza: " + items.get(i).getRaza());
        viewHolder.especie.setText("Especie: " + items.get(i).getEspecie());


/*
        if (mbitmap != null) {

            Log.i("debug", "Cargo imagen del adapter para " + items.get(i).getNombre());
            viewHolder.foto.setImageBitmap(mbitmap);
        } else {
            Log.i("debug", "Cargo foto generica para " + items.get(i).getNombre());
            viewHolder.foto.setImageResource(items.get(i).getFoto());
        }
*/
        if (items.get(i).getCuidadoEspecial().isEmpty()) {
            viewHolder.cuidadoEspecial.setVisibility(View.GONE);
        } else {
            viewHolder.cuidadoEspecial.setText("Cuidado especial: " + items.get(i).getCuidadoEspecial());
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(i, items.get(i));
                }
            }
        });

    }

    public void setOnClickListener(MascotaAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Mascota model);
    }

    private void cargarFotoSiExiste(Integer idMascota, String url, ImageView imageView, int fotoGenerica) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = url + "/mascota/imagen/" + idMascota;
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
