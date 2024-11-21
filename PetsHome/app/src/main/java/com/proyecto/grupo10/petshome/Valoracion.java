package com.proyecto.grupo10.petshome;

public class Valoracion {

    Integer idTutor;
    String nombre;
    String nombreMascota;
    String apellido;
    Integer puntuacion;
    String fechaFin;
    String comentario;

    Integer foto;
    String url;


    public Valoracion(Integer idTutor, String nombre, String nombreMascota, String apellido, Integer puntuacion, String fechaFin, String comentario, Integer foto, String url) {
        this.idTutor = idTutor;
        this.nombre = nombre;
        this.nombreMascota = nombreMascota;
        this.apellido = apellido;
        this.puntuacion = puntuacion;
        this.fechaFin = fechaFin;
        this.comentario = comentario;
        this.foto = foto;
        this.url = url;
    }

    public Integer getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(Integer idTutor) {
        this.idTutor = idTutor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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
}
