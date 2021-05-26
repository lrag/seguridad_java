package com.curso.clienteservicio;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ClienteServicioPeliculas {

	public static void main(String[] args) {
		try {

			Client client = Client.create();
			//metemos la autenticación
			client.addFilter(new HTTPBasicAuthFilter("a", "a"));
			
			//Establecemos el recurso al que queremos acceder
			WebResource webResource = client.resource("http://localhost:8080/Ej13_REST_Servicio/servicios/peliculas");
			
			//Invocamos al metodo get que nos retornará un JSON
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			
			//Si el estado es distinto a 200 algo mal a ocurrido
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			
			//obtenemos el cliente en json
			String output = response.getEntity(String.class);

			System.out.println("Resultado del servidor.... \n");
			System.out.println(output);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
