package com.proyecto.grupo10.petshome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    private List<Servicio> items;
    private ServicioAdapter.OnClickListener onClickListener;

    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        //public ImageView imagenCancha;
        //public TextView nombreCancha;

        public TextView tipoServicio;
        public TextView descripcion;
        ImageView foto;



        public ServicioViewHolder(View v) {
            super(v);

            foto = (ImageView) v.findViewById(R.id.img_foto_servicio);
            tipoServicio = (TextView) v.findViewById(R.id.tv_tipo_servicio);
            descripcion = (TextView) v.findViewById(R.id.tv_descripcion);


        }
    }

    public ServicioAdapter(List<Servicio> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ServicioViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.servicio_cardview, viewGroup, false);
        return new ServicioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ServicioViewHolder viewHolder, int i) {

        viewHolder.descripcion.setText("Descripcion: " + items.get(i).descripcion);
        viewHolder.tipoServicio.setText("Tipo de servicio: "+ items.get(i).tipoServicio);
        viewHolder.foto.setVisibility(View.VISIBLE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(i, items.get(i));
                }
            }
        });

    }

    public void setOnClickListener(ServicioAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Servicio model);
    }


}
