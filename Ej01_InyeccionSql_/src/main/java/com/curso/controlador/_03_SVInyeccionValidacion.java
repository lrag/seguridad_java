package com.curso.controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SVInyeccionValidacion")
public class _03_SVInyeccionValidacion extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//Lista blanca: solo se acepta lo que encaje con el patron, todo lo
	//demas se rechaza sin intentar "arreglarlo" (a diferencia del escape)
	//Esta tecnica es facil de aplicar en campos con formato cerrado como
	//un login, pero se complica en campos libres como una contraseña con
	//simbolos exigidos por politica de seguridad
	private static final Pattern LOGIN_VALIDO = Pattern.compile("^[a-zA-Z0-9_]{1,20}$");
	private static final Pattern PW_VALIDO = Pattern.compile("^[a-zA-Z0-9_]{1,20}$");

    public _03_SVInyeccionValidacion() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");

		//Si no cumple el formato esperado, se corta aqui, antes de
		//construir ninguna consulta
		if (login == null || pw == null || !LOGIN_VALIDO.matcher(login).matches() || !PW_VALIDO.matcher(pw).matches()) {
			response.sendRedirect("03_loginInyeccionValidacion.html?error=formato");
			return;
		}

		Connection cx = null;
		try {
			Class.forName("org.h2.Driver");
			cx = DriverManager.getConnection("jdbc:h2:c:/H2/bbdd_seguridad_2026","sa","");
			Statement st = cx.createStatement();

			//Más laborioso, mejor que codificar
			String sql = "select * from usuario where login='"+login+"' and pw='"+pw+"'";

			System.out.println("Consulta: "+sql);

			ResultSet rs = st.executeQuery(sql);
			if(rs.next()){
				response.sendRedirect("inicio.html");
			} else {
				response.sendRedirect("03_loginInyeccionValidacion.html");
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
