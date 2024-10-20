package com.proyecto.grupo10.petshome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CuidadorAdapter extends RecyclerView.Adapter<CuidadorAdapter.CuidadorViewHolder>{

    private List<Cuidador> items;
    private CuidadorAdapter.OnClickListener onClickListener;

    public static class CuidadorViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item


        public TextView mNombre;
        public TextView mDistancia;
        public TextView mResenia;
        public TextView mValoracion;





        public CuidadorViewHolder(View v) {
            super(v);

            mNombre = (TextView) v.findViewById(R.id.tv_nombre_cuidador);
            mDistancia = (TextView) v.findViewById(R.id.tv_distancia);
            mResenia = (TextView) v.findViewById(R.id.tv_resenias);
            mValoracion = (TextView) v.findViewById(R.id.tv_valoracion);




        }
    }

    public CuidadorAdapter(List<Cuidador> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Método para obtener el item en una posición específica
    public Cuidador getItem(int position) {
        return items.get(position);
    }


    @Override
    public CuidadorAdapter.CuidadorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cuidador_cardview, viewGroup, false);
        return new CuidadorAdapter.CuidadorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CuidadorAdapter.CuidadorViewHolder viewHolder, int i) {


        viewHolder.mNombre.setText(items.get(i).getNombre());
        viewHolder.mDistancia.setText("A " + items.get(i).getDistancia());
        viewHolder.mResenia.setText("Reseñas: " + items.get(i).getResenias());
        viewHolder.mValoracion.setText(items.get(i).getValoracion());


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(i, items.get(i));
                }
            }
        });

    }

    public void setOnClickListener(CuidadorAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Cuidador model);
    }


}
