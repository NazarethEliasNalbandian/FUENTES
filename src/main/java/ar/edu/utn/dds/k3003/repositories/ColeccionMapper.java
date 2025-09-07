package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.model.Coleccion;

public class ColeccionMapper {
    public ColeccionDTO map(Coleccion coleccion){
        if (coleccion == null) return null;
        return new ColeccionDTO(coleccion.getNombre(), coleccion.getDescripcion());
    }

    public Coleccion map(ColeccionDTO dto){
        if (dto == null) return null;
        return new Coleccion(dto.nombre(), dto.descripcion());
    }
}
