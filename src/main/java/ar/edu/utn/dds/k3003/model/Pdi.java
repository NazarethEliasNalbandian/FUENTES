package ar.edu.utn.dds.k3003.model;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
@Data
@Entity 
public class Pdi {
    @Id
    private String id;
    @Column
    private String hecho;
    @Column
    private String lugar;
    @Column
    private String descripcion;
    @Column
    private String contenido;
    @ElementCollection
    private List<String> etiquetas;
    @Column
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
