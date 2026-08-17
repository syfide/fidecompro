# Fidecompro

Sistema de facturación con arquitectura **cliente-servidor**, desarrollado en Java desde cero (sin frameworks ni librerías externas).

- **Redes:** el cliente (interfaz Swing) y el servidor se comunican por **sockets TCP**, intercambiando objetos serializados (`Solicitud`/`Respuesta`). El servidor es multihilo: atiende cada conexión de cliente en su propio hilo.
- **Base de datos:** motor propio de persistencia por tablas (`fidecompro.db.GestorBaseDatos`). Cada entidad (usuarios, clientes, productos, facturas) se guarda en su propio archivo de texto delimitado (`data/*.tabla`), con un índice en memoria (HashMap) por clave primaria — el mismo principio que usa cualquier motor de base de datos simple, pero implementado sin JDBC ni dependencias externas.

## Arquitectura

```
Cliente (Swing GUI)  <──── Sockets TCP ────>  Servidor (multihilo)  <────>  Base de datos propia (archivos "tabla")
   AlmacenRemoto                                ServidorFidecompro          GestorBaseDatos
```

## Estructura del proyecto

```
Fidecompro/
├── src/fidecompro/
│   ├── Main.java               # Punto de entrada: arranca servidor o cliente
│   ├── db/
│   │   └── GestorBaseDatos.java   # Motor de base de datos propio (tablas en archivo)
│   ├── red/
│   │   ├── Protocolo.java         # Mensajes Solicitud/Respuesta del protocolo
│   │   ├── ServidorFidecompro.java # Servidor TCP multihilo
│   │   └── AlmacenRemoto.java      # Cliente de red usado por la GUI
│   ├── excepciones/             # Excepciones personalizadas
│   ├── gui/                     # Paneles e interfaz (Login, Dashboard, Clientes, Productos, Facturas)
│   ├── modelo/                  # Clases del modelo (Cliente, Producto, Factura, Usuario, etc.)
│   └── util/
│       └── FacturaExporter.java # Exportación de facturas a texto
├── build.sh                     # Script de compilación y ejecución
└── nbproject/                   # Configuración del proyecto NetBeans
```

## Cómo compilar y ejecutar

Requiere JDK instalado.

**1. Iniciar el servidor** (déjalo corriendo en una terminal):
```bash
./build.sh servidor
```
Esto crea la carpeta `data/` con las "tablas" de la base de datos y queda escuchando en el puerto `5050`.

**2. Iniciar el cliente** (en otra terminal, o en otra máquina de la misma red):
```bash
./build.sh cliente
```
Por defecto se conecta a `localhost:5050`. Para conectarse a un servidor en otra máquina:
```bash
./build.sh cliente 192.168.1.50 5050
```

**Usuario por defecto:** `admin` / `admin123`

## Manual (sin build.sh)

```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out -sourcepath src @sources.txt

# Terminal 1 — servidor
java -cp out fidecompro.Main servidor 5050

# Terminal 2 — cliente
java -cp out fidecompro.Main localhost 5050
```

## Abrir en NetBeans

1. Abrir NetBeans IDE.
2. Archivo → Abrir Proyecto.
3. Seleccionar la carpeta `Fidecompro`.
4. Para probar: ejecutar primero `ServidorFidecompro.java` (botón derecho → Run File) y luego `Main.java`.
