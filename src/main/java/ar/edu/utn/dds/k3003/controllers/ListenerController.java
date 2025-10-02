package ar.edu.utn.dds.k3003.controllers;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suscriptor")
public class ListenerController {

    private final RabbitListenerEndpointRegistry registry;

    public ListenerController(RabbitListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/suscribirse")
    public ResponseEntity<String> start() {
        var container = registry.getListenerContainer("suscripcion");
        if (container != null && !container.isRunning()) {
            container.start();
            return ResponseEntity.ok("Suscripcion agregada");
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/desuscribirse")
    public ResponseEntity<String> stop() {
        var container = registry.getListenerContainer("suscripcion");
        if (container != null && container.isRunning()) {
            container.stop();
            return ResponseEntity.ok("Suscripcion cancelada");
        }
        return ResponseEntity.badRequest().build();
    }
}
