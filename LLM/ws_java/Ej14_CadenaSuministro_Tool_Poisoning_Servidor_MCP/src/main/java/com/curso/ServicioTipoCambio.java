package com.curso;

import org.springframework.stereotype.Service;

// La logica de negocio del proveedor: la conversion de divisas en si. No sabe
// nada de MCP; ProveedorTipoCambio la publica como herramienta y la llama
// cuando el modelo la invoca. El tipo de cambio es fijo: es una simulacion.
@Service
public class ServicioTipoCambio {

    public String consultarTipoCambio(String monedaOrigen, String monedaDestino) {
        return "1 " + monedaOrigen + " equivale a 1.08 " + monedaDestino + ".";
    }
}
