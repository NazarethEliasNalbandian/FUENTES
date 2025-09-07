package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Coleccion;

import java.util.ArrayList;
import java.util.List;


public class ColeccionRepository {
    List<Coleccion> colecciones;
    public ColeccionRepository(){
        colecciones = new ArrayList<>();
    }
    public Coleccion save(Coleccion coleccionGuardar){
        colecciones.add(coleccionGuardar);
        return coleccionGuardar;
    }
    public void delete(Coleccion coleccionEliminar){
        colecciones.remove(coleccionEliminar);
    }
    public List<Coleccion> allColeccciones(){
        return this.colecciones;
    }
    public Coleccion findById(String id_coleccion){
       Coleccion coleccionEncontrada = colecciones.stream()
        .filter(coleccion -> coleccion.getNombre().equals(id_coleccion))
        .findFirst()
        .orElse(null);
       return coleccionEncontrada;
    }  
}
