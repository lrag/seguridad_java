package expedientesx.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import expedientesx.modelo.entidad.Expediente;
import expedientesx.modelo.negocio.ServicioExpendientes;

@Controller
public class ControladorExpedientes {
	
	//@Autowired 
	private ServicioExpendientes servicioExpendientes;
	
	public ControladorExpedientes(ServicioExpendientes servicioExpendientes) {
		super();
		this.servicioExpendientes = servicioExpendientes;
	}

	//@RequestMapping("/mostrar/todos")
	@GetMapping("/mostrar/todos")
	public ModelAndView mostrarTodos() {
		System.out.println("Peticion Mostrar Expedientes Recibida");
		ModelAndView mav=new ModelAndView();
		mav.setViewName("listadoExpedientes");
		mav.addObject("expedientes", servicioExpendientes.listarTodos());
		return mav;
	}
	
	@GetMapping("/mostrar/{id}")
	public ModelAndView mostrar(@PathVariable() Long id) {
		System.out.println("Peticion Mostrar Expediente Recibida");
		ModelAndView mav=new ModelAndView();
		mav.setViewName("detalleExpediente");
		mav.addObject("expediente", servicioExpendientes.buscar(id));
		return mav;
	}
	
	//POST /clasificar
	//CT: form url encoded...
	//-----------------------
	//id=123
	@PostMapping(value="/clasificar")
	public String clasificar( @RequestParam("id") Long id, Model model){
		System.out.println("Clasificar el expediente: "+id);
		Expediente expediente = servicioExpendientes.buscar(id);
		servicioExpendientes.clasificar(expediente);
		return "redirect:/expedientesx/mostrar/todos";
	}
	
	//POST /desclasificar
	//CT: form url encoded...
	//-----------------------
	//id=123
	@PostMapping(value="/desclasificar")
	public String desclasificar( @RequestParam("id") Long id, Model model){
		System.out.println("Desclasificar el expediente: "+id);
		Expediente expediente = servicioExpendientes.buscar(id);
		servicioExpendientes.desclasificar(expediente);
		return "redirect:/expedientesx/mostrar/todos";
	}

}
