package com.proyecto.grupo10.petshome;

import org.osmdroid.util.GeoPoint;

public class Cuidador {

    String nombre;
    String distancia;
    String resenias;
    String valoracion;
    GeoPoint ubicacion;

    public Cuidador(String nombre, String distancia, String resenias, String valoracion, GeoPoint ubicacion) {
        this.nombre = nombre;
        this.distancia = distancia;
        this.resenias = resenias;
        this.valoracion = valoracion;
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getResenias() {
        return resenias;
    }

    public void setResenias(String resenias) {
        this.resenias = resenias;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public GeoPoint getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.ubicacion = ubicacion;
    }
}
