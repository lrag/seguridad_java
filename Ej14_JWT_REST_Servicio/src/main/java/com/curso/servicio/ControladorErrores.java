package com.curso.servicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
//@ControllerAdvice(basePackages = "com.curso")
public class ControladorErrores {

	public ControladorErrores() {
		super();
	}

	@ExceptionHandler(value={ Throwable.class })
	public ResponseEntity<Object> procesarError(Exception ex, WebRequest request){
		
		System.out.println("CONTROLADOR ERRORES");
		ResponseEntity<Object> re = new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return re;
		
	}
	
}
