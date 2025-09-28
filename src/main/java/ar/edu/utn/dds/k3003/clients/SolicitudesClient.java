package ar.edu.utn.dds.k3003.clients;

// src/main/java/ar/edu/utn/dds/k3003/clients/SolicitudesClient.java

import ar.edu.utn.dds.k3003.clients.dto.SolicitudDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SolicitudesClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public SolicitudesClient(
            ObjectMapper mapper,
            @Value("${URL_SOLICITUDES}") String baseUrl
    ) {
        this.mapper = mapper;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.restTemplate = mkTemplate();
        log.info("[Solicitudes] Base URL: {}", this.baseUrl);
    }

    private RestTemplate mkTemplate() {
        var rt = new RestTemplate();
        // opcional: timeouts (Spring Boot 3 con HttpComponentsClientHttpRequestFactory si querés afinar más)
        return rt;
    }

    /** Devuelve una Solicitud si existe para ese hecho; vacío si no. */
    public Optional<SolicitudDTO> findByHecho(String hechoId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/solicitudes")
                .queryParam("hecho", hechoId)
                .toUriString();

        try {
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                String body = resp.getBody().trim();

                // Caso arreglo vacío: []
                if (body.equals("[]")) return Optional.empty();

                // ¿Es objeto?
                if (body.startsWith("{")) {
                    var dto = mapper.readValue(body, SolicitudDTO.class);
                    return Optional.ofNullable(dto);
                }

                // ¿Es arreglo con 1..n?
                if (body.startsWith("[")) {
                    List<SolicitudDTO> list = mapper.readValue(body, new TypeReference<List<SolicitudDTO>>() {});
                    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
                }
            }

            // Cualquier otra cosa, lo consideramos como "no hay solicitud"
            log.warn("[Solicitudes] Respuesta inesperada para hecho {}: status={} body={}",
                    hechoId, resp.getStatusCodeValue(), resp.getBody());
            return Optional.empty();
        } catch (Exception e) {
            log.error("[Solicitudes] Error consultando hecho {}: {}", hechoId, e.toString());
            // Ante error de red/timeout, preferimos NO bloquear: asumimos que no hay solicitud
            return Optional.empty();
        }
    }
}
