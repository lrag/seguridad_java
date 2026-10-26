package com.curso;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

// Equivalente Java del objeto malicioso del script Python (CargaMaliciosa con
// __reduce__). Una clase Serializable puede definir un metodo privado
// readObject(ObjectInputStream), que ObjectInputStream invoca AUTOMATICAMENTE
// al deserializar el objeto, antes de que nadie lo use para nada. Lo que se
// ponga ahi se ejecuta solo por leer el fichero.
//
// La clase no es maliciosa: nadie escribe una clase maliciosa en su propio
// proyecto. El problema es que su readObject usa un dato del objeto (query)
// para hacer algo delicado (Repositorio.ejecutarQuery). El codigo es legitimo;
// lo malicioso son los DATOS, y esos los escribe quien fabrica el fichero. El
// atacante no necesita aportar codigo: le basta con elegir el valor de query.
//
// El repositorio es transient y se obtiene dentro de readObject, como en una
// aplicacion real: un repositorio con su conexion a base de datos no se puede
// serializar, y asi lo unico que controla el atacante es la consulta. Ojo: al
// deserializar NO se ejecuta el constructor de esta clase ni sus
// inicializadores de campo (un "= new Repositorio()" en la declaracion no
// serviria). Java solo llama al constructor sin argumentos de la primera
// superclase NO serializable (aqui Object), saltandose el resto de la cadena
// de constructores. Por eso cualquier validacion puesta en el constructor no
// protege frente a un objeto deserializado.
//
// Un ataque real tiene dos variantes:
// - Una clase propia como esta, cuyo readObject (o readResolve,
//   validateObject...) hace algo delicado con datos del flujo. El atacante
//   tiene que conocer la aplicacion, pero no necesita nada mas.
// - "Gadget chains": encadena clases que YA estan en el classpath (de
//   librerias como commons-collections), cada una inocente por separado, de
//   forma que los datos del flujo acaban dirigiendo la ejecucion hasta algo
//   como Runtime.exec. No hace falta conocer la aplicacion: herramientas
//   publicas como ysoserial generan esas cadenas ya hechas para las librerias
//   mas comunes.
public class ClasePeligrosa implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Repositorio repo;

    private String query;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        System.out.println("[PAYLOAD] Esto se ha ejecutado solo por deserializar el fichero, antes de usar ningun 'modelo'.");
        repo = new Repositorio();
        repo.ejecutarQuery(query);
    }
    
    public ClasePeligrosa(String query) {
		super();
		this.query = query;
	}

	@Override
    public String toString() {
        return "ClasePeligrosa (objeto deserializado)";
    }
}
