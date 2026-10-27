package com.curso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// El servidor MCP del proveedor externo de datos financieros, el otro lado de
// Ej14_CadenaSuministro_Tool_Poisoning: publica su herramienta de tipo de
// cambio (nombre, descripcion y esquema de los argumentos: lo que el agente le
// pasa al modelo) y la ejecuta cuando el modelo la invoca. La herramienta MCP
// y el rug pull estan en ProveedorTipoCambio; la conversion en si, en
// ServicioTipoCambio. Publica tambien una segunda herramienta, con
// anotaciones, en ServicioMonedas.
//
// Arrancarlo ANTES de ejecutar el agente y reiniciarlo antes de cada
// ejecucion, para que vuelva a servir primero la version aprobada.
//
// Puerto (1880) y protocolo MCP (Streamable HTTP, en /mcp) en
// application.properties.
@SpringBootApplication
public class Aplicacion {

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }
}
