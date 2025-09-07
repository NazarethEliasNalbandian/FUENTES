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
    private List<String> etiquetas;
    private LocalDateTime momento;

    public Pdi(String _id, String _hechoId, String _lugar, String _descripcion,
               String _contenido, List<String> _etiquetas, LocalDateTime _momento) {
        this.id = _id;
        this.hecho = _hechoId;
        this.contenido = _contenido;
        this.descripcion = _descripcion;
        this.etiquetas = _etiquetas;
        this.lugar = _lugar;
        this.momento = _momento;
    }

    // Getters/Setters explícitos no son necesarios porque @Data ya los genera.
    // Si preferís sin Lombok, borra @Data y dejá los getters/setters que ya tenías.
}
