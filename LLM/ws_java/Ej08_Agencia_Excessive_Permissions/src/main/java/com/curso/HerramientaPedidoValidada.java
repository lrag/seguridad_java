package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// Misma firma que HerramientaPedidoPermisoExcesivo (el modelo sigue eligiendo
// usuarioId), pero la herramienta conoce el usuario de la sesion autenticada
// y comprueba en codigo que coincide antes de hacer nada. Si el modelo pide
// datos de otro usuario, se lanza una excepcion: la decision no la toma el
// modelo, la toma la herramienta (mitigacion 4.7.2).
public class HerramientaPedidoValidada {

    private final String usuarioAutenticado;

    public HerramientaPedidoValidada(String usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
    }

    @Tool(name = "consultar_pedido",
            description = "Consulta el estado de un pedido de un usuario, dado su usuario_id y pedido_id.")
    public String consultarPedido(String usuarioId, String pedidoId) {
        if (!usuarioAutenticado.equals(usuarioId)) {
            throw new AccesoDenegadoException("el usuario " + usuarioAutenticado
                    + " no puede consultar pedidos del usuario " + usuarioId);
        }
        return "Pedido " + pedidoId + " del usuario " + usuarioId + ": en transito.";
    }
}
