package com.curso.controlador;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.curso.modelo.entidad.Usuario;

@WebServlet("/SVInyeccionHibernate")
public class _04_SVInyeccionHibernate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private SessionFactory sf;
	
    public _04_SVInyeccionHibernate() {
        super();
    }

    public void init(){
    	//creamos la factoria de sessiones de hibernate aqui mismo para hacer el acceso a la bb.dd
	    Map<String, String> settings = new HashMap<>();
	        settings.put(Environment.DRIVER, "org.h2.Driver");
	        settings.put(Environment.URL, "jdbc:h2:file:C:/H2/bbdd_seguridad");
	        settings.put(Environment.USER, "sa");
	        settings.put(Environment.PASS, "");
	        settings.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
	        settings.put(Environment.HBM2DDL_AUTO, "update");
			settings.put(Environment.SHOW_SQL, "true");
			settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
	
		StandardServiceRegistry standardRegistry =
				new StandardServiceRegistryBuilder().applySettings(settings).build();
		MetadataSources sources = new MetadataSources( standardRegistry );
		sources.addAnnotatedClass(Usuario.class);
		sf = sources.getMetadataBuilder().build().buildSessionFactory();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");

		Session s = sf.openSession();
		
		//Aunque utilicemos frameworks apliamente utilizados como hibernate, debemos de tener igual
		//cuidado con las inyecciones
		//Este ejemplo, aunque por debajo hibernate use prepared statement, en este caso no lo estamos
		//usando, por lo que seguiria siendo un coladero
		List<Usuario> rs = 
			s.createQuery("select u from Usuario u where u.login='"+login+"' and pw='"+pw+"'").list();
		
		//deberemos de usar algo as�, que seria el equivalente a prepared statement en hibernate
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












