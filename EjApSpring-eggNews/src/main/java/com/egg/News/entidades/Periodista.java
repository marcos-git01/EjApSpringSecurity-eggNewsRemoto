package com.egg.News.entidades;

import com.egg.News.enumeraciones.Rol;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Periodista extends Usuario {
   
    Integer sueldoMensual;

    public Periodista() {
    }

    public Periodista(Integer sueldoMensual, String id, String nombre, String password, Date fecha, Rol rol, Boolean activo) {
        super(id, nombre, password, fecha, rol, activo);
        this.sueldoMensual = sueldoMensual;
    }

       
    public Integer getSueldoMensual() {
        return sueldoMensual;
    }

    public void setSueldoMensual(Integer sueldoMensual) {
        this.sueldoMensual = sueldoMensual;
    }

}
