package com.curso;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// El lado de la aplicacion que habla con el proveedor externo (ver
// ServidorProveedor): descarga su manifiesto y convierte la herramienta que
// describe en una herramienta de Spring AI. La descripcion y el esquema que ve
// el modelo son, tal cual, los que mande el proveedor: la aplicacion no los
// revisa.
public class ClienteProveedor {

    private static final String URL_PROVEEDOR = "http://localhost:1881";

    // Lo que publica el proveedor sobre su herramienta.
    public record Manifiesto(String nombre, String descripcion, JsonNode esquema) {
    }

    // Los argumentos con los que el modelo invoca la herramienta, con los
    // nombres del esquema del proveedor.
    public record PeticionTipoCambio(
            @JsonProperty("moneda_origen") String monedaOrigen,
            @JsonProperty("moneda_destino") String monedaDestino) {
    }

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Manifiesto descargarManifiesto() {
        String json = get("/manifiesto");
        try {
            return mapper.readValue(json, Manifiesto.class);
        } catch (IOException e) {
            throw new IllegalStateException("Manifiesto ilegible: " + json, e);
        }
    }

    // La herramienta de Spring AI: nombre, descripcion y esquema del
    // manifiesto; al invocarla, hace la consulta HTTP al proveedor.
    public ToolCallback crearHerramienta(Manifiesto manifiesto) {
        return FunctionToolCallback
                .builder(manifiesto.nombre(), (PeticionTipoCambio peticion) -> get("/tipo_cambio"
                        + "?origen=" + URLEncoder.encode(peticion.monedaOrigen(), StandardCharsets.UTF_8)
                        + "&destino=" + URLEncoder.encode(peticion.monedaDestino(), StandardCharsets.UTF_8)))
                .description(manifiesto.descripcion())
                .inputSchema(manifiesto.esquema().toString())
                .inputType(PeticionTipoCambio.class)
                .build();
    }

    private String get(String ruta) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(URL_PROVEEDOR + ruta)).GET().build();
            return http.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("No se pudo contactar con el proveedor en " + URL_PROVEEDOR
                    + ": arranca antes ServidorProveedor", e);
        }
    }
}
