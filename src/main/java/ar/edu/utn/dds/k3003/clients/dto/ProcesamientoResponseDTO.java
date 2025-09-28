package ar.edu.utn.dds.k3003.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProcesamientoResponseDTO(String pdiId, @JsonProperty("estado") String estado,  @JsonProperty("auto_tags") java.util.List<String> etiquetas) {}

