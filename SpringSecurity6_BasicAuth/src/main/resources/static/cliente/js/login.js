$(function () {

	// Si ya hay credenciales guardadas en esta pestaña, no hace falta volver a loguearse
	if (obtenerCredenciales()) {
		window.location.href = 'listado.html';
		return;
	}

	$('#formLogin').on('submit', function (e) {
		e.preventDefault();
		$('#mensajeError').addClass('d-none').text('');

		var username = $('#username').val();
		var password = $('#password').val();

		// Basic Auth no tiene endpoint de "login": no hay nada que te devuelva
		// el servidor y guardar. Lo unico que podemos hacer es probar las
		// credenciales directamente contra un recurso protegido.
		$.ajax({
			url: '/peliculas',
			method: 'GET',
			headers: { 'Authorization': 'Basic ' + btoa(username + ':' + password) }
		}).done(function () {
			// Si no fallan, son validas: las guardamos para poder recalcular
			// la cabecera Authorization en cada peticion de listado.html
			guardarCredenciales(username, password);
			window.location.href = 'listado.html';
		}).fail(function (xhr) {
			var mensaje = xhr.status === 401
				? 'Usuario o contraseña incorrectos.'
				: (xhr.responseText || 'No se ha podido iniciar sesión');
			$('#mensajeError').removeClass('d-none').text(mensaje);
		});
	});

});
