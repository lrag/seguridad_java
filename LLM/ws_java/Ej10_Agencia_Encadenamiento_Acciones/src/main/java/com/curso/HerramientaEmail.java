package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// Operacion habitual de cualquier asistente. Sola no es peligrosa; encadenada
// despues de HerramientaPreciosCompra es un canal para sacar el dato interno
// fuera de la empresa.
public class HerramientaEmail {

    private static final String DOMINIO_EMPRESA = "@empresa.com";

    private final SesionAgente sesion;

    public HerramientaEmail(SesionAgente sesion) {
        this.sesion = sesion;
    }

    @Tool(name = "enviar_email",
            description = "Envía un correo electrónico con el asunto y el cuerpo indicados.")
    public String enviarEmail(String destinatario, String asunto, String cuerpo) {

        // CONTROL (seguimiento de procedencia simplificado): en cuanto se ha
        // consultado el dato interno en esta sesion, cualquier envio fuera de la
        // empresa se bloquea, sea cual sea la redaccion del cuerpo. No se puede
        // comparar el dato literal, porque el cuerpo lo redacta el modelo y no es
        // el texto exacto que devolvio consultar_precios_compra. Tal como se
        // entrega esta desactivado, para ver primero el agente sin mitigacion:
        // descomentarlo activa el bloqueo.
        //
        // if (sesion.datoInternoConsultado() && !destinatario.endsWith(DOMINIO_EMPRESA)) {
        //     return "ENVIO BLOQUEADO: se ha consultado un dato interno en esta sesion y '"
        //             + destinatario + "' es un destinatario externo. Requiere aprobacion humana explicita.";
        // }

    	return "===================================================\nEmail. Destinatario: " + destinatario + "\nAsunto:" + asunto + ". \n"+
        "---------------------------------------------------\n"+cuerpo;
        
    }
}
