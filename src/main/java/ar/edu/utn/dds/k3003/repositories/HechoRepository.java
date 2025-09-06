package ar.edu.utn.dds.k3003.repositories;
import java.util.ArrayList;
import java.util.List;


import ar.edu.utn.dds.k3003.model.Hecho;

public class HechoRepository {
    private List<Hecho> hechos;
    public HechoRepository(){
        hechos = new ArrayList<>();
    }
    public List<Hecho> allHechos(){
        return this.hechos;
    }
    
    public Hecho save(Hecho hechoGuardar){
        hechos.add(hechoGuardar);
        return hechoGuardar;
    }
    public void delete(Hecho hechoEliminar){
        hechos.remove(hechoEliminar);
    }
    public Hecho findById(String id_hecho){
       Hecho hechoEncontrado = hechos.stream()
        .filter(hecho -> hecho.getId().equals(id_hecho))
        .findFirst()
        .orElse(null);
       return hechoEncontrado;
    }    
}