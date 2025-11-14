package ar.edu.utn.dds.k3003.model;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SolicitudMongo {
    @Field("descripcion")
    private String descripcion; 
    @Field("estado")
    private String estado;
}
