package expedientesx.controlador;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class OyenteSesion implements HttpSessionListener, HttpSessionAttributeListener {

    public OyenteSesion() {
    }

    public void sessionCreated(HttpSessionEvent arg0)  { 
    	System.out.println("SESIÓN CREADA");
    }

    public void sessionDestroyed(HttpSessionEvent ev)  { 
    	System.out.println("SESIÓN INVALIDADA");
    }

	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		System.out.println("Atributo añadido a la sesión ("+ event.getSession().getId() +"): "+ event.getName());
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
		System.out.println("Atributo reemplazado a la sesión ("+ event.getSession().getId() +"): "+ event.getName());
		// TODO Auto-generated method stub
		
	}
	
}