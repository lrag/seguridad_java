package com.curso;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import com.curso.modelo.entidad.Usuario;

@ServletComponentScan
@SpringBootApplication
public class Aplicacion implements CommandLineRunner{

	@Autowired
	private SessionFactory sessionFactory;
	
	public static void main(String[] args) {
		SpringApplication.run(Aplicacion.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		Session s = sessionFactory.openSession();
		
		if(s.createQuery("from Usuario", Usuario.class).list().size()==0) {
			System.out.println("==CARGANDO DATOS=======================");
			s.beginTransaction();
			s.persist(new Usuario(null,"Bud Spencer","a","a","USR"));
			s.persist(new Usuario(null,"Harry Callahan","b","b","ADMIN"));
			s.persist(new Usuario(null,"Antunez","c","c","EMPLEADO"));		
			s.getTransaction().commit();
		}
		
		s.close();		
		
	}

}
