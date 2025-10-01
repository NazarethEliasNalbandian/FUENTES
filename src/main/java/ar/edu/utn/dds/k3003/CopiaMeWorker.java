package ar.edu.utn.dds.k3003;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import ar.edu.utn.dds.k3003.model.Hecho;

@Component
public class CopiaMeWorker {
    private Fachada fachada;
    private final ObjectMapper objectMapper;
    public CopiaMeWorker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public void setFachada(Fachada nueva_fachada){
        this.fachada = nueva_fachada;
    }
    @RabbitListener(queues = "${queue.name}")
    public void handleMessage(String message) {
        System.out.println("Se recibió el siguiente payload:");
        System.out.println(message);
        try {
            Hecho hecho = objectMapper.readValue(message, Hecho.class);
            HechoDTO hechoDTO = new HechoDTO(
                    hecho.getId(),
                    hecho.getNombreColeccion(),
                    hecho.getTitulo(),
                    hecho.getEtiquetas(),
                    hecho.getCategoria(),
                    hecho.getUbicacion(),
                    hecho.getFecha(),
                    hecho.getOrigen(),
                    hecho.getEstado()
            );
            fachada.agregar(hechoDTO);
            System.out.println("Hecho agregado correctamente");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
