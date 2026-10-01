// Utilidades de sesión compartidas por login.html y listado.html
// El JWT se guarda en sessionStorage: solo vive mientras dura la pestaña.

function obtenerToken() {
	return sessionStorage.getItem('jwt');
}

function guardarToken(token) {
	sessionStorage.setItem('jwt', token);
}

function cerrarSesion() {
	sessionStorage.removeItem('jwt');
	window.location.href = 'index.html';
}

function exigirSesion() {
	if (!obtenerToken()) {
		window.location.href = 'index.html';
	}
}

function cabeceraAutenticacion() {
	return { 'Authorization': 'Bearer ' + obtenerToken() };
}
