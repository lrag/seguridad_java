package com.curso;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// La pagina de tickets (templates/index.html). Solo se llega aqui con sesion
// iniciada: si no, Spring Security redirige antes al formulario de login.
@Controller
public class ControladorTickets {

    private final AgenteSoporte agente;
    private final ServicioPedidos servicioPedidos;
    private final Traza traza;

    public ControladorTickets(AgenteSoporte agente, ServicioPedidos servicioPedidos, Traza traza) {
        this.agente = agente;
        this.servicioPedidos = servicioPedidos;
        this.traza = traza;
    }

    @GetMapping("/")
    public String inicio(Model modelo) {
        modelo.addAttribute("pedidos", servicioPedidos.listarPedidos());
        return "index";
    }

    // El agente atiende el ticket en nombre del usuario de la sesion. Despues
    // se muestran su respuesta, las herramientas que ha invocado y como han
    // quedado los pedidos.
    @PostMapping("/ticket")
    public String enviarTicket(@RequestParam String ticket, Model modelo, Principal usuario) {
        modelo.addAttribute("ticket", ticket);
        modelo.addAttribute("procesadoComo", usuario.getName());
        modelo.addAttribute("respuesta", agente.atender(ticket));
        modelo.addAttribute("llamadas", traza.getLlamadas());
        modelo.addAttribute("pedidos", servicioPedidos.listarPedidos());
        return "index";
    }

    // Vuelve a los pedidos iniciales, para repetir la prueba.
    @PostMapping("/restaurar")
    public String restaurar() {
        servicioPedidos.restaurar();
        return "redirect:/";
    }
}
