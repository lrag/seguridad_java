package com.curso;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Recibe un ticket y se lo pasa a la IA, pero la IA actua en su propio nombre
// (IdentidadAgenteIA, rol AGENTE_IA, solo lectura), no en el del usuario que
// envia la peticion. El resultado es el mismo para un usuario que para un
// empleado: aunque el ticket lo envie "b", el agente no puede eliminar nada.
//
// Para llegar aqui sigue haciendo falta haber iniciado sesion (SeguridadConfig):
// lo que cambia es con que permisos se procesa el ticket.
@Controller
public class ControladorTicketsAgente {

    private final AgenteSoporte agente;
    private final IdentidadAgenteIA identidadAgenteIA;
    private final ServicioPedidos servicioPedidos;
    private final Traza traza;

    public ControladorTicketsAgente(AgenteSoporte agente, IdentidadAgenteIA identidadAgenteIA,
            ServicioPedidos servicioPedidos, Traza traza) {
        this.agente = agente;
        this.identidadAgenteIA = identidadAgenteIA;
        this.servicioPedidos = servicioPedidos;
        this.traza = traza;
    }

    @PostMapping("/ticket-agente")
    public String enviarTicket(@RequestParam String ticket, Model modelo) {
        modelo.addAttribute("ticket", ticket);
        modelo.addAttribute("procesadoComo", IdentidadAgenteIA.NOMBRE);
        modelo.addAttribute("respuesta", identidadAgenteIA.ejecutar(() -> agente.atender(ticket)));
        modelo.addAttribute("llamadas", traza.getLlamadas());
        modelo.addAttribute("pedidos", servicioPedidos.listarPedidos());
        return "index";
    }
}
