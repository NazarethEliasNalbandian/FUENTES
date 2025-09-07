package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   // <- NECESARIO para JPA
@Entity
@Table(name = "hecho")
public class Hecho {

    @Id
    private String id;

    @Column(nullable = false)
    private String nombreColeccion;

    @Column(nullable = false)
    private String titulo;

    // Relación de 1 Hecho → muchas etiquetas
    @ElementCollection
    @CollectionTable(
            name = "hecho_etiquetas",
            joinColumns = @JoinColumn(name = "hecho_id")
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

    public Hecho(String id_, String nombreColeccion2, String titulo2, List<String> etiquetas2,
                 CategoriaHechoEnum categoria2, String ubicacion2, LocalDateTime fecha2, String origen2) {
        this.id = id_;
        this.nombreColeccion = nombreColeccion2;
        this.titulo = titulo2;
        this.etiquetas = etiquetas2;
        this.categoria = categoria2;
        this.ubicacion = ubicacion2;
        this.fecha = fecha2;
        this.origen = origen2;
    }
}
