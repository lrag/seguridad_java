package com.curso.controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SVInyeccionPreparedStatement")
public class _03_SVInyeccionPreparedStatement extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public _03_SVInyeccionPreparedStatement() {
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
			cx = DriverManager.getConnection("jdbc:h2:c:/H2/bbdd_seguridad_2026","sa","");
			
			//La mejor solución suele ser crear un prepareStatement para hacer la consulta
			//ya que previene la inyeccion sql y mejora la eficiencia de las consultas
			PreparedStatement pst = cx.prepareStatement("select * from usuario where login=? and pw=?");
			pst.setString(1, login);
			pst.setString(2, pw);//' or '1'='1
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				response.sendRedirect("inicio.html");
			} else {
				response.sendRedirect("03_loginInyeccionPreparedStatement.html");
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
