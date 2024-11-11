package com.curso.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.curso.modelo.entidad.Noticia;

@WebServlet("/SVNoticias")
public class SVNoticias extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//Simplificando
	List<Noticia> noticias = new ArrayList<Noticia>();
	
	public void init(){
		noticias.add(new Noticia("Sube la venta de pitos y flautas","Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas cursus lacinia sagittis. Nullam mattis sem eget mauris congue, sit amet venenatis nibh porta. Nullam a leo at lorem ullamcorper tempor vel et nisl. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam pharetra ex iaculis tellus sollicitudin, eu sollicitudin libero sollicitudin. "));
		noticias.add(new Noticia("Hombre muerde a perro","Suspendisse rutrum sollicitudin magna, a finibus enim tincidunt at. Donec finibus dui sed metus viverra, vitae semper felis ornare. Donec aliquet vel metus nec aliquam. Ut magna libero, pellentesque nec nibh a, vulputate aliquam elit. Nunc et rhoncus urna, et pretium quam. Mauris lobortis libero et urna tincidunt tempus ut sit amet tortor. Quisque aliquet erat vitae consectetur venenatis. Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
		noticias.add(new Noticia("Inyectan por error HTML en lugar de vacuna","Etiam molestie varius consectetur. Curabitur maximus ac diam non rutrum. Praesent ac urna eget augue imperdiet eleifend nec et quam. Curabitur porta orci eu porta feugiat. Phasellus vehicula nunc lectus, non maximus nisl tempor a. Duis id consectetur nisi, vitae volutpat libero. Mauris a metus nulla. Praesent fringilla fermentum erat, quis pretium odio efficitur sit amet. "));
		noticias.add(new Noticia("Encontrada cura dislexia para","Vivamus tortor ante, interdum sed nunc nec, tincidunt rhoncus sapien. Fusce ac est urna. In in nulla elementum, scelerisque ligula sed, consectetur metus. Mauris quis sapien vel enim molestie facilisis eget sit amet leo. Aenean sit amet nisl eu dolor tempor consequat sed eu eros. Integer ultrices sodales justo quis tristique. Nam malesuada venenatis ullamcorper. Maecenas convallis scelerisque turpis vitae consectetur."));
		noticias.add(new Noticia("Siete caballos vienen de Bonanza","Integer justo ligula, ultricies a laoreet a, dictum ut dui. Suspendisse quis eleifend lacus. Aliquam lacus ante, convallis quis lacus in, malesuada pulvinar leo. Duis placerat dolor metus, nec malesuada tortor mattis eget. Donec sed augue ut neque dignissim lobortis id nec nibh. Praesent nulla est, vehicula quis mi vitae, vulputate aliquam libero. Maecenas nisl orci, viverra quis molestie aliquet, mollis id ipsum."));
	}
	
    public SVNoticias() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("noticias", noticias);
		request.getRequestDispatcher("noticias.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Noticia n = new Noticia(request.getParameter("titulo"), request.getParameter("texto"));
		noticias.add(n);
		
		response.sendRedirect("SVNoticias");
	}

}

