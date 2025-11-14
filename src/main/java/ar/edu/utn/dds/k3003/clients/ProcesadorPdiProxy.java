package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dto.PdICreateRequest;
import ar.edu.utn.dds.k3003.clients.dto.ProcesamientoResponseDTO;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
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
    this.mapper = objectMapper;
    this.endpoint = ensureEndsWithSlash(endpointEnv);
    log.info("[ProcesadorPdI] Base URL: {}", this.endpoint);

    HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor(msg -> log.info("[ProcesadorPdI] {}", msg))
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    // Correlation ID en cada request
    Interceptor requestIdInterceptor = chain -> {
      Request reqWithId = chain.request().newBuilder()
              .header("X-Request-Id", UUID.randomUUID().toString())
              .build();
      return chain.proceed(reqWithId);
    };

    // Retry simple: 1 reintento en 5xx/IOException con backoff exponencial
    Interceptor retryInterceptor = chain -> {
      Request req = chain.request();
      int attempts = 0;
      int max = 2;           // 1 intento + 1 reintento
      long backoff = 250L;   // ms

      while (true) {
        attempts++;
        okhttp3.Response resp = null;
        try {
          resp = chain.proceed(req);

          if (resp.code() >= 500 && attempts < max) {
            // liberar recursos antes de reintentar
            resp.close();
            try {
              Thread.sleep(backoff);
            } catch (InterruptedException ie) {
              Thread.currentThread().interrupt();
              throw new IOException("Interrumpido durante el backoff", ie);
            }
            backoff *= 2;
            continue;
          }
          // devolver la respuesta (NO cerrar: la consumirá Retrofit)
          return resp;

        } catch (IOException ioe) {
          if (resp != null) resp.close();
          if (attempts < max) {
            try {
              Thread.sleep(backoff);
            } catch (InterruptedException ie) {
              Thread.currentThread().interrupt();
              throw new IOException("Interrumpido durante el backoff", ie);
            }
            backoff *= 2;
            continue;
          }
          throw ioe;
        }
      }
    };

    OkHttpClient ok = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(requestIdInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(logging)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
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
  public ProcesamientoResponseDTO procesar(PdIDTO pdi) {
    Objects.requireNonNull(pdi, "PdIDTO requerido");
    if (pdi.hechoId() == null || pdi.hechoId().isBlank())
      throw new IllegalArgumentException("hechoId requerido en PdIDTO");

    try {
      // Body “slim” sin nulls/strings vacíos
      PdICreateRequest req = toCreateRequest(pdi);

      log.info("Fuentes → ProcesadorPdI request JSON: {}", mapper.writeValueAsString(req));

      // Asegurá la firma del Retrofit:
      // Call<ProcesamientoResponseDTO> procesar(@Body PdICreateRequest req);
      Response<ProcesamientoResponseDTO> resp = service.procesar(req).execute();

      log.info("ProcesadorPdI status={} message={} headers={}",
              resp.code(), resp.message(), resp.headers());

      if (resp.isSuccessful()) {
        ProcesamientoResponseDTO body = resp.body();
        if (body == null) {
          throw new IllegalStateException("ProcesadorPdI devolvió cuerpo nulo");
        }
        log.info("ProcesadorPdI → Fuentes {} {} body: {}",
                resp.code(), resp.message(), mapper.writeValueAsString(body));
        return body; // procesada true/false
      }

      // No-2xx
      String errorBody = safeReadBody(resp.errorBody());
      log.warn("ProcesadorPdI respondió error {} {}. Body: {}",
              resp.code(), resp.message(), errorBody);

      switch (resp.code()) {
        case 400 -> throw new IllegalStateException("Hecho inexistente o request inválido hacia ProcesadorPdI");
        case 404 -> throw new NoSuchElementException("Recurso de ProcesadorPdI no encontrado");
        case 422 -> throw new IllegalStateException("ProcesadorPdI rechazó la PdI (unprocessable)");
        default -> throw new RuntimeException("Error " + resp.code() + " al llamar ProcesadorPdI: " + errorBody);
      }

    } catch (NoSuchElementException | IllegalStateException e) {
      throw e;
    } catch (Exception e) {
      log.error("Fallo al conectar/llamar a ProcesadorPdI", e);
      throw new RuntimeException("Error conectándose con el componente ProcesadorPdI", e);
    }
  }

  /** Helpers **/

  private static String blankToNull(String s) {
    return (s == null || s.isBlank()) ? null : s.trim();
  }

  private PdICreateRequest toCreateRequest(PdIDTO p) {
    return new PdICreateRequest(
            p.hechoId(),
            emptyToNull(p.descripcion()),
            emptyToNull(p.lugar()),
            iso(p.momento()),
            emptyToNull(p.contenido()),
            emptyToNull(p.imageUrl())   // <<< clave
    );
  }

  private static String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }

  private static String iso(java.time.LocalDateTime dt) {
    return dt == null ? null : dt.toString(); // o DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dt)
  }


  private static String safeReadBody(ResponseBody body) {
    if (body == null) return "";
    try {
      String s = body.string();
      body.close();
      return s;
    } catch (Exception e) {
      return "<unreadable>";
    }
  }

  // --- Métodos del contrato aún no usados ---
  @Override
  public PdIDTO buscarPdIPorId(String pdiId) throws NoSuchElementException {
    throw new UnsupportedOperationException("Unimplemented method 'buscarPdIPorId'");
  }

  @Override
  public List<PdIDTO> buscarPorHecho(String hechoId) throws NoSuchElementException {
    if (hechoId.isBlank())
      throw new IllegalArgumentException("hechoId requerido");
    try {
      // Body “slim” sin nulls/strings vacíos
      // Asegurá la firma del Retrofit:
      // Call<ProcesamientoResponseDTO> procesar(@Body PdICreateRequest req);
      Response<List<PdIDTO>> resp = service.buscarPorHecho(hechoId).execute();

      if (resp.isSuccessful()) {
        List<PdIDTO> body = resp.body();
        if (body == null) {
          throw new IllegalStateException("ProcesadorPdI devolvió cuerpo nulo");
        }
        return body; // procesada true/false
      }

      // No-2xx
      String errorBody = safeReadBody(resp.errorBody());
      log.warn("ProcesadorPdI respondió error {} {}. Body: {}",
              resp.code(), resp.message(), errorBody);

      switch (resp.code()) {
        case 400 -> throw new IllegalStateException("Hecho inexistente o request inválido hacia ProcesadorPdI");
        case 404 -> throw new NoSuchElementException("Recurso de ProcesadorPdI no encontrado");
        case 422 -> throw new IllegalStateException("ProcesadorPdI rechazó la peticion (unprocessable)");
        default -> throw new RuntimeException("Error " + resp.code() + " al llamar ProcesadorPdI: " + errorBody);
      }

    } catch (NoSuchElementException | IllegalStateException e) {
      throw e;
    } catch (Exception e) {
      log.error("Fallo al conectar/llamar a ProcesadorPdI", e);
      throw new RuntimeException("Error conectándose con el componente ProcesadorPdI", e);
    }
  }

  @Override
  public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
    // No se usa en Fuentes
  }
}
