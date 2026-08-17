package fidecompro.red;

import fidecompro.excepciones.AutenticacionException;
import fidecompro.excepciones.RegistroDuplicadoException;
import fidecompro.excepciones.StockInsuficienteException;
import fidecompro.modelo.*;
import fidecompro.red.Protocolo.Operacion;
import fidecompro.red.Protocolo.Respuesta;
import fidecompro.red.Protocolo.Solicitud;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Cliente de red: es la puerta de entrada que usa la interfaz gráfica (GUI)
 * para hablar con el servidor Fidecompro. Mantiene la misma forma de uso
 * que tenía el antiguo almacén local (getInstance(), agregarCliente(),
 * getProductos(), etc.) pero cada llamada en realidad abre una conexión
 * de socket, envía una Solicitud y espera la Respuesta del servidor.
 *
 * Demuestra: Redes (Socket, cliente TCP)
 */
public class AlmacenRemoto {

    private static AlmacenRemoto instancia;

    private String host = "localhost";
    private int puerto = ServidorFidecompro.PUERTO_DEFECTO;

    private AlmacenRemoto() { }

    public static synchronized AlmacenRemoto getInstance() {
        if (instancia == null) {
            instancia = new AlmacenRemoto();
        }
        return instancia;
    }

    /** Permite configurar a qué servidor conectarse antes de usar la app. */
    public void configurar(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public String getHost() { return host; }
    public int getPuerto() { return puerto; }

    /** Verifica que el servidor esté disponible en host:puerto. */
    public boolean probarConexion() {
        try (Socket s = new Socket(host, puerto)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ── Comunicación de bajo nivel ─────────────────────────────────────────
    private Respuesta enviar(Operacion op, Object... datos) throws IOException {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            salida.writeObject(new Solicitud(op, datos));
            salida.flush();
            try {
                return (Respuesta) entrada.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("Respuesta inválida del servidor.", e);
            }
        }
    }

    // ── Autenticación ───────────────────────────────────────────────────────
    public Usuario autenticar(String usuario, String password) throws AutenticacionException {
        try {
            Respuesta r = enviar(Operacion.AUTENTICAR, usuario, password);
            if (!r.isExito()) throw new AutenticacionException(r.getMensajeError());
            return (Usuario) r.getResultado();
        } catch (IOException e) {
            throw new AutenticacionException("No se pudo conectar al servidor (" + host + ":" + puerto + "): " + e.getMessage());
        }
    }

    // ── Clientes ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Cliente> getClientes() {
        try {
            Respuesta r = enviar(Operacion.LISTAR_CLIENTES);
            return (List<Cliente>) r.getResultado();
        } catch (IOException e) {
            throw new RuntimeException("Error de red al listar clientes: " + e.getMessage());
        }
    }

    public void agregarCliente(Cliente c) throws RegistroDuplicadoException {
        ejecutarEscritura(Operacion.AGREGAR_CLIENTE, c);
    }

    public void actualizarCliente(Cliente c) {
        try {
            enviar(Operacion.ACTUALIZAR_CLIENTE, c);
        } catch (IOException e) {
            throw new RuntimeException("Error de red al actualizar cliente: " + e.getMessage());
        }
    }

    public Cliente buscarCliente(String cedula) {
        try {
            Respuesta r = enviar(Operacion.BUSCAR_CLIENTE, cedula);
            return (Cliente) r.getResultado();
        } catch (IOException e) {
            throw new RuntimeException("Error de red al buscar cliente: " + e.getMessage());
        }
    }

    // ── Productos ───────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Producto> getProductos() {
        try {
            Respuesta r = enviar(Operacion.LISTAR_PRODUCTOS);
            return (List<Producto>) r.getResultado();
        } catch (IOException e) {
            throw new RuntimeException("Error de red al listar productos: " + e.getMessage());
        }
    }

    public void agregarProducto(Producto p) throws RegistroDuplicadoException {
        ejecutarEscritura(Operacion.AGREGAR_PRODUCTO, p);
    }

    public void actualizarProducto(Producto p) {
        try {
            enviar(Operacion.ACTUALIZAR_PRODUCTO, p);
        } catch (IOException e) {
            throw new RuntimeException("Error de red al actualizar producto: " + e.getMessage());
        }
    }

    public Producto buscarProducto(String codigo) {
        for (Producto p : getProductos()) {
            if (p.getCodigo().equals(codigo)) return p;
        }
        return null;
    }

    // ── Facturas ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Factura> getFacturas() {
        try {
            Respuesta r = enviar(Operacion.LISTAR_FACTURAS);
            return (List<Factura>) r.getResultado();
        } catch (IOException e) {
            throw new RuntimeException("Error de red al listar facturas: " + e.getMessage());
        }
    }

    public String generarNumeroFactura() {
        try {
            Respuesta r = enviar(Operacion.GENERAR_NUMERO_FACTURA);
            return (String) r.getResultado();
        } catch (IOException e) {
            throw new RuntimeException("Error de red al generar número de factura: " + e.getMessage());
        }
    }

    /** Lanza StockInsuficienteException si el servidor no tiene stock suficiente. */
    public void agregarFactura(Factura f) throws StockInsuficienteException {
        try {
            Respuesta r = enviar(Operacion.AGREGAR_FACTURA, f);
            if (!r.isExito()) {
                throw new StockInsuficienteException(r.getMensajeError());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de red al generar factura: " + e.getMessage());
        }
    }

    // ── Helper para operaciones de escritura que pueden duplicar registro ─────
    private void ejecutarEscritura(Operacion op, Object dato) throws RegistroDuplicadoException {
        try {
            Respuesta r = enviar(op, dato);
            if (!r.isExito()) {
                throw new RegistroDuplicadoException(r.getMensajeError());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de red: " + e.getMessage());
        }
    }
}
