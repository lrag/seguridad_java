package com.curso.modelo.persistencia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.curso.modelo.entidad.Pelicula;
import com.curso.modelo.persistencia.rowmapper.PeliculaRowMapper;

@Repository
public class PeliculaDaoJdbcTemplate implements PeliculaDao {

	@Autowired private JdbcTemplate jdbcTemplate;
	@Autowired private PeliculaRowMapper peliculaRowMapper;

	//Con autowired el set es opcional
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public void setPeliculaRowMapper(PeliculaRowMapper peliculaRowMapper) {
		this.peliculaRowMapper = peliculaRowMapper;
	}
	//

	@Override
	public void insertar(Pelicula pelicula) {
		jdbcTemplate.update("insert into pelicula (TITULO,DIRECTOR,GENERO,YEAR) values (?,?,?,?)",
							pelicula.getTitulo(),
							pelicula.getDirector(),
							pelicula.getGenero(),
							pelicula.getYear());
	}
	
	@Override
	public void modificar(Pelicula pelicula) {
		jdbcTemplate.update("update pelicula set TITULO=?,DIRECTOR=?,GENERO=?,YEAR=? where ID=?",
				pelicula.getTitulo(),
				pelicula.getDirector(),
				pelicula.getGenero(),
				pelicula.getYear(),
				pelicula.getId());
	}

	@Override
	public void borrar(Pelicula pelicula) {
		jdbcTemplate.update("delete from pelicula where ID=?",
				pelicula.getId());
	}
	
	@Override
	public List<Pelicula> listar() {
		return jdbcTemplate.query("select * from pelicula", peliculaRowMapper);
	}

	@Override
	public Pelicula buscar(Integer id) {
		List<Pelicula> rs = jdbcTemplate.
			query("select * from pelicula where ID=?", peliculaRowMapper, id);
		if( rs.size()==1 ) {
			return rs.get(0);
		}
		return null;
	}
	
	/*
	public List<Pelicula> listarJDBC(string sql, rowMapper, param){
		Connection cx = null;
		List<Pelicula> peliculas = new ArrayList<Pelicula>();
		try {
			cx = DriverManager.getConnection("","","");
			
			PreparedStatement pst = cx.prepareStatement("select * from peliculas");
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				Pelicula p = new Pelicula(rs.getInt("ID"), 
						rs.getString("TITULO"), 
						rs.getString("DIRECTOR"), 
						rs.getString("GENERO"), 
						rs.getInt("YEAR"));
				peliculas.add(p);				
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				cx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return peliculas;		
	}
	*/
	

}

class Prueba {
	
	public static void main(String[] args) throws SQLException {
		
		_JdbcTemplate jdbcTemplate = new _JdbcTemplate();
		
		List<Object> objetos = jdbcTemplate.query("select * from peliculas", new PeliculaRowMapper());
		
		
		RowMapper<String> rm = new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet arg0, int arg1) throws SQLException {
				return null;
			}
		};
		
		List<Object> objetos2 = jdbcTemplate.query("select * from peliculas",   

			  (rs, arg1) -> new Pelicula(rs.getInt("ID"), 
							  rs.getString("TITULO"), 
							  rs.getString("DIRECTOR"), 
							  rs.getString("GENERO"), 
							  rs.getInt("YEAR"))				  
				
			);
		
		
	}
	
}

class _JdbcTemplate {
	
	public List<Object> query(String sql, RowMapper rm) throws SQLException{
		
		List<Object> objetos = new ArrayList<>();
		//ejecutar la query
		ResultSet rs = null;
		int contador = 1;
		while(rs.next()){
			objetos.add(rm.mapRow(rs, contador));
			contador++;			
		}
		return objetos;
		
	}
	
}

















