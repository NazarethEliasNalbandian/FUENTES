package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProcesadorPdiProxy implements FachadaProcesadorPdI {

  private final String endpoint;
  private final ProcesadorPdIRetrofit service;
  private final ObjectMapper mapper;

  public ProcesadorPdiProxy(ObjectMapper objectMapper) {
    this(
            objectMapper,
            System.getenv().getOrDefault("URL_PROCESADOR", "http://localhost:8080/")
    );
  }

  public ProcesadorPdiProxy(
          ObjectMapper objectMapper,
          @Value("${URL_PROCESADOR:http://localhost:8080/}") String endpointEnv
  ) {
    // ⚠️ usar SIEMPRE el mapper inyectado (ya configurado con SNAKE_CASE y JavaTimeModule)
    this.mapper = objectMapper;

    this.endpoint = ensureEndsWithSlash(endpointEnv);
    log.info("[ProcesadorPdI] Base URL: {}", this.endpoint);

    // Cliente HTTP con timeouts + logging
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(msg -> log.info("[ProcesadorPdI] {}", msg));
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient ok =
            new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

    Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl(this.endpoint)
                    .addConverterFactory(JacksonConverterFactory.create(this.mapper))
                    .client(ok)
                    .build();

    this.service = retrofit.create(ProcesadorPdIRetrofit.class);
  }

  private static String ensureEndsWithSlash(String base) {
    if (base == null || base.isBlank()) return "http://localhost:8080/";
    return base.endsWith("/") ? base : base + "/";
  }

  @Override
  public PdIDTO procesar(PdIDTO pdi) throws NoSuchElementException {
    Objects.requireNonNull(pdi, "PdIDTO requerido");
    if (pdi.hechoId() == null || pdi.hechoId().isBlank())
      throw new IllegalArgumentException("hechoId requerido en PdIDTO");

    try {
      // Log de lo que enviamos (en snake_case por configuración global del mapper)
      log.info("Fuentes → ProcesadorPdI request JSON: {}", mapper.writeValueAsString(pdi));

      Response<PdIDTO> resp = service.procesar(pdi).execute();

      if (resp.isSuccessful()) {
        PdIDTO body = resp.body();
        log.info("ProcesadorPdI → Fuentes {} {} body: {}",
                resp.code(), resp.message(),
                mapper.writeValueAsString(body));
        return body;
      }

      String errorBody = (resp.errorBody() != null) ? resp.errorBody().string() : "";
      log.warn("ProcesadorPdI respondió error {} {}. Body: {}", resp.code(), resp.message(), errorBody);

      // Traducción de códigos a excepciones del dominio
      switch (resp.code()) {
        case 404 -> throw new NoSuchElementException("PdI no procesable o recurso no encontrado");
        case 422 -> throw new IllegalStateException("ProcesadorPdI rechazó la PdI (unprocessable)");
        default -> throw new RuntimeException("Error " + resp.code() + " al llamar ProcesadorPdI: " + errorBody);
      }

    } catch (NoSuchElementException | IllegalStateException e) {
      throw e; // ya mapeadas
    } catch (Exception e) {
      log.error("Fallo al conectar/llamar a ProcesadorPdI", e);
      throw new RuntimeException("Error conectándose con el componente ProcesadorPdI", e);
    }
  }

  // Métodos no implementados del contrato (si aún no se usan)
  @Override
  public PdIDTO buscarPdIPorId(String pdiId) throws NoSuchElementException {
    throw new UnsupportedOperationException("Unimplemented method 'buscarPdIPorId'");
  }

  @Override
  public List<PdIDTO> buscarPorHecho(String hechoId) throws NoSuchElementException {
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorHecho'");
  }

  @Override
  public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
    // No se usa en Fuentes
  }
}
