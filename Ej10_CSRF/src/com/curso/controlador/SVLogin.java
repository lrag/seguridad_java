package com.curso.controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//Invalidamos la sesion
		HttpSession sesion = request.getSession();
		if(sesion != null){
			sesion.invalidate();
		}
		response.sendRedirect("../login.html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");
		String accion = request.getParameter("accion");

		if("login".equals(accion)){
			login(request, response, login, pw);
		} else if("logout".equals(accion)){
			logout(request, response);
		}
		
	}

	private void login(HttpServletRequest request,
			HttpServletResponse response, String login, String pw)
			throws IOException {
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
								null /*rs.getString("pw")*/);
				s.setAttribute("usuario",usr);
				
				response.sendRedirect("seguro/inicio.jsp");
				
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
