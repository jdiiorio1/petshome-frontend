package com.proyecto.grupo10.petshome;

public class Mascota {

    String nombre;
    String especie;
    String raza;
    String edad;
    Integer idMascota, idTutor;
    String cuidadoEspecial;
    Integer foto;
    String url;

    public Mascota(Integer idMascota, String nombre, String especie, String raza, String edad, String cuidadoEspecial, Integer foto, String url) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.cuidadoEspecial = cuidadoEspecial;
        this.foto = foto;
        this.url = url;
    }

    public Mascota(String nombre, String especie, String raza, String edad, String cuidadoEspecial, Integer idTutor) {

        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.cuidadoEspecial = cuidadoEspecial;
        this.idTutor = idTutor;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getCuidadoEspecial() {
        return cuidadoEspecial;
    }

    public void setCuidadoEspecial(String cuidadoEspecial) {
        this.cuidadoEspecial = cuidadoEspecial;
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

    public Integer getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(Integer idTutor) {
        this.idTutor = idTutor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
