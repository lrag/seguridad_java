package com.curso.servicio;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.curso.modelo.entidad.Pelicula;
import com.curso.modelo.negocio.GestorPeliculas;
import com.curso.servicio.dto.PeliculaDTO;

@Path("peliculas")
public class ServicioPeliculas {

	private GestorPeliculas gestorPeliculas = new GestorPeliculas();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertar(PeliculaDTO peliculaDTO){
		Pelicula pelicula = peliculaDTO.asPelicula();
		gestorPeliculas.insertarPelicula(pelicula);
		return Response.status(200).entity(pelicula).build();
	}
	
	@PUT
	//@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modificar(PeliculaDTO pelicula){
		//gestorPeliculas.modificarPelicula(pelicula.asPelicula());
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response borrar(@PathParam("id") int id){
		//gestorPeliculas.borrarPelicula(id);
		return Response.status(200).build();
	}
	
	//http://localhost:8080/Ej10_REST_Servicio/servicios/peliculas/5
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response buscar(@PathParam("id") int id){
		Pelicula pelicula = gestorPeliculas.buscarPelicula(id);
		if(pelicula!=null) {
			return Response.status(200).entity(new PeliculaDTO(pelicula)).build();
		} 
		return Response.status(404).entity("Pel�cula no encontrada").build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//@Path("peliculas")
	public List<PeliculaDTO> listarTodos(){

		System.out.println("=======================================");
		System.out.println("LISTANDO PELICULAS");
		List<Pelicula> peliculas = gestorPeliculas.listarPeliculas();
		List<PeliculaDTO> peliculasDTO = new ArrayList<>();
		for(Pelicula c: peliculas){
			peliculasDTO.add(new PeliculaDTO(c));			
		}
		//llamada a la lógica de negocio
		return peliculasDTO;	
	}
	
}







