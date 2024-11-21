package com.proyecto.grupo10.petshome;

import org.osmdroid.util.GeoPoint;

public class Cuidador {

    Integer idCuidador;
    String nombre;
    String apellido;
    String distancia;
    String resenias;
    String valoracion;
    GeoPoint ubicacion;
    Integer foto;
    String url;

    public Cuidador(String nombre, String distancia, String resenias, String valoracion, GeoPoint ubicacion) {
        this.nombre = nombre;
        this.distancia = distancia;
        this.resenias = resenias;
        this.valoracion = valoracion;
        this.ubicacion = ubicacion;
    }

    public Cuidador() {

    }
    public Cuidador(String nombre,  String resenias, String valoracion, GeoPoint ubicacion) {
        this.nombre = nombre;
        this.resenias = resenias;
        this.valoracion = valoracion;
        this.ubicacion = ubicacion;
    }

    public Cuidador(Integer idCuidador, String nombre, String apellido, GeoPoint ubicacion,String distancia,String resenias, String valoracion, Integer foto, String url) {
        this.idCuidador = idCuidador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.ubicacion = ubicacion;
        this.distancia = distancia;
        this.resenias = resenias;
        this.valoracion = valoracion;
        this.foto = foto;
        this.url = url;
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

    public Integer getIdCuidador() {
        return idCuidador;
    }

    public void setIdCuidador(Integer idCuidador) {
        this.idCuidador = idCuidador;
    }

    public Integer getFoto() {
        return foto;
    }

    public void setFoto(Integer foto) {
        this.foto = foto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
