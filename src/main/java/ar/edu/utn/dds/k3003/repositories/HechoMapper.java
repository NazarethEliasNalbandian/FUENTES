package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;

public class HechoMapper {
    public HechoDTO map(Hecho hecho){
        if (hecho == null) return null;
        return new HechoDTO(
                hecho.getId(),
                hecho.getNombreColeccion(),
                hecho.getTitulo(),
                hecho.getEtiquetas(),
                hecho.getCategoria(),
                hecho.getUbicacion(),
                hecho.getFecha(),
                hecho.getOrigen()
        );
    }

    public Hecho map(HechoDTO dto){
        if (dto == null) return null;
        return new Hecho(
                dto.id(),
                dto.nombreColeccion(),
                dto.titulo(),
                dto.etiquetas(),
                dto.categoria(),
                dto.ubicacion(),
                dto.fecha(),
                dto.origen()
        );
    }
}
