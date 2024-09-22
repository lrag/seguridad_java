import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.curso.modelo.entidad.Usuario;

public class CargaDatos {

	public static void main(String[] args) {
		
		Map<String, String> settings = new HashMap<>();
	        settings.put(Environment.DRIVER, "org.h2.Driver");
	        settings.put(Environment.URL, "jdbc:h2:C:/H2/bbdd_seguridad");
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
		SessionFactory sf = sources.getMetadataBuilder().build().buildSessionFactory();
		
		Session s = sf.getCurrentSession();
		s.beginTransaction();
		s.persist(new Usuario(null,"Bud Spencer","a","a","USR"));
		s.persist(new Usuario(null,"Harry Callahan","b","b","ADMIN"));
		s.persist(new Usuario(null,"Antunez","c","c","EMPLEADO"));
		
		s.getTransaction().commit();	
		
		s.close();
		sf.close();
		
	}
	
}
