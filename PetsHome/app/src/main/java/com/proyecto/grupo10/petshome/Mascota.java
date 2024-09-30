package com.proyecto.grupo10.petshome;

public class Mascota {

    String nombre;
    String especie;
    String raza;
    String edad;

    String cuidadoEspecial;

    public Mascota(String nombre, String especie, String raza, String edad, String cuidadoEspecial) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.cuidadoEspecial = cuidadoEspecial;
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
}
