#!/usr/bin/env bash
#
# Regenera toda la cadena de certificados usada por este proyecto (CA raiz -> CA
# intermedia -> certificado de servidor + certificados de cliente), siguiendo
# exactamente los pasos documentados en src/main/resources/application.yml.
#
# Passwords fijos (igual que en application.yml): "antunez" para la CA raiz,
# "changeme" para los .p12, "cliente1"/"cliente2" para las claves de esos clientes.
# cliente3 es un certificado AUTOFIRMADO (no pasa por la CA intermedia) a
# proposito, para que se pueda ver como lo rechaza el truststore del servidor.
#
# Si algun fichero de salida ya existe, el script se queja y no toca nada.

set -euo pipefail

# En Git Bash (MSYS), cualquier argumento que empiece por "/" (como nuestros -subj
# "/C=ES/ST=...") se interpreta como ruta Unix y se reescribe a una ruta de Windows
# antes de pasarselo a openssl.exe. Esto lo desactiva para todo el script.
export MSYS_NO_PATHCONV=1

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RES_DIR="$SCRIPT_DIR/src/main/resources"
ROOT_DIR="$SCRIPT_DIR"

PASS_ROOT="antunez"
PASS_P12="changeme"

FICHEROS_RES=(
	AntunezRootCA.key AntunezRootCA.crt AntunezRootCA.srl
	AntunezIntermediateCA_extensions.txt
	AntunezIntermediateCA.key AntunezIntermediateCA.csr AntunezIntermediateCA.crt AntunezIntermediateCA.srl
	servidor_localhost_extensions.txt
	servidor_localhost.key servidor_localhost.csr servidor_localhost.crt servidor_localhost.p12
	truststore.p12
	cliente_extensions.txt
	cliente1.key cliente1.csr cliente1.crt cliente1.p12
	cliente2.key cliente2.csr cliente2.crt cliente2.p12
	cliente3.key cliente3.crt cliente3.p12
)
FICHEROS_ROOT=(cliente1.p12 cliente2.p12 cliente3.p12)

echo "Comprobando que no exista ya nada en $RES_DIR ..."
encontrados=()
for f in "${FICHEROS_RES[@]}"; do
	[ -e "$RES_DIR/$f" ] && encontrados+=("$RES_DIR/$f")
done
for f in "${FICHEROS_ROOT[@]}"; do
	[ -e "$ROOT_DIR/$f" ] && encontrados+=("$ROOT_DIR/$f")
done

if [ ${#encontrados[@]} -gt 0 ]; then
	echo "ERROR: ya existen estos ficheros, no se genera nada:" >&2
	printf '  %s\n' "${encontrados[@]}" >&2
	echo "Borralos o muevelos (haz copia de seguridad antes) y vuelve a lanzar el script." >&2
	exit 1
fi

mkdir -p "$RES_DIR"
cd "$RES_DIR"

echo "== 1/6: CA raiz (AntunezRootCA) =="
openssl req -x509 -sha256 -days 36500 -newkey rsa:4096 \
	-keyout AntunezRootCA.key -out AntunezRootCA.crt \
	-passout pass:"$PASS_ROOT" \
	-subj "/C=ES/ST=Salamanca/O=Internet Widgits Pty Ltd/CN=Antunez Root CA"

echo "== 2/6: CA intermedia (AntunezIntermediateCA) =="
cat > AntunezIntermediateCA_extensions.txt <<'EOF'
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
basicConstraints = critical, CA:true, pathlen:0
keyUsage = critical, digitalSignature, cRLSign, keyCertSign
EOF

openssl genrsa -out AntunezIntermediateCA.key 4096

openssl req -new -key AntunezIntermediateCA.key -out AntunezIntermediateCA.csr \
	-subj "/C=ES/ST=Salamanca/O=Internet Widgits Pty Ltd/CN=Antunez Intermediate CA"

openssl x509 -req -in AntunezIntermediateCA.csr \
	-CA AntunezRootCA.crt -CAkey AntunezRootCA.key -CAcreateserial \
	-out AntunezIntermediateCA.crt -days 3650 -sha256 \
	-extfile AntunezIntermediateCA_extensions.txt \
	-passin pass:"$PASS_ROOT"

echo "== 3/6: Certificado de servidor (servidor_localhost) =="
cat > servidor_localhost_extensions.txt <<'EOF'
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = critical, digitalSignature, keyEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
EOF

openssl genrsa -out servidor_localhost.key 2048

openssl req -new -key servidor_localhost.key -out servidor_localhost.csr \
	-subj "/CN=localhost/O=Api de peliculas/C=ES"

openssl x509 -req -in servidor_localhost.csr \
	-CA AntunezIntermediateCA.crt -CAkey AntunezIntermediateCA.key -CAcreateserial \
	-out servidor_localhost.crt -days 36500 -sha256 \
	-extfile servidor_localhost_extensions.txt

openssl pkcs12 -export -in servidor_localhost.crt -inkey servidor_localhost.key \
	-out servidor_localhost.p12 -name tomcat \
	-CAfile AntunezIntermediateCA.crt -caname root \
	-passout pass:"$PASS_P12"

echo "== 4/6: Truststore del servidor =="
keytool -importcert -noprompt -trustcacerts \
	-file AntunezIntermediateCA.crt -alias intermediaca \
	-keystore truststore.p12 -storetype PKCS12 -storepass "$PASS_P12"

echo "== 5/6: Certificados de cliente firmados por la CA intermedia (cliente1, cliente2) =="
cat > cliente_extensions.txt <<'EOF'
basicConstraints = CA:FALSE
nsCertType = client
keyUsage = critical, digitalSignature, keyEncipherment
extendedKeyUsage = clientAuth
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid,issuer
EOF

generar_cliente_firmado() {
	local prefix="$1" cn="$2" keypass="$3"

	openssl req -new -newkey rsa:2048 -keyout "$prefix.key" -out "$prefix.csr" \
		-passout pass:"$keypass" \
		-subj "/CN=$cn"

	openssl x509 -req -in "$prefix.csr" \
		-CA AntunezIntermediateCA.crt -CAkey AntunezIntermediateCA.key -CAcreateserial \
		-out "$prefix.crt" -days 36500 -sha256 \
		-extfile cliente_extensions.txt \
		-passin pass:"$keypass"

	openssl pkcs12 -export -in "$prefix.crt" -inkey "$prefix.key" \
		-passin pass:"$keypass" \
		-out "$prefix.p12" -name "$cn" \
		-passout pass:"$PASS_P12"
}

generar_cliente_firmado cliente1 "Harry Callahan" cliente1
generar_cliente_firmado cliente2 "Bud Spencer" cliente2

echo "== 6/6: Certificado de cliente AUTOFIRMADO, no confiable (cliente3 / Harpo) =="
openssl req -x509 -newkey rsa:2048 -keyout cliente3.key -out cliente3.crt \
	-days 36500 -sha256 -nodes \
	-subj "/CN=Harpo"

openssl pkcs12 -export -in cliente3.crt -inkey cliente3.key \
	-out cliente3.p12 -name Harpo \
	-passout pass:"$PASS_P12"

echo "Copiando los .p12 de cliente a la raiz del proyecto (de ahi los lee AplicacionCliente) ..."
cp cliente1.p12 cliente2.p12 cliente3.p12 "$ROOT_DIR/"

echo "Listo. Todo generado en $RES_DIR (y los .p12 de cliente tambien en $ROOT_DIR)."
