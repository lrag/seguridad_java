package expedientesx.modelo.negocio;

import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import expedientesx.modelo.entidad.Expediente;

public interface ServicioExpendientes {
	
	@PreAuthorize("hasRole('ROLE_DIRECTOR')")
	void clasificar(Expediente expediente);

	@PreAuthorize("hasRole('ROLE_DIRECTOR') "+
			      "or #expediente.investigador == authentication.name")
	void desclasificar(Expediente expediente);

	@PreAuthorize("hasAnyRole('ROLE_AGENTE_ESPECIAL','ROLE_DIRECTOR')")	
	@PostAuthorize("hasRole('ROLE_DIRECTOR') "
			+ "or returnObject.investigador == authentication.name")
	Expediente buscar(Long id);
	
	@PostFilter("hasRole('ROLE_DIRECTOR') or filterObject.investigador==principal.username")
	List<Expediente> listarTodos();
}




