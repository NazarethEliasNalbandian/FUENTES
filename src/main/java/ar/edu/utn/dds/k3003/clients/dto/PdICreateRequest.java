package ar.edu.utn.dds.k3003.clients.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL) // evita incluir nulls
public record PdICreateRequest(
        String hechoId,
        String descripcion,
        String lugar,
        LocalDateTime momento,
        String contenido
) { }
