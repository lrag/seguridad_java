$(function () {

	exigirSesion();

	function escapeHtml(texto) {
		return $('<div>').text(texto == null ? '' : texto).html();
	}

	function mostrarMensaje(texto, tipo) {
		$('#mensaje')
			.removeClass('d-none alert-danger alert-success')
			.addClass('alert-' + (tipo || 'danger'))
			.text(texto);
	}

	function ocultarMensaje() {
		$('#mensaje').addClass('d-none');
	}

	function limpiarFormulario() {
		$('#id').val('');
		$('#titulo').val('');
		$('#director').val('');
		$('#genero').val('');
		$('#year').val('');
		$('#tituloFormulario').text('Nueva película');
	}

	function manejarError(xhr) {
		if (xhr.status === 401) {
			// Credenciales ausentes o incorrectas: Spring Security ha rechazado el Basic Auth
			cerrarSesion();
			return;
		}
		mostrarMensaje(xhr.responseText || ('Error ' + xhr.status), 'danger');
	}

	function cargarPeliculas() {
		ocultarMensaje();
		$.ajax({
			url: '/peliculas',
			method: 'GET',
			headers: cabeceraAutenticacion()
		}).done(function (peliculas) {
			var filas = peliculas.map(function (p) {
				return '<tr data-id="' + p.id + '">' +
					'<td>' + p.id + '</td>' +
					'<td>' + escapeHtml(p.titulo) + '</td>' +
					'<td>' + escapeHtml(p.director) + '</td>' +
					'<td>' + escapeHtml(p.genero) + '</td>' +
					'<td>' + (p.year != null ? p.year : '') + '</td>' +
					'<td class="text-end">' +
						'<button type="button" class="btn btn-sm btn-outline-primary btnEditar">Editar</button> ' +
						'<button type="button" class="btn btn-sm btn-outline-danger btnBorrar">Borrar</button>' +
					'</td>' +
				'</tr>';
			}).join('');
			$('#tablaPeliculas').html(filas);
		}).fail(manejarError);
	}

	$('#btnNueva').on('click', limpiarFormulario);
	$('#btnRecargar').on('click', cargarPeliculas);
	$('#btnCancelar').on('click', limpiarFormulario);
	$('#btnLogout').on('click', cerrarSesion);

	$('#formPelicula').on('submit', function (e) {
		e.preventDefault();
		ocultarMensaje();

		var id = $('#id').val();
		var esNueva = !id;
		var pelicula = {
			titulo: $('#titulo').val(),
			director: $('#director').val(),
			genero: $('#genero').val(),
			year: $('#year').val() ? parseInt($('#year').val(), 10) : null
		};

		$.ajax({
			url: esNueva ? '/peliculas' : '/peliculas/' + id,
			method: esNueva ? 'POST' : 'PUT',
			contentType: 'application/json',
			headers: cabeceraAutenticacion(),
			data: JSON.stringify(pelicula)
		}).done(function () {
			mostrarMensaje(esNueva ? 'Película insertada' : 'Película modificada', 'success');
			limpiarFormulario();
			cargarPeliculas();
		}).fail(manejarError);
	});

	$('#tablaPeliculas').on('click', '.btnEditar', function () {
		var fila = $(this).closest('tr');
		$('#id').val(fila.data('id'));
		$('#titulo').val(fila.find('td').eq(1).text());
		$('#director').val(fila.find('td').eq(2).text());
		$('#genero').val(fila.find('td').eq(3).text());
		$('#year').val(fila.find('td').eq(4).text());
		$('#tituloFormulario').text('Editar película #' + fila.data('id'));
	});

	$('#tablaPeliculas').on('click', '.btnBorrar', function () {
		var fila = $(this).closest('tr');
		var id = fila.data('id');
		if (!confirm('¿Borrar la película "' + fila.find('td').eq(1).text() + '"?')) {
			return;
		}
		$.ajax({
			url: '/peliculas/' + id,
			method: 'DELETE',
			headers: cabeceraAutenticacion()
		}).done(function () {
			mostrarMensaje('Película borrada', 'success');
			cargarPeliculas();
		}).fail(manejarError);
	});

	cargarPeliculas();

});
