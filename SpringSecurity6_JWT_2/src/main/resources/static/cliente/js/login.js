$(function () {

	// Si ya hay un token en esta pestaña, no hace falta volver a loguearse
	if (obtenerToken()) {
		window.location.href = 'listado.html';
		return;
	}

	$('#formLogin').on('submit', function (e) {
		e.preventDefault();
		$('#mensajeError').addClass('d-none').text('');

		$.ajax({
			url: '/controlAutenticacion',
			method: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				username: $('#username').val(),
				password: $('#password').val()
			})
		}).done(function (token) {
			guardarToken(token);
			window.location.href = 'listado.html';
		}).fail(function (xhr) {
			var mensaje = xhr.responseText || 'No se ha podido iniciar sesión';
			$('#mensajeError').removeClass('d-none').text(mensaje);
		});
	});

});
