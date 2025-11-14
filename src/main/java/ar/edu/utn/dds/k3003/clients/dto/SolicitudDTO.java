package ar.edu.utn.dds.k3003.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudDTO(
        String descripcion,
        @JsonProperty("hecho_id") String hechoId,
                String estado

) {}
