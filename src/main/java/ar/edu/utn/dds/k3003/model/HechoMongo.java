package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import lombok.Data;

@Data
@Document(collection = "hechos")
public class HechoMongo {

    @Id
    private String id;

    @Field("nombre_coleccion")
    private String nombreColeccion;

    @Field("titulo")
    private String titulo;

    @Field("etiquetas")
    private List<String> etiquetas;

    @Field("categoria")
    private CategoriaHechoEnum categoria;

    @Field("ubicacion")
    private String ubicacion;

    @Field("fecha")
    private LocalDateTime fecha;

    @Field("origen")
    private String origen;

    @Field("estado")
    private EstadoHechoEnum estado = EstadoHechoEnum.ACTIVO;

    @Field("pdis")
    private List<PdiMongo> pdiIds = new ArrayList<>();

    @Field("solicitudes")
    private List<SolicitudMongo> solicitudes = new ArrayList<>();
    public HechoMongo() {}

    public HechoMongo(
            String id_,
            String nombreColeccion2,
            String titulo2,
            List<String> etiquetas2,
            CategoriaHechoEnum categoria2,
            String ubicacion2,
            LocalDateTime fecha2,
            String origen2) {
        this.id = id_;
        this.nombreColeccion = nombreColeccion2;
        this.titulo = titulo2;
        this.etiquetas = etiquetas2;
        this.categoria = categoria2;
        this.ubicacion = ubicacion2;
        this.fecha = fecha2;
        this.origen = origen2;
    }

    public HechoMongo(
            String id_,
            String nombreColeccion2,
            String titulo2,
            List<String> etiquetas2,
            CategoriaHechoEnum categoria2,
            String ubicacion2,
            LocalDateTime fecha2,
            String origen2,
            EstadoHechoEnum estado_2) {
        this(id_, nombreColeccion2, titulo2, etiquetas2, categoria2, ubicacion2, fecha2, origen2);
        this.estado = estado_2;
    }

    /** Asegura valor por defecto si se guarda sin estado */
    public void ensureEstadoDefault() {
        if (this.estado == null) {
            this.estado = EstadoHechoEnum.ACTIVO;
        }
    }
}
