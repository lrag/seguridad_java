package expedientesx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

//Esta anotación indica a spring boot que en el proyecto hay clases con las anotaciones:
//@WebServlet
//@WebFilter
//@WebListener
@ServletComponentScan
@SpringBootApplication
public class FBI {
	
	public static void main(String[] args) {
		SpringApplication.run(FBI.class, args);
	}

}
