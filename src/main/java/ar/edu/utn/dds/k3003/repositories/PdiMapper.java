package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.Pdi;

public class PdiMapper {
    public PdIDTO map(Pdi pdi){
        if(pdi.equals(null)){
            return null;
        } // AHORA VA
        PdIDTO retorno = new PdIDTO(pdi.getId(),pdi.getHecho(),pdi.getDescripcion(), pdi.getLugar(),pdi.getMomento(),pdi.getContenido(),pdi.getEtiquetas()) ;
        return retorno;
    }
    public Pdi map(PdIDTO pdiDTO){
        if(pdiDTO.equals(null)){
            return null;
        } // AHORA VA
        Pdi pdiMappeado = new Pdi(pdiDTO.id(),pdiDTO.hechoId(),pdiDTO.lugar(),pdiDTO.descripcion(),pdiDTO.contenido(),pdiDTO.etiquetas(),pdiDTO.momento());
        return pdiMappeado;
    }
}
