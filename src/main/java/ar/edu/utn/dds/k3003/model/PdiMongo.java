package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Document(collection = "hechos")
@Getter
@Setter
public class PdiMongo {

    @Id
    private Long id;
    @Field("hecho_id")
    private String hechoId;
    @Field("descripcion")
    private String descripcion;
    @Field("lugar")
    private String lugar;
    @Field("momento")
    private LocalDateTime momento;
    @Field("contenido")
    private String contenido;
    @Field("image_url")
    private String imageurl;
    @Field("tags")
    private List<String> autoTags = new ArrayList<>();

    public enum ProcessingState { PENDING, PROCESSING, PROCESSED, ERROR }

    public PdiMongo(String descripcion, String lugar, LocalDateTime momento,
        String contenido) {
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.momento = momento;
        this.contenido = contenido;
    }

    public PdiMongo(String id2, String hechoId2, String descripcion2, String lugar2, LocalDateTime momento2,
            String contenido2, String imageUrl2, List<String> etiquetas) {        
        this.imageurl=imageUrl2;
        this.autoTags = etiquetas;
        this.contenido=contenido2;
        this.descripcion=descripcion2;
                
    }

    public void setAutoTags(List<String> tags) {
        this.autoTags.clear();
        if (tags != null) this.autoTags.addAll(tags);
    }
}