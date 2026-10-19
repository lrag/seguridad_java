package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// Permiso minimo: el usuario lo fija la aplicacion al crear la herramienta, a
// partir de la sesion autenticada. El modelo solo elige el pedido; no tiene
// ningun parametro con el que decidir sobre que usuario opera.
public class HerramientaPedidoSegura {

    private final String usuarioAutenticado;

    public HerramientaPedidoSegura(String usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
    }

    @Tool(name = "consultar_pedido_seguro",
            description = "Consulta el estado de un pedido del usuario autenticado en la sesion actual, dado el pedido_id.")
    public String consultarPedidoSeguro(String pedidoId) {
        return "Pedido " + pedidoId + " del usuario " + usuarioAutenticado + ": en transito.";
    }
}
