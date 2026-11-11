
from pathlib import Path
from html.parser import HTMLParser

# Rutas de los archivos
CARPETA = Path(__file__).resolve().parent
ARCHIVO_ORIGEN = CARPETA / "CodigosPostales.xls"
ARCHIVO_SALIDA = CARPETA / "sepomex_saltillo_local.sql"


class LectorSEPOMEX(HTMLParser):

    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.en_tabla = False
        self.en_fila = False
        self.en_celda = False
        self.filas = []
        self.fila = []
        self.contenido = []

    def handle_starttag(self, etiqueta, atributos):
        if etiqueta == "table":
            datos = dict(atributos)
            if datos.get("id") == "Datagrid1":
                self.en_tabla = True

        elif etiqueta == "tr" and self.en_tabla:
            self.en_fila = True
            self.fila = []

        elif etiqueta == "td" and self.en_fila:
            self.en_celda = True
            self.contenido = []

    def handle_data(self, texto):
        if self.en_celda:
            self.contenido.append(texto)

    def handle_endtag(self, etiqueta):
        if etiqueta == "td" and self.en_celda:
            valor = " ".join(
                "".join(self.contenido).split()
            )
            self.fila.append(valor)
            self.en_celda = False

        elif etiqueta == "tr" and self.en_fila:
            self.filas.append(self.fila)
            self.en_fila = False

        elif etiqueta == "table" and self.en_tabla:
            self.en_tabla = False


def escapar_sql(valor):
    return valor.replace("\\", "\\\\").replace("'", "''")


def generar_catalogo():

    print("======================================")
    print(" CATALOGO SEPOMEX - SALTILLO")
    print("======================================")

    if not ARCHIVO_ORIGEN.exists():
        print("[ERROR] No se encontro CodigosPostales.xls")
        return

    # Leer el archivo descargado
    contenido = ARCHIVO_ORIGEN.read_text(
        encoding="latin-1"
    )

    lector = LectorSEPOMEX()
    lector.feed(contenido)

    if not lector.filas:
        print("[ERROR] No se encontraron registros.")
        return

    registros = []
    duplicados = 0
    registros_invalidos = 0
    encontrados = set()

    # Omitir encabezados
    for fila in lector.filas[1:]:

        if len(fila) != 7:
            registros_invalidos += 1
            continue

        codigo_postal = fila[0]
        estado = fila[1]
        municipio = fila[2]
        tipo = fila[4]
        nombre = fila[5]

        # Filtrar exclusivamente Saltillo
        if municipio.casefold() != "saltillo":
            continue

        # Validar campos
        if (
            not codigo_postal.isdigit()
            or len(codigo_postal) != 5
            or not nombre
            or not tipo
            or len(nombre) > 100
            or len(tipo) > 100
            or len(municipio) > 100
            or len(estado) > 100
        ):
            registros_invalidos += 1
            continue

        clave = (
            codigo_postal,
            nombre.casefold(),
            tipo.casefold()
        )

        if clave in encontrados:
            duplicados += 1
            continue

        encontrados.add(clave)

        registros.append((
            codigo_postal,
            nombre,
            tipo,
            municipio,
            estado
        ))

    # Evitar generar un catalogo incompleto
    if registros_invalidos:
        print("[ERROR] Hay registros invalidos.")
        print("Cantidad:", registros_invalidos)
        return

    if not registros:
        print("[ERROR] No hay registros de Saltillo.")
        return

    instrucciones = [
        "-- Catalogo SEPOMEX Saltillo",
        "-- Generado desde la descarga local.",
        "-- Uso local. No redistribuir sin autorizacion.",
        "-- Ejecutar despues de agregar TIPO_ASENTAMIENTO.",
        "",
        "START TRANSACTION;",
        ""
    ]

    for cp, nombre, tipo, municipio, estado in registros:

        cp = escapar_sql(cp)
        nombre = escapar_sql(nombre)
        tipo = escapar_sql(tipo)
        municipio = escapar_sql(municipio)
        estado = escapar_sql(estado)

        instrucciones.append(
            "INSERT INTO colonias "
            "(NOMBRE, CODIGO_POSTAL, MUNICIPIO, "
            "ESTADO, TIPO_ASENTAMIENTO) "
            f"SELECT '{nombre}', '{cp}', "
            f"'{municipio}', '{estado}', '{tipo}' "
            "WHERE NOT EXISTS ("
            "SELECT 1 FROM colonias "
            f"WHERE NOMBRE = '{nombre}' "
            f"AND CODIGO_POSTAL = '{cp}' "
            f"AND MUNICIPIO = '{municipio}'"
            f"AND TIPO_ASENTAMIENTO = '{tipo}'"
            ");"
        )

    instrucciones.extend([
        "",
        "COMMIT;",
        ""
    ])

    ARCHIVO_SALIDA.write_text(
        "\n".join(instrucciones),
        encoding="utf-8"
    )

    tipos = set(registro[2] for registro in registros)

    print("[EXITO] Catalogo generado correctamente.")
    print("Registros de Saltillo:", len(registros))
    print("Tipos de asentamiento:", len(tipos))
    print("Duplicados omitidos:", duplicados)
    print("Registros invalidos:", registros_invalidos)
    print("Archivo:", ARCHIVO_SALIDA.name)


if __name__ == "__main__":
    generar_catalogo()
