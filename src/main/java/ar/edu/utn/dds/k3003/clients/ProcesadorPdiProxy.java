package ar.edu.utn.dds.k3003.clients;

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

    // Retry 1 vez en 5xx o IOException con backoff
    Interceptor retryInterceptor = chain -> {
      var req = chain.request();
      int attempts = 0, max = 2;
      long backoff = 250;
      while (true) {
        attempts++;
        try {
          var resp = chain.proceed(req);
          if (resp.code() >= 500 && attempts < max) {
            if (resp.body() != null) resp.close();
            Thread.sleep(backoff);
            backoff *= 2;
            continue;
          }
          return resp;
        } catch (Exception ex) {
          if (attempts < max) {
              try {
                  Thread.sleep(backoff);
              } catch (InterruptedException e) {
                  throw new RuntimeException(e);
              }
              backoff *= 2;
            continue;
          }
            try {
                throw ex;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
  public PdIDTO procesar(PdIDTO pdi) throws NoSuchElementException {
    Objects.requireNonNull(pdi, "PdIDTO requerido");
    if (pdi.hechoId() == null || pdi.hechoId().isBlank())
      throw new IllegalArgumentException("hechoId requerido en PdIDTO");

    try {
      log.info("Fuentes → ProcesadorPdI request JSON: {}", mapper.writeValueAsString(pdi));

      Response<PdIDTO> resp = service.procesar(pdi).execute();

      log.info("ProcesadorPdI status={} message={} headers={}",
              resp.code(), resp.message(), resp.headers());

      if (resp.isSuccessful()) {
        PdIDTO body = resp.body();
        log.info("ProcesadorPdI → Fuentes {} {} body: {}",
                resp.code(), resp.message(), mapper.writeValueAsString(body));
        return body;
      }

      String errorBody = safeReadBody(resp.errorBody());
      log.warn("ProcesadorPdI respondió error {} {}. Body: {}", resp.code(), resp.message(), errorBody);

      switch (resp.code()) {
        case 404 -> throw new NoSuchElementException("PdI no procesable o recurso no encontrado");
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
    throw new UnsupportedOperationException("Unimplemented method 'buscarPorHecho'");
  }

  @Override
  public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
    // No se usa en Fuentes
  }
}
