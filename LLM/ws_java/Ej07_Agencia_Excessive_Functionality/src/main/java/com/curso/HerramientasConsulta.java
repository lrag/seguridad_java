package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// Catalogo minimo: solo las herramientas que un agente de soporte necesita
// para gestionar un ticket. Ninguna cambia el estado del sistema.
public class HerramientasConsulta {

    @Tool(name = "consultar_estado_pedido", description = "Consulta el estado de un pedido por su ID.")
    public String consultarEstadoPedido(String pedidoId) {
        System.out.println("consultarEstadoPedido");
    	return "El pedido " + pedidoId + " esta en transito.";
    }

    @Tool(name = "consultar_faq", description = "Busca la respuesta a una pregunta frecuente.")
    public String consultarFaq(String pregunta) {
    	System.out.println("consultarFaq: "+pregunta);
        return "No se encontro una FAQ relacionada.";
    }
}
