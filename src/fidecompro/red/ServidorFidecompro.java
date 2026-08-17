package fidecompro.red;

import fidecompro.db.GestorBaseDatos;
import fidecompro.excepciones.RegistroDuplicadoException;
import fidecompro.excepciones.StockInsuficienteException;
import fidecompro.modelo.*;
import fidecompro.red.Protocolo.Operacion;
import fidecompro.red.Protocolo.Respuesta;
import fidecompro.red.Protocolo.Solicitud;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor TCP de Fidecompro.
 * Demuestra: Redes (sockets), Multihilos (un hilo por cliente conectado)
 *
 * Escucha conexiones en el puerto indicado y, por cada cliente que se
 * conecta, lanza un hilo dedicado (ManejadorCliente) que atiende sus
 * solicitudes contra la base de datos compartida (GestorBaseDatos),
 * la cual es thread-safe.
 */
public class ServidorFidecompro {

    public static final int PUERTO_DEFECTO = 5050;

    private final int puerto;
    private final GestorBaseDatos bd;
    private volatile boolean activo = false;

    public ServidorFidecompro(int puerto) {
        this.puerto = puerto;
        this.bd = GestorBaseDatos.getInstance();
    }

    public void iniciar() {
        activo = true;
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("=======================================================");
            System.out.println(" Servidor Fidecompro escuchando en el puerto " + puerto);
            System.out.println("=======================================================");
            while (activo) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress());
                new Thread(new ManejadorCliente(cliente, bd)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int puerto = PUERTO_DEFECTO;
        if (args.length > 0) {
            try {
                puerto = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) { }
        }
        new ServidorFidecompro(puerto).iniciar();
    }

    /**
     * Hilo que atiende a un único cliente durante toda su conexión,
     * procesando solicitudes hasta que el cliente cierra el socket.
     */
    private static class ManejadorCliente implements Runnable {
        private final Socket socket;
        private final GestorBaseDatos bd;

        ManejadorCliente(Socket socket, GestorBaseDatos bd) {
            this.socket = socket;
            this.bd = bd;
        }

        @Override
        public void run() {
            try (ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

                while (true) {
                    Solicitud solicitud;
                    try {
                        solicitud = (Solicitud) entrada.readObject();
                    } catch (EOFException fin) {
                        break; // el cliente cerró la conexión
                    }
                    Respuesta respuesta = procesar(solicitud);
                    salida.writeObject(respuesta);
                    salida.flush();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Conexión finalizada con error: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) { }
                System.out.println("Cliente desconectado: " + socket.getInetAddress());
            }
        }

        private Respuesta procesar(Solicitud s) {
            try {
                Object[] d = s.getDatos();
                switch (s.getOperacion()) {
                    case AUTENTICAR: {
                        Usuario u = bd.autenticar((String) d[0], (String) d[1]);
                        if (u == null) return Respuesta.error("Usuario o contraseña incorrectos.");
                        return Respuesta.ok(u);
                    }
                    case LISTAR_CLIENTES:
                        return Respuesta.ok(new java.util.ArrayList<>(bd.getClientes()));
                    case AGREGAR_CLIENTE:
                        bd.agregarCliente((Cliente) d[0]);
                        return Respuesta.ok(null);
                    case ACTUALIZAR_CLIENTE:
                        bd.actualizarCliente((Cliente) d[0]);
                        return Respuesta.ok(null);
                    case BUSCAR_CLIENTE:
                        return Respuesta.ok(bd.buscarCliente((String) d[0]));
                    case LISTAR_PRODUCTOS:
                        return Respuesta.ok(new java.util.ArrayList<>(bd.getProductos()));
                    case AGREGAR_PRODUCTO:
                        bd.agregarProducto((Producto) d[0]);
                        return Respuesta.ok(null);
                    case ACTUALIZAR_PRODUCTO:
                        bd.actualizarProducto((Producto) d[0]);
                        return Respuesta.ok(null);
                    case LISTAR_FACTURAS:
                        return Respuesta.ok(new java.util.ArrayList<>(bd.getFacturas()));
                    case GENERAR_NUMERO_FACTURA:
                        return Respuesta.ok(bd.generarNumeroFactura());
                    case AGREGAR_FACTURA:
                        bd.agregarFactura((Factura) d[0]);
                        return Respuesta.ok(null);
                    default:
                        return Respuesta.error("Operación no reconocida.");
                }
            } catch (RegistroDuplicadoException | StockInsuficienteException ex) {
                return Respuesta.error(ex.getMessage());
            } catch (Exception ex) {
                return Respuesta.error("Error interno del servidor: " + ex.getMessage());
            }
        }
    }
}
