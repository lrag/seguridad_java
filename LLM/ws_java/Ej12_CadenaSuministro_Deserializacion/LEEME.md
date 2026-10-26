# Ej12 — Inyección de código durante la deserialización

Este proyecto se sale del tema del curso (no usa ningún LLM), pero aborda el
mismo problema que el script Python `12_Deserializacion_Pickle_ejemplo.py` del
capítulo LLM04 (cadena de suministro, 5.2), traducido al mundo Java para quien
venga de ahí.

## El problema

Deserializar datos de origen no verificado puede **ejecutar código**, antes de
que nadie use el objeto para nada. Está en el **OWASP Top 10**: fue
`A8:2017 Insecure Deserialization` y hoy forma parte de
`A08:2021 Software and Data Integrity Failures`.

| Mundo Python / PyTorch            | Equivalente Java                          |
|-----------------------------------|-------------------------------------------|
| `pickle` / `torch.load` por defecto | `ObjectInputStream` sin filtro            |
| `__reduce__`                      | `readObject(ObjectInputStream)`           |
| `torch.load(weights_only=True)`   | `ObjectInputFilter` con lista blanca      |
| `safetensors`                     | un formato de solo datos (JSON, protobuf) |

En Python, un fichero de pesos de PyTorch en formato clásico (`.pt` / `.bin`)
se guarda con `pickle`. Cargarlo con `torch.load` puede ejecutar lo que el
fichero decida. En Java, `ObjectInputStream.readObject()` invoca el método
`readObject` del objeto durante la carga, con el mismo efecto.

En Java el atacante no puede aportar código, solo **datos**: necesita una clase
que ya esté en la aplicación y cuyo `readObject` haga algo delicado con ellos.
Es lo que muestra `ClasePeligrosa`: una clase legítima cuyo `readObject` pasa
el campo `query` a `Repositorio.ejecutarQuery`. Quien fabrica el fichero elige
la consulta y la aplicación la ejecuta al cargarlo. El repositorio es
`transient` y se obtiene dentro de `readObject`, porque al deserializar Java no
ejecuta el constructor de la clase ni sus inicializadores de campo: solo el
constructor sin argumentos de la primera superclase no serializable. Por eso una
validación en el constructor no protege a un objeto deserializado.

Un ataque real tiene dos variantes:

- **Una clase propia** como `ClasePeligrosa`, que hace algo delicado con los
  datos del flujo. Exige conocer la aplicación.
- ***Gadget chains***: clases que ya están en el *classpath* (de librerías como
  commons-collections), inocentes por separado, que combinadas acaban
  dirigiendo la ejecución hasta algo como `Runtime.exec`. No exige conocer la
  aplicación: herramientas públicas como **ysoserial** generan las cadenas ya
  hechas para las librerías más comunes, y por eso hubo explotación masiva en
  WebLogic, JBoss, WebSphere o Jenkins.

En Python es aún más fácil: `__reduce__` puede devolver cualquier función
importable (por ejemplo `os.system`) y pickle la llama tal cual, sin necesidad
de *gadgets*. El *payload* del script solo imprime un mensaje, pero correría con
los permisos del proceso que carga el fichero.

## Mitigaciones en Java, de más fuerte a más débil

1. **No usar la serialización nativa con datos que vengan de fuera.** Un
   formato de solo datos (JSON, protobuf...) mapeado a un tipo concreto que
   elige *el código*, no el fichero, no tiene ningún mecanismo para ejecutar
   nada al cargarse. Es el equivalente de safetensors. *Ojo:* Jackson con
   *default typing* (`activateDefaultTyping`) o con
   `@JsonTypeInfo(use = Id.CLASS)` deja que el JSON diga qué clase instanciar
   y reintroduce el mismo problema.
2. **`ObjectInputFilter` por flujo, con lista blanca y límites.** Rechaza las
   clases no permitidas *antes* de instanciarlas, así que su `readObject` nunca
   se ejecuta. Siempre lista blanca: una lista negra de *gadgets* conocidos
   queda incompleta en cuanto se publica uno nuevo. Los límites (`maxdepth`,
   `maxrefs`, `maxbytes`, `maxarray`) protegen además de la denegación de
   servicio.
3. **Filtro global de la JVM** (`-Djdk.serialFilter=...`, Java 9+; fábrica de
   filtros `jdk.serialFilterFactory`, Java 17). Cubre también a las librerías
   que deserializan por su cuenta, a las que no se les puede poner un filtro a
   mano.
4. **Verificar integridad y origen antes de deserializar**: firma o HMAC de los
   datos, comprobado antes de abrirlos. La misma idea que fijar un modelo por
   *hash* en LLM04.
5. **Reducir los *gadgets***: quitar dependencias que no se usan y mantener
   actualizadas las demás. Un ataque real encadena clases que ya están en el
   *classpath* (commons-collections fue el caso histórico).
6. **Mínimo privilegio** para el proceso que deserializa.

Además, los datos que viajan serializados conviene que sean `record`: Java los
reconstruye llamando a su constructor canónico y no pueden definir
`readObject`.

Ninguna es infalible por separado: la 1 elimina el problema de raíz y las demás
lo contienen cuando la 1 no es posible.

## Ejecutar la versión Java

```
mvn -q package
java -jar target/ej12-cadenasuministro-deserializacion-1.0.0.jar
```

Pasos de la demo:

| Paso | Qué hace | Resultado |
|------|----------|-----------|
| 1 | Guarda una `ClasePeligrosa` con una consulta maliciosa | |
| 2 | La carga sin filtro | **La consulta se ejecuta** |
| 3 | Lo carga con la lista blanca (mitigación 2) | Bloqueado |
| 4 | Carga un dato real con la lista blanca | Pasa |
| 5 | Carga un dato real demasiado grande | Bloqueado por `maxarray` |
| 6 | Guarda y carga el dato real en JSON (mitigación 1) | Pasa, sin serialización nativa |

Para ver la mitigación 3 sin tocar el código, se arranca con un filtro global;
entonces el paso 2 también queda bloqueado:

```
java "-Djdk.serialFilter=com.curso.DatoReal;java.lang.String;!*" -jar target/ej12-cadenasuministro-deserializacion-1.0.0.jar
```

## Ejecutar la versión Python

Está en `12_Deserializacion_Pickle_ejemplo.py`, copiada aquí para tenerlas
juntas. Necesita `torch` y `safetensors` (ver `software-curso`), y se lanza
desde una terminal:

```
python 12_Deserializacion_Pickle_ejemplo.py
```

> Nota: con `torch` 2.5 y anteriores, `weights_only` es `False` por defecto, así
> que el paso 2 ejecuta el *payload*. En `torch` 2.6+ el valor por defecto pasó
> a `True`; ahí el paso 2 ya no lo ejecuta, pero el paso 3 (`weights_only=False`
> explícito) sigue siendo vulnerable, que es lo que hace mucho código para
> cargar modelos antiguos.
