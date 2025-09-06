package ar.edu.utn.dds.k3003.configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.utn.dds.k3003.clients.ProcesadorPdiProxy;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;

@Configuration 
public class AppConfig { 
    @Bean 
    public FachadaProcesadorPdI fachadaprocesadorPdI(ObjectMapper objectMapper) 
    { return new ProcesadorPdiProxy(objectMapper);} 
}