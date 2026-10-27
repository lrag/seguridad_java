package com.curso;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

// La logica de negocio: los pedidos (en memoria) y las operaciones sobre
// ellos. Cada metodo declara con @PreAuthorize que rol hace falta. Spring
// Security lo comprueba con el usuario de la sesion antes de entrar en el
// metodo, y si no tiene el rol lanza AccessDeniedException: da igual que la
// llamada venga de un controlador o de una herramienta invocada por el modelo.
@Service
public class ServicioPedidos {

    private final Map<String, String> pedidos = new LinkedHashMap<>();

    public ServicioPedidos() {
        restaurar();
    }

    // AGENTE_IA: la identidad propia del agente (IdentidadAgenteIA), solo lectura.
    @PreAuthorize("hasAnyRole('USUARIO', 'EMPLEADO', 'AGENTE_IA')")
    public synchronized Map<String, String> listarPedidos() {
        return new LinkedHashMap<>(pedidos);
    }

    @PreAuthorize("hasAnyRole('USUARIO', 'EMPLEADO', 'AGENTE_IA')")
    public synchronized String consultarPedido(String pedidoId) {
        String estado = pedidos.get(pedidoId);
        return estado == null ? "El pedido " + pedidoId + " no existe." : "Pedido " + pedidoId + ": " + estado + ".";
    }

    @PreAuthorize("hasRole('EMPLEADO')")
    public synchronized String eliminarPedido(String pedidoId) {
        return pedidos.remove(pedidoId) == null ? "El pedido " + pedidoId + " no existe."
                : "Pedido " + pedidoId + " eliminado.";
    }

    // Vuelve a los pedidos iniciales, para repetir la prueba. No es una
    // operacion de negocio: no la ve el modelo.
    public synchronized void restaurar() {
        pedidos.clear();
        pedidos.put("201", "en tránsito");
        pedidos.put("555", "entregado");
        pedidos.put("777", "pendiente de pago");
    }
}
