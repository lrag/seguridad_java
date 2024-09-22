package com.curso.controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.curso.modelo.entidad.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@WebServlet("/SVAutenticacion")
public class SVAutenticacion extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SVAutenticacion() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String login = request.getParameter("username");
		String pw    = request.getParameter("password");
		
		System.out.println(login+","+pw);
		
		Connection cx = null;
		try {
			Class.forName("org.h2.Driver");
			cx = DriverManager.getConnection("jdbc:h2:c:/H2/bbdd_seguridad","sa","");

			PreparedStatement pst = cx.prepareStatement("select * from usuario where login=? and pw=?");
			pst.setString(1, login);
			pst.setString(2, pw);
			ResultSet rs = pst.executeQuery();

			if(rs.next()){
				
				Usuario usuario = new Usuario(
						rs.getInt("id"),
						rs.getString("nombre"),
						rs.getString("login"),
						rs.getString("pw"),
						rs.getString("rol")
					); 
				byte[] clave = CriptografiaUtil.clave.getBytes();

	            String token = Jwts.builder()
	                .signWith(Keys.hmacShaKeyFor(clave), SignatureAlgorithm.HS512)
	                .setHeaderParam("typ", "JWT")
	                .setIssuer("servicio-peliculas")
	                .setAudience("secure-app")
	                .setSubject(usuario.getLogin())
	                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) //Un día
	                .claim("rol", usuario.getRol())
	                .compact();
	            response.addHeader("Authorization", "Bearer " + token);
				
			} else {
				response.sendError(401,"Credenciales incorrectas");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				cx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
