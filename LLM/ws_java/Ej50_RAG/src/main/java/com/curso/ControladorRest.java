package com.curso;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// La misma consulta como API REST, para probarla desde Postman:
//
//   POST http://localhost:8080/api/consulta
//   Content-Type: application/json
//
//   {"pregunta": "¿Qué hay de primer plato en el comedor y cuál es el postre?"}
//
// Devuelve un JSON con la pregunta, el contexto recuperado y la respuesta.
@RestController
@RequestMapping("/api")
public class ControladorRest {

    // El cuerpo JSON de la peticion.
    public record Consulta(String pregunta) {
    }

    private final RAG rag;

    public ControladorRest(RAG rag) {
        this.rag = rag;
    }

    @PostMapping("/consulta")
    public RAG.Resultado consultar(@RequestBody Consulta consulta) {
        return rag.responder(consulta.pregunta());
    }
}
