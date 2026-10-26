package com.curso;

// Simula un repositorio de acceso a base de datos: la "clase que hace cosas
// delicadas" que usa ClasePeligrosa. No es Serializable, como no lo seria uno
// real con su conexion; por eso ClasePeligrosa lo declara transient y lo
// obtiene al deserializarse.
public class Repositorio {

	public void ejecutarQuery(String query) {
		System.out.println("Ejecutando la consulta: "+query);
	}

}
