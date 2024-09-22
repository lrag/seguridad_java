package com.curso.controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.curso.modelo.entidad.Usuario;

@WebServlet("/SVLogin")
public class SVLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVLogin() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");

		Connection cx = null;
		try {
			Class.forName("org.h2.Driver");			
			cx = DriverManager.getConnection("jdbc:h2:c:/H2/bbdd_seguridad","sa","");
			
			PreparedStatement pst = cx.prepareStatement("select * from usuario where login=? and pw=?");
			pst.setString(1, login);
			pst.setString(2, pw);
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				
				HttpSession s = request.getSession(false);
				if(s != null){
					s.invalidate();
				}
								
				s = request.getSession(true);	

				Usuario usr = new Usuario(rs.getInt("id"), 
								rs.getString("nombre"), 
								rs.getString("login"), 
								rs.getString("pw"));
				s.setAttribute("usuario",usr);
				
				//        //
				// COOKIE //
				//        //
				Cookie galleta = new Cookie("galletilla", "VALOR");
				
				//con esto podemos hacer que la cookie solo se 
				//envie a a estas URLs
				//galleta.setPath("/Ej05_Cookies/seguro");
				
				//Para hacer que las cookies solo se vean
				//en el servidor, no en el javascript
				//ojo, se siguen existiendo en el navegador
				//pero NO son visibles en javascript
				//Se puede hacer que en el server tomcat
				//servers/tomcat v7.0/context.xmls
				//se configure para desabilitarlo
				//galleta.setHttpOnly(true);
				//galleta.setSecure(true);
				
				response.addCookie(galleta);

				response.sendRedirect("seguro/paginaPrivada1.jsp");
				
			} else {
				response.sendRedirect("login.html");
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
