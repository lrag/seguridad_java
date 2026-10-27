package com.curso;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

// Una unica coleccion vectorial compartida por todas las empresas (tenants),
// por simplicidad de implementacion. Cada documento lleva su tenant_id en los
// metadatos. SimpleVectorStore es una base vectorial en memoria, el
// equivalente del Chroma en memoria del script Python.
public class BBDDVectorial {

    private final VectorStore vectorStore;

    public BBDDVectorial(EmbeddingModel embeddingModel) {
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
    }

    public void ingerir(String tenantId, String texto) {
        vectorStore.add(List.of(new Document(texto, Map.of("tenant_id", tenantId))));
    }

    // El fallo: busca en toda la coleccion, sin filtrar por tenant. Devuelve
    // documentos de cualquier empresa que esten cerca de la pregunta.
    public List<Document> buscarSinFiltro(String pregunta, int k) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(pregunta).topK(k).build());
    }

    // La correccion: el tenant es un parametro OBLIGATORIO de la busqueda, asi
    // que no se puede olvidar el filtro. Ese tenantId tiene que salir de la
    // sesion autenticada, nunca de algo que el usuario o el modelo puedan
    // elegir.
    public List<Document> buscar(String pregunta, int k, String tenantId) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(pregunta)
                .topK(k)
                .filterExpression("tenant_id == '" + tenantId + "'")
                .build());
    }
}
