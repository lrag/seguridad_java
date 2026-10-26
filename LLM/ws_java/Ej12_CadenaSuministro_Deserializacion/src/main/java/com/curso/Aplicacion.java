package com.curso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

// Inyeccion de codigo durante la deserializacion (OWASP A08:2021 Software and
// Data Integrity Failures; antes A8:2017 Insecure Deserialization). Es el
// equivalente Java del script Python 12_Deserializacion_Pickle_ejemplo.py, que
// trata el mismo problema con el formato pickle de PyTorch (LLM04 5.2, modelos
// de origen no verificado).
//
// -------------------------------------------------------------------------
// Para los que vienen de Java y no conocen pickle ni los ficheros .pt:
//
// En Python, "pickle" es el mecanismo estandar para serializar objetos a un
// fichero. Al deserializar (cargar), pickle no se limita a leer datos:
// reconstruye el objeto, y esa reconstruccion puede invocar cualquier funcion
// que el fichero indique (via el metodo __reduce__). Cargar un pickle de
// origen desconocido es, por tanto, ejecutar lo que el fichero decida.
//
// Los ficheros de pesos de PyTorch en formato clasico (.pt / .bin) se guardan
// con pickle. Por eso "cargar un modelo" descargado de un repositorio publico
// con torch.load puede ejecutar codigo antes de usar el modelo para nada. La
// alternativa segura en ese mundo es el formato safetensors, que solo sabe
// describir tensores y no puede ejecutar nada.
//
// Java tiene exactamente el mismo problema con su serializacion nativa:
// ObjectInputStream.readObject() reconstruye el objeto e invoca su metodo
// readObject privado durante la carga. Si ese readObject hace algo delicado con
// los datos del objeto, quien fabrica el fichero decide que se ejecuta (ver
// ClasePeligrosa, que lanza la consulta que venga en el fichero). La correspondencia es directa:
//
//   pickle / torch.load (por defecto)      ->  ObjectInputStream sin filtro
//   __reduce__                             ->  readObject(ObjectInputStream)
//   torch.load(weights_only=True)          ->  ObjectInputFilter con lista blanca
//   safetensors                            ->  un formato de solo datos (JSON, protobuf...)
// -------------------------------------------------------------------------
//
// MITIGACIONES EN JAVA, de mas fuerte a mas debil:
//
// 1. No usar la serializacion nativa con datos que vengan de fuera (paso 6).
//    Un formato de solo datos (JSON, protobuf...) mapeado a un tipo concreto
//    que elige EL CODIGO, no el fichero, no tiene ningun mecanismo para
//    ejecutar nada al cargarse. Ojo: Jackson con "default typing" activado
//    (activateDefaultTyping) o con @JsonTypeInfo(use = Id.CLASS) deja que el
//    JSON diga que clase instanciar, y reintroduce exactamente el mismo
//    problema; hay una larga lista de CVE de Jackson por eso.
//
// 2. Si no queda mas remedio que usarla: un ObjectInputFilter por flujo con
//    LISTA BLANCA de clases y limites de recursos (pasos 3 a 5). Rechaza las
//    clases antes de instanciarlas, asi que su readObject nunca llega a
//    ejecutarse. Siempre lista blanca, nunca lista negra: una lista negra de
//    "gadgets conocidos" queda incompleta en cuanto se publica uno nuevo. Los
//    limites (maxdepth, maxrefs, maxbytes, maxarray) cubren ademas la
//    denegacion de servicio por objetos gigantes o anidados.
//
// 3. Un filtro global para toda la JVM, que tambien cubre a las librerias que
//    deserializan por su cuenta y a las que no puedes ponerles el filtro a
//    mano: la propiedad -Djdk.serialFilter=... (Java 9+) o, para combinar
//    filtros por contexto, una fabrica de filtros con jdk.serialFilterFactory
//    (Java 17, JEP 415).
//
// 4. Verificar la integridad y el origen ANTES de deserializar: firmar o
//    calcular un HMAC de los datos al guardarlos y comprobarlo al cargarlos.
//    Es la misma idea que fijar un modelo por hash en LLM04: si el fichero no
//    es el que se genero, ni se llega a abrir.
//
// 5. Reducir los "gadgets" disponibles: quitar del classpath las dependencias
//    que no se usan y mantener actualizadas las demas (commons-collections fue
//    el ejemplo historico). Un ataque real encadena clases que ya estan en tu
//    aplicacion; cuantas menos haya, menos cadenas posibles.
//
// 6. Minimo privilegio: si algo falla, el payload corre con los permisos del
//    proceso que deserializa. Cuantos menos tenga, menos dano puede hacer.
//
// Ninguna es infalible por separado; la 1 elimina el problema de raiz y las
// demas lo contienen cuando la 1 no es posible.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final Path FICHERO = Path.of("objeto.ser");

    // Filtro declarativo del JDK: lista blanca de clases + limites de recursos.
    // Los arrays de tipos primitivos (el double[] de DatoReal) se aceptan sin
    // nombrarlos; "!*" rechaza todo lo que no este en la lista.
    private static final ObjectInputFilter FILTRO = ObjectInputFilter.Config.createFilter(
            "maxdepth=5;maxrefs=100;maxbytes=10000;maxarray=1000;"
            + "com.curso.DatoReal;java.lang.String;!*");

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================================");
        System.out.println("1. Guardando una ClasePeligrosa con una consulta maliciosa, como si fuera un checkpoint");
        serializar(new ClasePeligrosa("DELETE FROM CLIENTES, JUA JUA JUAJUA!!!!"), FICHERO);
        System.out.println("Escrito " + FICHERO + ": por fuera es un fichero serializado cualquiera");
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Cargando con ObjectInputStream sin filtro (como torch.load por defecto)");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHERO.toFile()))) {
            Object resultado = in.readObject();
            System.out.println("Valor devuelto: " + resultado);
        } catch (Exception error) {
            // Solo pasa si se arranca la JVM con un filtro global
            // (-Djdk.serialFilter=...): bloquea aunque este codigo no ponga ninguno.
            System.out.println("Carga bloqueada por el filtro global de la JVM: " + error.getMessage());
        }
        System.out.println();

        System.out.println("========================================================");
        System.out.println("3. Cargando con lista blanca de clases (como torch.load weights_only=True)");
        System.out.println("Solo se permite deserializar DatoReal; cualquier otra clase se rechaza.");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHERO.toFile()))) {
            in.setObjectInputFilter(FILTRO);
            Object resultado = in.readObject();
            System.out.println("Valor devuelto: " + resultado);
        } catch (Exception error) {
            System.out.println("Carga bloqueada: " + error.getMessage() + " (el payload NO se ha ejecutado)");
        }
        System.out.println();

        System.out.println("========================================================");
        System.out.println("4. Contraste: guardando y cargando un dato real, con la misma lista blanca");
        serializar(new DatoReal("capa1.peso", new double[] {0.12, 0.98, 0.55, 0.31}), FICHERO);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHERO.toFile()))) {
            in.setObjectInputFilter(FILTRO);
            Object resultado = in.readObject();
            System.out.println("Contenido cargado: " + resultado);
            System.out.println("El dato real pasa el filtro y no ejecuta nada al cargarse.");
        }
        System.out.println();

        System.out.println("========================================================");
        System.out.println("5. Limites de recursos: un DatoReal legitimo pero con 5000 pesos (maxarray=1000)");
        serializar(new DatoReal("capa_gigante", new double[5000]), FICHERO);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHERO.toFile()))) {
            in.setObjectInputFilter(FILTRO);
            Object resultado = in.readObject();
            System.out.println("Valor devuelto: " + resultado);
        } catch (Exception error) {
            System.out.println("Carga bloqueada: " + error.getMessage());
            System.out.println("La clase esta permitida, pero el tamano no: protege de la denegacion de servicio.");
        }
        System.out.println();

        System.out.println("========================================================");
        System.out.println("6. Alternativa de raiz: un formato de solo datos (JSON), como safetensors");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new DatoReal("capa1.peso", new double[] {0.12, 0.98, 0.55, 0.31}));
        System.out.println("JSON guardado: " + json);
        DatoReal cargado = mapper.readValue(json, DatoReal.class);
        System.out.println("Contenido cargado: " + cargado);
        System.out.println("La clase la elige el codigo (DatoReal.class), no el fichero: no hay");
        System.out.println("ningun mecanismo para que el JSON pida instanciar otra cosa.");
        System.out.println();

        Files.deleteIfExists(FICHERO);
    }

    private static void serializar(Object objeto, Path destino) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(destino.toFile()))) {
            out.writeObject(objeto);
        }
    }
}
