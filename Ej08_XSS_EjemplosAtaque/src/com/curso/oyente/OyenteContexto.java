package com.curso.oyente;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionCookieConfig;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class OyenteContexto implements ServletContextListener {

    public OyenteContexto() {
    }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		System.out.println("====================================================");
		System.out.println("Contexto inicializado.");
		//ESTO ES A PARTIR DE LA 3.0!!!
		
		SessionCookieConfig scf = sce.getServletContext().getSessionCookieConfig();

		//Más opciones:		
		scf.setHttpOnly(false); //No podrá ser accedida desde JS con true
		//scf.setSecure(true);		

		System.out.println("Is http only:"+scf.isHttpOnly());
		System.out.println("Is secure   :"+scf.isSecure());
	
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
