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

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

@WebServlet("/SVInyeccionEscape")
public class _02_SVInyeccionEscape extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public _02_SVInyeccionEscape() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");

		//Una de las claves es validar los parametros enviados por los usuarios
		//ESAPI es una api creada por la gente de www.owasp.org
		//que nos va a ayudar a validar los datos de entrada de los usuarios
		//saltaria una excepcion en caso de que no pudiera formar la sentencia sql correctamente
		login = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), request.getParameter("login"));
		pw    = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), request.getParameter("pw"));
		
		//sacamos el valor para ver en que lo convierte
		System.out.println("pw:"+pw);

		Connection cx = null;
		try {
			Class.forName("org.h2.Driver");
			cx = DriverManager.getConnection("jdbc:h2:c:/H2/bbdd_seguridad","sa","");
			Statement st = cx.createStatement();

			String sql = "select * from usuario where login='"+login+"' and pw='"+pw+"'";
			System.out.println("Consulta:"+sql);
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()){
				response.sendRedirect("inicio.html");
			} else {
				response.sendRedirect("02_loginInyeccionESCAPE.html");
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
