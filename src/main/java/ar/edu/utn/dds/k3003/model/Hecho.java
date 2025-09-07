package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.List;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hecho")
public class Hecho {
    @Id
    private String id;

    @Column(nullable = false)
    private String nombreColeccion;

    @Column(nullable = false)
    private String titulo;

    // Relación de 1 Hecho → muchas etiquetas (strings)
    @ElementCollection
    @CollectionTable(
        name = "hecho_etiquetas",
        joinColumns = @JoinColumn(name = "hechoid")
    )
    @Column(name = "etiqueta")
    private List<String> etiquetas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaHechoEnum categoria;

    @Column
    private String ubicacion;

    @Column
    private LocalDateTime fecha;

    @Column
    private String origen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHechoEnum estado = EstadoHechoEnum.ACTIVO;

    public Hecho() { }

    public Hecho(String id_, String nombreColeccion2, String titulo2, List<String> etiquetas2,
            CategoriaHechoEnum categoria2, String ubicacion2, LocalDateTime fecha2, String origen2) {
        id= id_;
        nombreColeccion = nombreColeccion2;
        titulo = titulo2;
        etiquetas = etiquetas2;
        categoria = categoria2;
        ubicacion= ubicacion2;
        fecha = fecha2;
        origen = origen2;
    }

    @PrePersist
    public void ensureEstadoDefault() {
        if (estado == null) estado = EstadoHechoEnum.ACTIVO;
    }
    public String getId(){
        return this.id;
    }
    public void setId(String id_nuevo){
        this.id = id_nuevo;
    }
    public String getTitulo(){
        return this.titulo;
    }
    public void setTitulo(String titulo_nuevo){
        this.titulo = titulo_nuevo;
    }
    public String getNombreColeccion(){
        return this.nombreColeccion;
    }
    public void setNombreColeccion(String nombre_coleccion){
        this.nombreColeccion = nombre_coleccion;
    }
    public List<String> getEtiquetas(){
        return this.etiquetas;
    }
    public void setEtiquetas(List<String> etiquetas_nuevas){
        this.etiquetas = etiquetas_nuevas;
    }
    public CategoriaHechoEnum getCategoria(){
        return categoria;
    }
    public void setCategoria(CategoriaHechoEnum categoria_nueva){
        this.categoria = categoria_nueva;
    }
    public String getUbicacion(){
        return this.ubicacion;
    }
    public void setUbicacion(String ubicacion_nueva){
        this.ubicacion = ubicacion_nueva;
    }
    public String getOrigen(){
        return this.origen;
    }
    public void setOrigen(String origen_nuevo){
        this.origen = origen_nuevo;
    }
    public LocalDateTime getFecha(){
        return this.fecha;
    }
    public void setFecha(LocalDateTime fecha_nueva){
        this.fecha = fecha_nueva;
    }
}
