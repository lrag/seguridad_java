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
		response.sendRedirect("login.html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String accion = request.getParameter("accion");

		if("login".equals(accion)){
			login(request, response);
		} else if("logout".equals(accion)){
			logout(request, response);
		}
	}

	private void login(HttpServletRequest request,
					   HttpServletResponse response)
					   throws IOException {

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

				HttpSession s = null;

				//Esto para evitar session hijacking
				//si no existe no la crea con false, devuelve null

				s = request.getSession(false);
				if(s != null){
					s.invalidate();
				}
				
				//Desde JEE 7:
				/*
				request.changeSessionId();
				*/

				//Solicitamos la session asociada al usuario
				//con true, si no existe, crea una
				//El identificador de session te lo pueden mandar por
				//sobreescritura de url o por cookie
				s = request.getSession(true);

				//Podemos establecer el tiempo de vida de la session
				//en segundos(en tomcat 30 min, por defecto)
				//Y deberiamos avisar al usuario cuando vaya a caducar
				//mediante javascript (avisoTimeout.jsp)
				//el intervalo deber�a de ser mas exigente para los roles
				//m�s comprometidos (administradores, etc)
				/*
				 */
				int segundos = 240;
				s.setMaxInactiveInterval(segundos);
				s.setAttribute("segundos", segundos);
				
				//Guardamos algo en la sesión para diferenciarla de aquellas que se
				//hayan podido crear de manera autom�tica
				Usuario usr = new Usuario(
						rs.getInt("id"),
						rs.getString("nombre"),
						rs.getString("login"),
						null //rs.getString("pw") //Ser�a interesante guardar el usuario sin el password
					); 
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
