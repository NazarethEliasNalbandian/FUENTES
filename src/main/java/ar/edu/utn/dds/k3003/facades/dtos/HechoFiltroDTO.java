package ar.edu.utn.dds.k3003.facades.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HechoFiltroDTO {
    private String titulo;
    private String categoria;
    private String ubicacion;
    private String etiquetas;
    private String origen;
    private String estado; //ELIMINABLE EN LA VERSION DEFAULT
    private String pdi_nombre;
    private String pdi_etiquetas;
    private String pdi_contenido;
    private String pdi_descripcion;
    private String pdi_lugar;
    public HechoFiltroDTO(String titulo2,String categoria_2,String ubicacion_2,String etiquetas_2,String origen_2,String estado_2, String pdi_nombre_2, String pdi_descripcion_2, String pdi_etiquetas_2,String pdi_lugar_2,String pdi_contenido2){
        titulo = titulo2;
        categoria = categoria_2;
        ubicacion = ubicacion_2;
        etiquetas = etiquetas_2;
        origen = origen_2;
        estado = estado_2;
        pdi_descripcion = pdi_descripcion_2;
        pdi_lugar = pdi_lugar_2;
        pdi_etiquetas = pdi_etiquetas_2;
        pdi_nombre = pdi_nombre_2;
    };
}
