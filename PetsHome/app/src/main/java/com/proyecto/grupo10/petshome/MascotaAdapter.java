package com.proyecto.grupo10.petshome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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



        public MascotaViewHolder(View v) {
            super(v);

            nombreMascota = (TextView) v.findViewById(R.id.tv_nombre_mascota);
            raza = (TextView) v.findViewById(R.id.tv_raza);
            especie = (TextView) v.findViewById(R.id.tv_especie);
            edadMascota = (TextView) v.findViewById(R.id.tv_edad_mascota);
            cuidadoEspecial = (TextView) v.findViewById(R.id.tv_cuidado_especial);



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


        viewHolder.nombreMascota.setText("Nombre:  " + items.get(i).getNombre());
        viewHolder.edadMascota.setText("Edad: " + items.get(i).getEdad());
        viewHolder.raza.setText("Raza: " + items.get(i).getRaza());
        viewHolder.especie.setText("Especie: " + items.get(i).getEspecie());
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


}
