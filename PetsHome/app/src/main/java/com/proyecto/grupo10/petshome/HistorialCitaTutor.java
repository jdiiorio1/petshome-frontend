package com.proyecto.grupo10.petshome;

import java.time.LocalDate;

public class HistorialCitaTutor {

    Integer idCita;
    String nombreCuidador;
    Integer idCuidador;
    String nombreMascota;
    LocalDate fechaInicio;
    LocalDate fechaFin;
    Integer idMascota;

    Integer foto;
    String url;

    public HistorialCitaTutor(Integer idCita, String nombreCuidador, String nombreMascota, LocalDate fechaInicio, LocalDate fechaFin, Integer idMascota, Integer foto, String url, Integer idCuidador) {
        this.idCita = idCita;
        this.nombreCuidador = nombreCuidador;
        this.nombreMascota = nombreMascota;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.idMascota = idMascota;
        this.foto = foto;
        this.url = url;
        this.idCuidador = idCuidador;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public String getNombreCuidador() {
        return nombreCuidador;
    }

    public void setNombreCuidador(String nombreCuidador) {
        this.nombreCuidador = nombreCuidador;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(Integer idMascota) {
        this.idMascota = idMascota;
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

    public Integer getIdCuidador() {
        return idCuidador;
    }

    public void setIdCuidador(Integer idCuidador) {
        this.idCuidador = idCuidador;
    }
}
