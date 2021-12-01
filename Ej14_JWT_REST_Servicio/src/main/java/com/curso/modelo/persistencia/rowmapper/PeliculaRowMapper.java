package com.curso.modelo.persistencia.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.curso.modelo.entidad.Pelicula;

@Component
public class PeliculaRowMapper implements RowMapper<Pelicula>{

	@Override
	public Pelicula mapRow(ResultSet rs, int rowNum) throws SQLException {
		Pelicula p = new Pelicula(rs.getInt("ID"), 
								  rs.getString("TITULO"), 
								  rs.getString("DIRECTOR"), 
								  rs.getString("GENERO"), 
								  rs.getInt("YEAR"));
		return p;
	}
	
}
