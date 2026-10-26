package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// Permiso excesivo: la herramienta es la que la tarea necesita, pero acepta
// como parametro sobre que usuario opera. Quien rellena ese parametro es el
// modelo, y el modelo puede estar leyendo un ticket envenenado.
public class HerramientaPedidoPermisoExcesivo {

    @Tool(
    	name = "consultar_pedido",
        description = "Consulta el estado de un pedido de un usuario, dado su usuario_id y pedido_id."
    )
    public String consultarPedido(String usuarioId, String pedidoId) {
    	System.out.println("consultarPedido");
        return "Pedido " + pedidoId + " del usuario " + usuarioId + ": en transito.";
    }
}
