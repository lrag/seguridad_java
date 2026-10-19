package com.curso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Demo de "inversion de embeddings" por similitud, sin descifrar el vector letra
// a letra: un atacante que solo tiene el embedding de un documento secreto (por
// ejemplo, robado de una base vectorial) prueba una lista de frases candidatas y
// se queda con la mas parecida por similitud coseno. No es una inversion real
// (eso requeriria un modelo decodificador tipo vec2text), pero demuestra que el
// vector conserva suficiente informacion semantica como para identificar el
// contenido original sin haberlo visto nunca en texto plano.
//
// Pasos de un ataque real (a diferencia del diccionario de 6 frases de este demo):
// 1. Exfiltrar los vectores objetivo de una base vectorial mal asegurada (backup
//    filtrado, API sin autenticacion, instancia mal configurada, insider).
// 2. Identificar que modelo de embeddings los genero (se suele deducir por el
//    numero de dimensiones o por metadatos filtrados junto a los vectores),
//    porque comparar solo funciona si las candidatas se embeben con ese mismo
//    modelo.
// 3. Construir un corpus de candidatas mucho mayor que el de este demo: registros
//    publicos filtrados de organizaciones similares, o plantillas generadas por
//    fuerza bruta combinando campos probables (nombres, cifras, ciudades).
// 4. Calcular el embedding de cada candidata con el modelo identificado en el
//    paso 2, para que los vectores sean comparables entre si.
// 5. Comparar cada vector robado contra todas las candidatas. A esa escala ya no
//    vale un bucle como el de este demo: se usa un indice de busqueda por
//    vecinos mas cercanos (FAISS, HNSW) para no comparar uno a uno.
// 6. Quedarse con las candidatas mas similares como mejor reconstruccion
//    aproximada de cada vector robado.
// 7. Opcional, para acercarse mas al texto exacto: tomar las candidatas mejor
//    puntuadas, generar variaciones pequenas de ellas, volver a embeberlas y
//    repetir la comparacion, en vez de conformarse con la primera pasada.
// 8. La busqueda por diccionario no escala a texto libre arbitrario: por eso la
//    investigacion real (vec2text) entrena un modelo que va directamente del
//    vector al texto aproximado, sin necesitar un diccionario de candidatas.
//
// El modelo de embeddings (nomic-embed-text) esta en application.properties.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private record Resultado(double similitud, String candidato) {
    }

    private final EmbeddingModel embeddingModel;

    Aplicacion(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================================");
        System.out.println("1. Documento secreto (el atacante solo tiene el embedding, nunca el texto)");
        String secreto = "El empleado Higinio Alonso tiene un salario anual de 54000 euros y vive en Sevilla.";
        float[] embeddingSecreto = embeddingModel.embed(secreto);
        System.out.println("Vector de " + embeddingSecreto.length + " dimensiones, texto NO disponible para el atacante");

        System.out.println("========================================================");
        System.out.println("2. Diccionario de frases candidatas del atacante");
        List<String> candidatos = List.of(
                "El empleado Higinio Alonso tiene un salario anual de 54000 euros y vive en Sevilla.",
                "Higinio Alonso cobra 54000 euros al año y reside en Sevilla.",
                "El empleado Higinio Alonso gana un salario de 30000 euros y vive en Madrid.",
                "La empleada Laura Perez tiene un salario anual de 54000 euros y vive en Sevilla.",
                "El comedor de la empresa sirve gazpacho los martes.",
                "La capital de Portugal es Pinto.");
        for (String candidato : candidatos) {
            System.out.println("- " + candidato);
        }

        System.out.println("========================================================");
        System.out.println("3. Comparando el embedding secreto contra cada candidata");
        List<Resultado> resultados = new ArrayList<>();
        for (String candidato : candidatos) {
            float[] embeddingCandidato = embeddingModel.embed(candidato);
            resultados.add(new Resultado(similitudCoseno(embeddingSecreto, embeddingCandidato), candidato));
        }

        resultados.sort(Comparator.comparingDouble(Resultado::similitud).reversed());
        for (Resultado resultado : resultados) {
            System.out.printf("%.4f  %s%n", resultado.similitud(), resultado.candidato());
        }

        System.out.println("========================================================");
        System.out.println("4. Mejor reconstruccion aproximada del atacante, sin haber leido el original");
        Resultado mejor = resultados.get(0);
        System.out.printf("%s (similitud %.4f)%n", mejor.candidato(), mejor.similitud());
    }

    // Similitud coseno: producto punto dividido por el producto de las normas.
    // Vale 1 para vectores que apuntan en la misma direccion (mismo significado).
    private static double similitudCoseno(float[] a, float[] b) {
        double productoPunto = 0;
        double normaA = 0;
        double normaB = 0;
        for (int i = 0; i < a.length; i++) {
            productoPunto += a[i] * b[i];
            normaA += a[i] * a[i];
            normaB += b[i] * b[i];
        }
        return productoPunto / (Math.sqrt(normaA) * Math.sqrt(normaB));
    }
}
