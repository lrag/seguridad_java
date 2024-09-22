package com.curso.controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SVInyeccionSQL")
public class _01_SVInyeccionSQL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public _01_SVInyeccionSQL() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");
	
		Connection cx = null;
		try {
			//h2 es una base de datos pequeña tipo derby o sqlite
			Class.forName("org.h2.Driver");
			//a:a,b:b
			
			//c:/H2/bbdd.mv.db
			cx = DriverManager.getConnection("jdbc:h2:c:/H2/bbdd_seguridad","sa","");
			Statement st = cx.createStatement();
			
			//hay dos usuarios en la base de datos, a y b, con passwords a y b
			String sql = "select * from usuario where login='"+login+"' and pw='"+pw+"'";
			
			System.out.println("Consulta: "+sql);
			
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()){
				response.sendRedirect("inicio.html");
			} else {
				response.sendRedirect("01_loginInyeccionSQL.html");
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
