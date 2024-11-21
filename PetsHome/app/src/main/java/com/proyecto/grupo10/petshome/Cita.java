package com.proyecto.grupo10.petshome;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cita {

    Integer idCuidador;
    String nombre;
    String nombreMascota;
    LocalDate fechaInicio;
    LocalDate fechaFin;
    Integer idMascota;
    String cuidadoEspecial;
    String estado;

    Integer foto;
    String url;

    public Cita(Integer idCuidador, Integer idMascota, LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        this.idCuidador = idCuidador;

        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.idMascota = idMascota;

        this.estado = estado;
    }

    public Cita() {

    }

    public Cita(Integer idCuidador, String nombre, String nombreMascota, LocalDate fechaInicio, LocalDate fechaFin, Integer idMascota,String cuidadoEspecial, Integer foto, String url) {
        this.idCuidador = idCuidador;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.idMascota = idMascota;
        this.nombreMascota = nombreMascota;
        this.cuidadoEspecial = cuidadoEspecial;
        this.foto = foto;
        this.url = url;
    }

    public Integer getIdCuidador() {
        return idCuidador;
    }

    public void setIdCuidador(Integer idCuidador) {
        this.idCuidador = idCuidador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getCuidadoEspecial() {
        return cuidadoEspecial;
    }

    public void setCuidadoEspecial(String cuidadoEspecial) {
        this.cuidadoEspecial = cuidadoEspecial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
