package com.curso.controlador;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.curso.modelo.entidad.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SVInyeccionORM")
public class _04_SVInyeccionORM extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	//Hibernate, JPA, lo mismo es.
	private SessionFactory sessionFactory;
	
    public _04_SVInyeccionORM() {
        super();
    }

    public void init(){
    	WebApplicationContext context = WebApplicationContextUtils
    		    .getRequiredWebApplicationContext(getServletContext());
    	sessionFactory = context.getBean(SessionFactory.class);    	
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");

		Session s = sessionFactory.openSession();
		
		//Aunque utilicemos frameworks apliamente utilizados como hibernate, debemos de tener igual
		//cuidado con las inyecciones
		//Este ejemplo, aunque por debajo hibernate use prepared statement, en este caso no lo estamos
		//usando, por lo que seguiria siendo un coladero
		List<Usuario> rs = 
			s.createQuery("select u from Usuario u where u.login='"+login+"' and pw='"+pw+"'", Usuario.class).list();
		
		//deberemos de usar algo así, que seria el equivalente a prepared statement en hibernate
		//Query q = s.createQuery("from Usuario u where u.login=:login and pw=:pw");
		//q.setParameter("login", login);
		//q.setParameter("pw", pw);
		//List<Usuario> rs = q.list();		
		
		if(rs.size()>0){
			response.sendRedirect("inicio.html");
		} else { 
			response.sendRedirect("04_loginInyeccionHibernate.html");
		}		
		
		s.close();
	}

}












