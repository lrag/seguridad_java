$(function () {

	function mostrarEstado(texto, tipo) {
		$('#estado').removeClass('text-muted text-success text-danger').addClass('text-' + tipo).text(texto);
	}

	var codigo = new URLSearchParams(window.location.search).get('codigo');

	if (!codigo) {
		mostrarEstado('Falta el codigo de acceso en la URL.', 'danger');
		return;
	}

	$.ajax({
		url: '/magic-link/token',
		method: 'POST',
		data: { codigo: codigo }
	}).done(function (respuesta) {
		sessionStorage.setItem('jwt', respuesta.token);

		//quitamos el codigo de la barra de direcciones, ya no sirve para nada (de un solo uso)
		history.replaceState(null, '', window.location.pathname);

		mostrarEstado('Autenticado correctamente. Token guardado en sessionStorage.', 'success');
	}).fail(function () {
		mostrarEstado('El enlace ha caducado o no es valido. Vuelve a pedir uno.', 'danger');
	});

});
