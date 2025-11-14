package ar.edu.utn.dds.k3003.repositories;

import org.springframework.stereotype.Component;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.model.HechoMongo;

@Component
public class HechoMongoMapper{

  public HechoDTO mapDTO(HechoMongo hecho){
        if(hecho ==null){
            return null;
        }
        HechoDTO retorno = new HechoDTO(hecho.getId(),hecho.getNombreColeccion(),hecho.getTitulo(),hecho.getEtiquetas(),hecho.getCategoria(),hecho.getUbicacion(),hecho.getFecha(),hecho.getOrigen(),hecho.getEstado());
        return retorno;
    }
    public HechoMongo mapDTO(HechoDTO hechoDTO){
        if(hechoDTO == null){
            return null;
        }
        HechoMongo hechoMappeado = new HechoMongo(hechoDTO.id(),hechoDTO.nombreColeccion(),hechoDTO.titulo(),hechoDTO.etiquetas(),hechoDTO.categoria(),hechoDTO.ubicacion(),hechoDTO.fecha(),hechoDTO.origen());
        return hechoMappeado;
    }
}