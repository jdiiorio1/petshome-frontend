package com.proyecto.grupo10.petshome;

public class Servicio {

    String descripcion;
    String tipoServicio;
    Integer idServicio;

    public Servicio(Integer idServicio, String descripcion, String tipoServicio){
        this.idServicio=idServicio;
        this.descripcion=descripcion;
        this.tipoServicio=tipoServicio;
    }

    public  String getDescripcion(){return descripcion;}
    public void setDescripcion(String descripcion){this.descripcion=descripcion;}

    public String getTipoServicio(){return tipoServicio;}
    public void setTipoServicio(String tipoServicio){this.tipoServicio=tipoServicio;}

    public Integer getIdServicio(){return idServicio;}
    public void setIdServicio(Integer idServicio){this.idServicio=idServicio;}
}
