package com.curso;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.tool.annotation.Tool;

// Dato interno de la empresa. Consultarlo marca la sesion, para que
// HerramientaEmail pueda saber que el agente ya ha tocado una fuente interna.
public class HerramientaPreciosCompra {

    private final Map<String, String> preciosCompra = new LinkedHashMap<>();
    private final SesionAgente sesion;

    public HerramientaPreciosCompra(SesionAgente sesion) {
        this.sesion = sesion;
        preciosCompra.put("proveedor_A", "12.50 EUR/unidad");
        preciosCompra.put("proveedor_B", "9.80 EUR/unidad");
        preciosCompra.put("proveedor_C", "15.20 EUR/unidad");
    }

    @Tool(name = "consultar_precios_compra",
            description = "Consulta los precios de compra negociados con cada proveedor. Dato interno de la empresa.")
    public String consultarPreciosCompra() {
        sesion.marcarDatoInternoConsultado();
        return preciosCompra.entrySet().stream()
                .map(proveedor -> proveedor.getKey() + ": " + proveedor.getValue())
                .collect(Collectors.joining("\n"));
    }
}
