package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Pdi {
    private String id;
    private String hecho;
    private String lugar;
    private String descripcion;
    private String contenido;
    private String imageUrl;          // <--- NUEVO: url de la imagen asociada
    private List<String> etiquetas;
    private LocalDateTime momento;

    public Pdi(String _id,
               String _hechoId,
               String _lugar,
               String _descripcion,
               String _contenido,
               String _imageUrl,      // <--- NUEVO parámetro
               List<String> _etiquetas,
               LocalDateTime _momento) {
        this.id = _id;
        this.hecho = _hechoId;
        this.contenido = _contenido;
        this.descripcion = _descripcion;
        this.imageUrl = _imageUrl;    // <--- asignación nueva
        this.etiquetas = _etiquetas;
        this.lugar = _lugar;
        this.momento = _momento;
    }
}
