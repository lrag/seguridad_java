package com.curso.modelo.persistencia;

import java.time.LocalDateTime;

import com.curso.modelo.entidad.TokenMagicLink;

public interface TokenMagicLinkDao {

	TokenMagicLink guardar(TokenMagicLink token);

	TokenMagicLink buscarPorToken(String token);

	//Marca el token como utilizado solo si todavia no lo estaba. Devuelve true si esta
	//llamada es la que lo ha consumido (false si ya estaba usado, o si no existe) -evita
	//que dos peticiones casi simultaneas con el mismo token cuelen las dos-.
	boolean marcarComoUtilizado(String token);

	//Limpieza de tokens caducados que nadie llego a usar
	void borrarCaducados(LocalDateTime ahora);

}
