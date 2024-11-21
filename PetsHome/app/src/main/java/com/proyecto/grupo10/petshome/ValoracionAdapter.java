package com.proyecto.grupo10.petshome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ValoracionAdapter extends RecyclerView.Adapter<ValoracionAdapter.ValoracionViewHolder> {

    private List<Valoracion> items;
    private Context context;
    private ValoracionAdapter.OnClickListener onClickListener;

    public static class ValoracionViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item


        public TextView mNombreTutor;
        public TextView mfecha;
        public TextView mNombreMascota;
        public TextView mComentario;
        public TextView mPuntuacion;

        ImageView foto;





        public ValoracionViewHolder(View v) {
            super(v);


            mNombreTutor = (TextView) v.findViewById(R.id.tv_tutor);
            mNombreMascota = (TextView) v.findViewById(R.id.tv_mascota);
            mfecha = (TextView) v.findViewById(R.id.tv_fecha);
            mComentario = (TextView) v.findViewById(R.id.tv_comentario);
            mPuntuacion = (TextView) v.findViewById(R.id.tv_puntuacion);
            foto = (ImageView) v.findViewById(R.id.img_foto_tutor);


        }
    }

    public ValoracionAdapter(Context context, List<Valoracion> items) {

        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Método para obtener el item en una posición específica
    public Valoracion getItem(int position) {
        return items.get(position);
    }


    @Override
    public ValoracionAdapter.ValoracionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.valoracion_cardview, viewGroup, false);
        return new ValoracionAdapter.ValoracionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ValoracionAdapter.ValoracionViewHolder viewHolder, int i) {

        String urlStr = items.get(i).getUrl() + "/usuario/imagen/" + items.get(i).getIdTutor(); // Asegúrate de que esta URL sea correcta
        Glide.with(context)
                .load(urlStr)
                .placeholder(R.drawable.cuidador1) // Imagen de carga
                .error(R.drawable.cuidador1) // Imagen de error
                .into(viewHolder.foto);

       // cargarFotoSiExiste(items.get(i).getIdTutor(), items.get(i).getUrl(), viewHolder.foto, items.get(i).getFoto());


        viewHolder.mNombreTutor.setText(items.get(i).getNombre() + " " +  items.get(i). getApellido());
        viewHolder.mNombreMascota.setText("Por cuidar a " + items.get(i).getNombreMascota() );
        viewHolder.mfecha.setText(items.get(i).getFechaFin() );
        viewHolder.mPuntuacion.setText(items.get(i).getPuntuacion().toString() );
        viewHolder.mComentario.setText(items.get(i).getComentario() );




        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(i, items.get(i));
                }
            }
        });

    }

    public void setOnClickListener(ValoracionAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Valoracion model);
    }

/*
    private void cargarFotoSiExiste(Integer idTutor, String url, ImageView imageView, int fotoGenerica) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = url + "/usuario/imagen/" + idTutor;
                    Log.i("debug", "URL Imagen en adapter: " + urlStr);
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int responseCode = urlConnection.getResponseCode();
                    Log.i("debug", "El response code de la imagen es : " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {


                        InputStream imageInputStream = urlConnection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream);

                        imageView.setImageBitmap(bitmap);
                        Log.i("debug", "Se cargo la imagen del adapter");
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

*/


}
