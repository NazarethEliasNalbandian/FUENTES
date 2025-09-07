package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import org.springframework.stereotype.Component;

@Component
public class HechoMapper {
    public HechoDTO map(Hecho hecho){
        if(hecho ==null){
            return null;
        }
        HechoDTO retorno = new HechoDTO(hecho.getId(),hecho.getNombreColeccion(),hecho.getTitulo(),hecho.getEtiquetas(),hecho.getCategoria(),hecho.getUbicacion(),hecho.getFecha(),hecho.getOrigen());
        return retorno;
    }
    public Hecho map(HechoDTO hechoDTO){
        if(hechoDTO == null){
            return null;
        }
        Hecho hechoMappeado = new Hecho(hechoDTO.id(),hechoDTO.nombreColeccion(),hechoDTO.titulo(),hechoDTO.etiquetas(),hechoDTO.categoria(),hechoDTO.ubicacion(),hechoDTO.fecha(),hechoDTO.origen());
        return hechoMappeado;
    }
}

