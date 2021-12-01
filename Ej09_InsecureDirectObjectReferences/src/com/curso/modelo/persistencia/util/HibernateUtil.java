package com.curso.modelo.persistencia.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.curso.modelo.entidad.Cliente;

public class HibernateUtil {

	private static SessionFactory sf;
	
	public static SessionFactory getSF(){
		
		if(sf == null){
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
			sources.addAnnotatedClass(Cliente.class);
			sf = sources.getMetadataBuilder().build().buildSessionFactory();
		}
		
		return sf;		
	}	
}

