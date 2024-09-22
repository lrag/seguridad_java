package com.curso.xss;

//Solo tiene sentido pasar saneamiento a los atributos
//que sean de tipo String
public class XSSException extends Exception {

	private static final long serialVersionUID = 1L;

	public XSSException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
