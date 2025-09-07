package ar.edu.utn.dds.k3003.model;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public class Pdi {
    private String id;
    private String hecho;
    private String lugar;
    private String descripcion;
    private String contenido;
    private List<String> etiquetas;
    private LocalDateTime momento;
    public Pdi(String _id,String _hechoId, String _lugar,String _descripcion,String _contenido,List<String> _etiquetas, LocalDateTime _momento){
        this.id = _id;
        this.hecho = _hechoId;
        this.contenido = _contenido;
        this.descripcion = _descripcion;
        this.etiquetas = _etiquetas;
        this.lugar = _lugar;
        this.momento = _momento;
    }
    public String getId(){
        return this.id;
    }
    public String getHecho(){
        return this.hecho;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public String getContenido(){
        return this.contenido;
    }
    public String getLugar(){
        return this.lugar;
    }
    public LocalDateTime getMomento(){
        return this.momento;
    }
    public List<String> getEtiquetas(){
        return this.etiquetas;
    }
    public void setId(String nuevoId){
        this.id = nuevoId;
    }
    public void setHecho(String nuevoIdHecho){
        this.hecho = nuevoIdHecho;
    }
    public void setLugar(String nuevoLugar){
        this.lugar = nuevoLugar;
    }
    public void setContenido(String nuevaDescripcion){
        this.contenido = nuevaDescripcion;
    }
    public void setDescripcion(String nuevaDescripcion){
        this.descripcion = nuevaDescripcion;
    }
    public void setEtiquetas(List<String> nuevasEtiquetas){
        this.etiquetas = nuevasEtiquetas;
    }
    public void setMomento(LocalDateTime nuevoMomento){
        this.momento = nuevoMomento;
    }
}
