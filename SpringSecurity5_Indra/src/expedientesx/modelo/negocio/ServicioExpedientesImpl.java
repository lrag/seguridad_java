package expedientesx.modelo.negocio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import expedientesx.modelo.entidad.Expediente;
import expedientesx.modelo.persistencia.ExpedientesDao;

@Service
public class ServicioExpedientesImpl implements ServicioExpendientes {
	
	@Autowired
	private ExpedientesDao expedientesDao;

	public void actualizar(Expediente expediente) {
		expedientesDao.guardar(expediente);
		System.out.println("Actualizado Expediente: " + expediente);
	}

	public void clasificar(Expediente expediente) {
		if (!expediente.isClasificado()) {
			expediente.setClasificado(true);
			actualizar(expediente);
			System.out.println("Expediente Clasificado: " + expediente);
		}
	}

	public void desclasificar(Expediente expediente) {
		
		//if(rol == DIRECTOR or rol == AGENTE_ESPECIAL AND expediente.investigador == usuario.login)
		
		/*
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) auth.getAuthorities();
		if(!(authorities.contains(new SimpleGrantedAuthority("ROLE_DIRECTOR")) 
		  || (authorities.contains(new SimpleGrantedAuthority("ROLE_AGENTE_ESPECIAL")) && expediente.getInvestigador().equals(auth.getName()))) )		
		{
			throw new RuntimeException("No tienes permisos para desclasificar este expediente");
		}
		*/
		
		if (expediente.isClasificado()) {
			expediente.setClasificado(false);
			actualizar(expediente);
			System.out.println("Expediente Desclasificado: " + expediente);
		}
	}

	public List<Expediente> listarTodos() {
		
		//
		
		List<Expediente>expedientes=expedientesDao.listar();
		System.out.println("Mostrar "+expedientes.size()+" Expedientes: " + expedientes.toString());
		return expedientes;
	}

	public Expediente buscar(Long id) {
		Expediente expediente=expedientesDao.buscar(id);
		System.out.println("Mostrar Expediente: " + expediente.toString());
		return expediente;
	}
	
	/*
	@Autowired
	private ServicioExpendientes proxy;
	
	@PreAuthorize("hasRole('USR')")
	public void metodo1() {
		//this.metodo2();
		proxy.metodo2();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public void metodo2() {
		//
	}
	*/
	
	
	

}
