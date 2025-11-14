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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        rt.getMessageConverters()
                .removeIf(c -> c.getClass().getName().contains("MappingJackson2XmlHttpMessageConverter"));
        return rt;
    }


    
    /** Devuelve una Solicitud si existe para ese hecho; vacío si no. */
    @SuppressWarnings("deprecation")
    public Optional<SolicitudDTO> findByHecho(String hechoId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/solicitudes")
                .queryParam("hecho", hechoId)
                .toUriString();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, "application/json"); // pedir JSON primero
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                log.warn("[Solicitudes] Respuesta no exitosa para hecho {}: status={}", hechoId, resp.getStatusCodeValue());
                return Optional.empty();
            }

            @SuppressWarnings("null")
            String body = resp.getBody().trim();

            // --- JSON clásico ---
            if (body.startsWith("{")) {
                var dto = mapper.readValue(body, SolicitudDTO.class);
                return Optional.ofNullable(dto);
            }
            if (body.startsWith("[")) {
                List<SolicitudDTO> list = mapper.readValue(body, new TypeReference<List<SolicitudDTO>>() {});
                return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
            }

            // --- XML (fallback) ---
            if (body.startsWith("<")) {
                // Lista vacía: <List/>  -> no hay solicitud
                if (body.contains("<List/>")) {
                    return Optional.empty();
                }

                // Si hay al menos un <item>, consideramos que existe solicitud
                if (body.contains("<item>")) {
                    // extracción simple por regex (defensiva y suficiente para decidir)
                    String hechoIdXml = extractTag(body, "hecho_id");
                    String descripcionXml = extractTag(body, "descripcion");
                    String estadoXml = extractTag(body, "estado");
                    if (hechoIdXml == null || hechoIdXml.isBlank()) {
                        hechoIdXml = hechoId; // fallback
                    }
                    return Optional.of(new SolicitudDTO(descripcionXml, hechoIdXml,estadoXml));
                }

                // Cualquier otro XML lo tomamos como "no hay"
                log.warn("[Solicitudes] XML inesperado para hecho {}: {}", hechoId, body);
                return Optional.empty();
            }

            // Formato desconocido -> no bloquear
            log.warn("[Solicitudes] Formato desconocido para hecho {}: {}", hechoId, body);
            return Optional.empty();

        } catch (Exception e) {
            log.error("[Solicitudes] Error consultando hecho {}: {}", hechoId, e.toString());
            return Optional.empty();
        }
    }
    //ESTO PUEDE NO FUNCIONAR
    private List<String> extractItems(String xml) {
        Pattern p = Pattern.compile("<item>(.*?)</item>", Pattern.DOTALL);
        Matcher m = p.matcher(xml);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group(1));
        }
        return list;
    }

    //ESTO PUEDE NO FUNCIONAR 2
    public List<SolicitudDTO> findAllByHecho(String hechoId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/solicitudes")
                .queryParam("hecho", hechoId)
                .toUriString();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, "application/json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return List.of();
            }

            @SuppressWarnings("null")
            String body = resp.getBody().trim();
            if (body.startsWith("{")) {
                SolicitudDTO dto = mapper.readValue(body, SolicitudDTO.class);
                return List.of(dto);
            }
            if (body.startsWith("[")) {
                return mapper.readValue(body, new TypeReference<List<SolicitudDTO>>() {});
            }
            if (body.startsWith("<")) {
                if (body.contains("<List/>") || body.contains("<List></List>")) {
                    return List.of();
                }
                if (body.contains("<item>")) {
                    List<String> items = extractItems(body); // cada <item>...</item>

                    List<SolicitudDTO> result = new ArrayList<>();

                    for (String xml : items) {
                        String descripcion = extractTag(xml, "descripcion");
                        String hechoXml = extractTag(xml, "hecho_id");
                        String estadoXml = extractTag(xml, "estado");

                        if (hechoXml == null || hechoXml.isBlank()) {
                            hechoXml = hechoId;
                        }

                        result.add(new SolicitudDTO(
                                descripcion,
                                hechoXml,
                                estadoXml
                        ));
                    }

                    return result;
                }

                return List.of();
            }

            return List.of();

        } catch (Exception e) {
            log.error("[Solicitudes] Error consultando hecho {}: {}", hechoId, e.toString());
            return List.of();
        }
    }


    // Helper muy simple para extraer <tag>valor</tag> del primer item
    private static String extractTag(String xml, String tag) {
        int i = xml.indexOf("<" + tag + ">");
        if (i < 0) return null;
        int j = xml.indexOf("</" + tag + ">", i);
        if (j < 0) return null;
        int start = i + tag.length() + 2;
        return xml.substring(start, j).trim();
    }

}
