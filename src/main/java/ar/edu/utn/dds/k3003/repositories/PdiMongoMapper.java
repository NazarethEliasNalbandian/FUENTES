package ar.edu.utn.dds.k3003.repositories;

import org.springframework.stereotype.Component;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.PdiMongo;

@Component
public class PdiMongoMapper {

    public PdIDTO mapDTO(PdiMongo pdi) {
        if (pdi == null) {
            return null;
        }
        return new PdIDTO(
                pdi.getId().toString(),
                pdi.getHechoId(),
                pdi.getDescripcion(),
                pdi.getLugar(),
                pdi.getMomento(),
                pdi.getContenido(),
                pdi.getImageurl(),
                pdi.getAutoTags()  // ← corregido: convención camelCase    // ← coincide con el DTO "etiquetas"
        );
    }

    public PdiMongo mapDTO(PdIDTO dto) {
        if (dto == null) {
            return null;
        }
        return new PdiMongo(
                dto.id(),
                dto.hechoId(),
                dto.descripcion(),
                dto.lugar(),
                dto.momento(),
                dto.contenido(),
                dto.imageUrl(),
                dto.etiquetas()
        );
    }
}

