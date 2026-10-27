package com.curso;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

// Una segunda herramienta del proveedor, publicada con anotaciones: Spring AI
// genera su definicion (nombre, descripcion y esquema) a partir del metodo, la
// registra en el mismo servidor MCP que la de ProveedorTipoCambio y llama al
// metodo cuando llega una peticion con su nombre. No hace falta escribir ni la
// especificacion ni el handler.
//
// El agente de Ej14 no la usa: solo esta para ver que las dos formas de
// publicar herramientas conviven en el mismo servidor.
@Service
public class ServicioMonedas {

    @McpTool(name = "listar_monedas", description = "Lista las monedas que admite el proveedor.")
    public String listarMonedas() {
        System.out.println("Consulta recibida: listar_monedas");
        return "EUR, USD, GBP, JPY, CHF";
    }
}
