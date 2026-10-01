// Utilidades de sesión compartidas por login.html y listado.html.
//
// OJO: a diferencia de JWT, Basic Auth no emite un token que el servidor pueda
// luego revocar/expirar. El navegador reenvía usuario+contraseña en CADA
// petición, así que si queremos hacer llamadas AJAX en varias pantallas sin
// pedirlos cada vez, no nos queda otra que guardar usuario Y CONTRASEÑA EN
// CLARO en sessionStorage para poder recalcular la cabecera Authorization.
// Esto es precisamente la limitación que se quiere mostrar con este ejemplo:
// cualquier script (o extensión del navegador, o un XSS) con acceso a
// sessionStorage puede leer la contraseña tal cual.

function obtenerCredenciales() {
	var username = sessionStorage.getItem('authUsername');
	var password = sessionStorage.getItem('authPassword');
	if (!username || !password) {
		return null;
	}
	return { username: username, password: password };
}

function guardarCredenciales(username, password) {
	sessionStorage.setItem('authUsername', username);
	sessionStorage.setItem('authPassword', password);
}

function cerrarSesion() {
	sessionStorage.removeItem('authUsername');
	sessionStorage.removeItem('authPassword');
	window.location.href = 'index.html';
}

function exigirSesion() {
	if (!obtenerCredenciales()) {
		window.location.href = 'index.html';
	}
}

function cabeceraAutenticacion() {
	var credenciales = obtenerCredenciales();
	if (!credenciales) {
		return {};
	}
	// btoa trabaja con Latin1; para usuario/contraseña ASCII (los de este
	// ejemplo) es suficiente.
	var token = btoa(credenciales.username + ':' + credenciales.password);
	return { 'Authorization': 'Basic ' + token };
}
