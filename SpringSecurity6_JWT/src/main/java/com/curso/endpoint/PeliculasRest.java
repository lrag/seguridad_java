package com.curso.endpoint;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.curso.endpoint.dto.PeliculaDto;
import com.curso.modelo.entidad.Pelicula;
import com.curso.modelo.negocio.ServicioPeliculas;

@RestController
@RequestMapping(path="peliculas")
public class PeliculasRest {

	@Autowired
	private ServicioPeliculas gestorPeliculas;
	
	@PostMapping(
			/*path="peliculas",*/
			consumes=MediaType.APPLICATION_JSON_VALUE
		)
	public ResponseEntity<String> insertar(@RequestBody PeliculaDto peliculaDto) throws Exception{
		gestorPeliculas.insertar(peliculaDto.asPelicula());
		return new ResponseEntity<String>("La pelicula se insertó", HttpStatus.CREATED);
	}

	@PutMapping(
			path="/{id}",
			consumes=MediaType.APPLICATION_JSON_VALUE
		)
	public ResponseEntity<String> modificar(@RequestBody PeliculaDto peliculaDto, @PathVariable Integer id){
		peliculaDto.setId(id);
		gestorPeliculas.modificar(peliculaDto.asPelicula());
		return new ResponseEntity<String>("La pelicula se ha modificado", HttpStatus.OK);
	}
	
	@DeleteMapping(path="/{id}")
	public ResponseEntity<String> borrar(@PathVariable Integer id){
		gestorPeliculas.borrar(new Pelicula(id));
		return new ResponseEntity<String>("La pelicula ha borrado", HttpStatus.CREATED);
	}
	
	@GetMapping(
			path="{id}", 
			produces=MediaType.APPLICATION_JSON_VALUE
		)
	public ResponseEntity<PeliculaDto> buscar(@PathVariable Integer id){
		Pelicula p = gestorPeliculas.buscar(id);
		if(p==null){
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new PeliculaDto(p), HttpStatus.OK);
	}
	
	//get peliculas?director=XXX&genero=YYY
	@GetMapping(produces=MediaType.APPLICATION_JSON_VALUE)
	public List<PeliculaDto> listar(/*@RequestParam("director") String director, @RequestParam("genero") String genero*/) throws Exception{
		return gestorPeliculas.listar()
			.stream()
			.map( p -> new PeliculaDto(p))
			.collect(Collectors.toList());
	}
	
}























