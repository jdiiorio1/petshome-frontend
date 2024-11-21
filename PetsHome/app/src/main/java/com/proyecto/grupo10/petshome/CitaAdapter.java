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

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder>{


    private List<Cita> items;
    private Context context;
    private CitaAdapter.OnClickListener onClickListener;

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item


        public TextView mNombreTutor;
        public TextView mfecha;
        public TextView mNombreMascota;
        public TextView mCuidadoEspecial;

        ImageView foto;





        public CitaViewHolder(View v) {
            super(v);

            mNombreTutor = (TextView) v.findViewById(R.id.tv_nombre_tutor);
            mNombreMascota = (TextView) v.findViewById(R.id.tv_mascota);
            mfecha = (TextView) v.findViewById(R.id.tv_fecha);
            mCuidadoEspecial = (TextView) v.findViewById(R.id.tv_cuidado_esp);
            foto = (ImageView) v.findViewById(R.id.img_foto_mascota);




        }
    }

    public CitaAdapter(Context context, List<Cita> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Método para obtener el item en una posición específica
    public Cita getItem(int position) {
        return items.get(position);
    }


    @Override
    public CitaAdapter.CitaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cita_cardview, viewGroup, false);
        return new CitaAdapter.CitaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CitaAdapter.CitaViewHolder viewHolder, int i) {

        String urlStr = items.get(i).getUrl() + "/mascota/imagen/" + items.get(i).getIdMascota(); // Asegúrate de que esta URL sea correcta
        Glide.with(context)
                .load(urlStr)
                .placeholder(R.drawable.gato) // Imagen de carga
                .error(R.drawable.gato) // Imagen de error
                .into(viewHolder.foto);

      //  cargarFotoSiExiste(items.get(i).getIdMascota(), items.get(i).getUrl(), viewHolder.foto, items.get(i).getFoto());


        viewHolder.mNombreTutor.setText("Mascota de "+ items.get(i).getNombre());
        viewHolder.mNombreMascota.setText("Cuidas a " + items.get(i).getNombreMascota() );
        viewHolder.mfecha.setText("Desde: " + items.get(i).getFechaInicio() + " hasta " + items.get(i).getFechaFin() );
        if (items.get(i).getCuidadoEspecial().isEmpty()) {
            viewHolder.mCuidadoEspecial.setVisibility(View.GONE);
        } else {
            viewHolder.mCuidadoEspecial.setText("Cuidado especial: " + items.get(i).getCuidadoEspecial());
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

    public void setOnClickListener(CitaAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Cita model);
    }

    public void updateCitas(List<Cita> nuevasCitas) {

        this.items.clear(); // Limpia la lista actual
        this.items.addAll(nuevasCitas); // Agrega las nuevas citas
        notifyDataSetChanged(); // Notifica que los datos han cambiado

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
