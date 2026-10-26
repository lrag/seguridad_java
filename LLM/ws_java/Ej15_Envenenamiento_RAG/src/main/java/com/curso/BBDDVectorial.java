package com.curso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

// La base vectorial del RAG. SimpleVectorStore es una base vectorial en
// memoria, el equivalente del Chroma en memoria del script Python.
public class BBDDVectorial {

    private final VectorStore vectorStore;

    public BBDDVectorial(EmbeddingModel embeddingModel) {
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
    }

    // Crea un vector (embedding) por cada documento y lo guarda. No comprueba
    // nada: lo que se ingiere, falso o no, queda disponible para el RAG.
    public void ingerir(List<String> documentos) {
        List<Document> documentosVectoriales = new ArrayList<>();
        for (String documento : documentos) {
            documentosVectoriales.add(new Document(documento));
        }
        vectorStore.add(documentosVectoriales);
        System.out.println(documentos.size() + " documentos indexados.");
    }

    // El documento mas parecido a la pregunta.
    public Document recuperar(String pregunta) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(pregunta).topK(1).build()).get(0);
    }
}
