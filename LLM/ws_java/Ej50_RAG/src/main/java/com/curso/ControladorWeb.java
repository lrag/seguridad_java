package com.curso;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// La pagina web para probar el RAG en el navegador (plantilla
// templates/index.html): un formulario con la pregunta que se envia por POST
// a /consulta, y la misma pagina de vuelta con la respuesta.
@Controller
public class ControladorWeb {

    private final RAG rag;

    public ControladorWeb(RAG rag) {
        this.rag = rag;
    }

    // La pagina vacia, con el formulario.
    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    // Recibe la pregunta del formulario y muestra la pagina con el resultado.
    @PostMapping("/consulta")
    public String consultar(@RequestParam String pregunta, Model modelo) {
        modelo.addAttribute("resultado", rag.responder(pregunta));
        return "index";
    }
}
