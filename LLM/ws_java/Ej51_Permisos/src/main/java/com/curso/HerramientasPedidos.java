package com.curso;

import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

// Las herramientas que ve el modelo. No contienen logica de negocio ni
// comprobaciones de permisos: delegan en ServicioPedidos, que es quien decide
// con @PreAuthorize. Lo unico que anaden es anotar cada llamada en la Traza y
// convertir un acceso denegado en un texto que se devuelve al modelo, para
// que pueda explicarlo en su respuesta.
@Component
public class HerramientasPedidos {

    private final ServicioPedidos servicioPedidos;
    private final Traza traza;

    public HerramientasPedidos(ServicioPedidos servicioPedidos, Traza traza) {
        this.servicioPedidos = servicioPedidos;
        this.traza = traza;
    }

    @Tool(name = "listar_pedidos", description = "Lista todos los pedidos con su estado.")
    public String listarPedidos() {
        try {
            Map<String, String> pedidos = servicioPedidos.listarPedidos();
            String resultado = pedidos.toString();
            traza.anotar("listar_pedidos()", resultado, false);
            return resultado;
        } catch (AccessDeniedException e) {
            return denegado("listar_pedidos()");
        }
    }

    @Tool(name = "consultar_pedido", description = "Consulta el estado de un pedido, dado su número de pedido.")
    public String consultarPedido(String pedidoId) {
        try {
            String resultado = servicioPedidos.consultarPedido(pedidoId);
            traza.anotar("consultar_pedido(" + pedidoId + ")", resultado, false);
            return resultado;
        } catch (AccessDeniedException e) {
            return denegado("consultar_pedido(" + pedidoId + ")");
        }
    }

    @Tool(name = "eliminar_pedido", description = "Elimina permanentemente un pedido, dado su número de pedido.")
    public String eliminarPedido(String pedidoId) {
        try {
            String resultado = servicioPedidos.eliminarPedido(pedidoId);
            traza.anotar("eliminar_pedido(" + pedidoId + ")", resultado, false);
            return resultado;
        } catch (AccessDeniedException e) {
            return denegado("eliminar_pedido(" + pedidoId + ")");
        }
    }

    private String denegado(String llamada) {
        String resultado = "ACCESO DENEGADO: no hay permiso para esta operación.";
        traza.anotar(llamada, resultado, true);
        return resultado;
    }
}
