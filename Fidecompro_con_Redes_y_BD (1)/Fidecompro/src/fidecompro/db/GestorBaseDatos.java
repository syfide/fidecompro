package fidecompro.db;

import fidecompro.excepciones.RegistroDuplicadoException;
import fidecompro.modelo.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Motor de base de datos propio, hecho desde cero (sin JDBC ni librerías
 * externas). Cada entidad se persiste como una "tabla" en un archivo de
 * texto plano con campos delimitados por "|", y se mantiene un índice en
 * memoria (HashMap) por clave primaria para acceso rápido — el mismo
 * principio que usa cualquier motor de base de datos simple.
 *
 * Es utilizado exclusivamente por el servidor (fidecompro.red.ServidorFidecompro).
 * Todas las operaciones son thread-safe mediante un ReentrantReadWriteLock,
 * ya que el servidor atiende múltiples clientes de forma concurrente.
 */
public class GestorBaseDatos {

    private static final String DIR_DATOS = "data";
    private static final String TABLA_USUARIOS   = DIR_DATOS + "/usuarios.tabla";
    private static final String TABLA_CLIENTES   = DIR_DATOS + "/clientes.tabla";
    private static final String TABLA_PRODUCTOS  = DIR_DATOS + "/productos.tabla";
    private static final String TABLA_FACTURAS   = DIR_DATOS + "/facturas.tabla";

    private final Map<String, Usuario> usuarios   = new LinkedHashMap<>();
    private final Map<String, Cliente> clientes   = new LinkedHashMap<>();
    private final Map<String, Producto> productos = new LinkedHashMap<>();
    private final Map<String, Factura> facturas   = new LinkedHashMap<>();
    private int contadorFactura = 1;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static GestorBaseDatos instancia;

    public static synchronized GestorBaseDatos getInstance() {
        if (instancia == null) {
            instancia = new GestorBaseDatos();
        }
        return instancia;
    }

    private GestorBaseDatos() {
        try {
            Files.createDirectories(Paths.get(DIR_DATOS));
        } catch (IOException e) {
            System.err.println("No se pudo crear el directorio de datos: " + e.getMessage());
        }
        cargarTodo();
        if (usuarios.isEmpty()) {
            cargarDatosIniciales();
        }
    }

    // ── Carga inicial de ejemplo (solo si la base de datos está vacía) ────────
    private void cargarDatosIniciales() {
        try {
            agregarUsuario(new Usuario("admin", "admin123", "Administrador",
                    "Sistema", "admin@fidecompro.com", "ADMIN"));
            agregarUsuario(new Usuario("vendedor1", "vend123", "Carlos",
                    "Ramírez", "cramirez@fidecompro.com", "VENDEDOR"));

            agregarProducto(new ProductoGeneral("GEN001", "Detergente Líquido 1L", 3500.00, 50));
            agregarProducto(new ProductoGeneral("GEN002", "Shampoo Pantene 400ml", 4200.00, 30));
            agregarProducto(new ProductoAlimenticio("ALI001", "Arroz El Toro 1kg", 1200.00, 100, "Granos"));
            agregarProducto(new ProductoAlimenticio("ALI002", "Leche Dos Pinos 1L", 950.00, 80, "Lácteos"));
            agregarProducto(new ProductoElectronico("ELE001", "Audífonos Bluetooth", 25000.00, 15, 12));
            agregarProducto(new ProductoElectronico("ELE002", "Cable USB-C 2m", 3800.00, 40, 6));

            agregarCliente(new Cliente("1-0234-5678", "María", "González",
                    "8888-1111", "mgonzalez@gmail.com", "San José, Costa Rica"));
            agregarCliente(new Cliente("2-0456-7890", "Juan", "Pérez",
                    "7777-2222", "jperez@hotmail.com", "Alajuela, Costa Rica"));
        } catch (RegistroDuplicadoException e) {
            // No debería ocurrir en la carga inicial
        }
    }

    // ── Usuarios ────────────────────────────────────────────────────────────
    public void agregarUsuario(Usuario u) throws RegistroDuplicadoException {
        lock.writeLock().lock();
        try {
            if (usuarios.containsKey(u.getUsername())) {
                throw new RegistroDuplicadoException("Ya existe un usuario: " + u.getUsername());
            }
            usuarios.put(u.getUsername(), u);
            guardarTabla(TABLA_USUARIOS, usuarios.values(), this::lineaDeUsuario);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Usuario autenticar(String username, String password) {
        lock.readLock().lock();
        try {
            Usuario u = usuarios.get(username);
            if (u != null && u.autenticar(username, password)) {
                return u;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    // ── Clientes ────────────────────────────────────────────────────────────
    public void agregarCliente(Cliente c) throws RegistroDuplicadoException {
        lock.writeLock().lock();
        try {
            if (clientes.containsKey(c.getCedula())) {
                throw new RegistroDuplicadoException("Ya existe un cliente con cédula: " + c.getCedula());
            }
            clientes.put(c.getCedula(), c);
            guardarTabla(TABLA_CLIENTES, clientes.values(), this::lineaDeCliente);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void actualizarCliente(Cliente c) {
        lock.writeLock().lock();
        try {
            clientes.put(c.getCedula(), c);
            guardarTabla(TABLA_CLIENTES, clientes.values(), this::lineaDeCliente);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Cliente> getClientes() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(clientes.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Cliente buscarCliente(String cedula) {
        lock.readLock().lock();
        try {
            return clientes.get(cedula);
        } finally {
            lock.readLock().unlock();
        }
    }

    // ── Productos ───────────────────────────────────────────────────────────
    public void agregarProducto(Producto p) throws RegistroDuplicadoException {
        lock.writeLock().lock();
        try {
            if (productos.containsKey(p.getCodigo())) {
                throw new RegistroDuplicadoException("Ya existe un producto con código: " + p.getCodigo());
            }
            productos.put(p.getCodigo(), p);
            guardarTabla(TABLA_PRODUCTOS, productos.values(), this::lineaDeProducto);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void actualizarProducto(Producto p) {
        lock.writeLock().lock();
        try {
            productos.put(p.getCodigo(), p);
            guardarTabla(TABLA_PRODUCTOS, productos.values(), this::lineaDeProducto);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Producto> getProductos() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(productos.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    public Producto buscarProducto(String codigo) {
        lock.readLock().lock();
        try {
            return productos.get(codigo);
        } finally {
            lock.readLock().unlock();
        }
    }

    // ── Facturas ────────────────────────────────────────────────────────────
    public String generarNumeroFactura() {
        lock.writeLock().lock();
        try {
            return String.format("FCT-%05d", contadorFactura++);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Agrega la factura y descuenta el stock de cada producto involucrado
     * de forma atómica (protegido por el lock de escritura), validando que
     * exista stock suficiente en el servidor antes de confirmar la venta.
     */
    public void agregarFactura(Factura f) throws fidecompro.excepciones.StockInsuficienteException {
        lock.writeLock().lock();
        try {
            for (LineaFactura l : f.getLineas()) {
                Producto pServidor = productos.get(l.getProducto().getCodigo());
                if (pServidor == null || !pServidor.hayStock(l.getCantidad())) {
                    int disponible = pServidor == null ? 0 : pServidor.getStock();
                    throw new fidecompro.excepciones.StockInsuficienteException(
                            l.getProducto().getCodigo(), l.getCantidad(), disponible);
                }
            }
            for (LineaFactura l : f.getLineas()) {
                Producto pServidor = productos.get(l.getProducto().getCodigo());
                pServidor.reducirStock(l.getCantidad());
            }
            facturas.put(f.getNumeroFactura(), f);
            guardarTabla(TABLA_PRODUCTOS, productos.values(), this::lineaDeProducto);
            guardarTabla(TABLA_FACTURAS, facturas.values(), this::lineaDeFactura);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Factura> getFacturas() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(facturas.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    // ── Persistencia: escritura genérica de una tabla ─────────────────────────
    private <T> void guardarTabla(String archivo, Collection<T> registros,
                                   java.util.function.Function<T, String> serializador) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(archivo), StandardCharsets.UTF_8)) {
            for (T r : registros) {
                bw.write(serializador.apply(r));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar tabla " + archivo + ": " + e.getMessage());
        }
    }

    // ── Carga de todas las tablas al iniciar el servidor ──────────────────────
    private void cargarTodo() {
        cargarUsuarios();
        cargarClientes();
        cargarProductos();
        cargarFacturas();
    }

    private void cargarUsuarios() {
        for (String linea : leerLineas(TABLA_USUARIOS)) {
            String[] c = linea.split("\\|", -1);
            Usuario u = new Usuario(c[0], c[1], c[2], c[3], c[4], c[5]);
            usuarios.put(u.getUsername(), u);
        }
    }

    private void cargarClientes() {
        for (String linea : leerLineas(TABLA_CLIENTES)) {
            String[] c = linea.split("\\|", -1);
            Cliente cli = new Cliente(c[0], c[1], c[2], c[3], c[4], c[5]);
            clientes.put(cli.getCedula(), cli);
        }
    }

    private void cargarProductos() {
        for (String linea : leerLineas(TABLA_PRODUCTOS)) {
            String[] c = linea.split("\\|", -1);
            String tipo = c[0];
            String codigo = c[1], nombre = c[2];
            double precio = Double.parseDouble(c[3]);
            int stock = Integer.parseInt(c[4]);
            String extra = c[5];
            Producto p;
            switch (tipo) {
                case "ALIMENTICIO":
                    p = new ProductoAlimenticio(codigo, nombre, precio, stock, extra);
                    break;
                case "ELECTRONICO":
                    p = new ProductoElectronico(codigo, nombre, precio, stock,
                            extra.isEmpty() ? 0 : Integer.parseInt(extra));
                    break;
                default:
                    p = new ProductoGeneral(codigo, nombre, precio, stock);
            }
            productos.put(p.getCodigo(), p);
        }
    }

    private void cargarFacturas() {
        int maxNumero = 0;
        for (String linea : leerLineas(TABLA_FACTURAS)) {
            String[] c = linea.split("\\|", -1);
            String numero = c[0];
            Cliente cli = clientes.get(c[1]);
            Usuario vend = usuarios.get(c[2]);
            long fechaMillis = Long.parseLong(c[3]);
            String estado = c[4];
            String lineasStr = c.length > 5 ? c[5] : "";
            if (cli == null || vend == null) continue;

            Factura f = new Factura(numero, cli, vend);
            f.setEstado(estado);
            try {
                java.lang.reflect.Field fecha = Factura.class.getDeclaredField("fecha");
                fecha.setAccessible(true);
                fecha.set(f, new Date(fechaMillis));
            } catch (Exception ignored) { }

            if (!lineasStr.isEmpty()) {
                for (String par : lineasStr.split(";")) {
                    String[] pc = par.split(":");
                    Producto p = productos.get(pc[0]);
                    if (p != null) {
                        f.agregarLinea(new LineaFactura(p, Integer.parseInt(pc[1])));
                    }
                }
            }
            facturas.put(numero, f);

            try {
                int n = Integer.parseInt(numero.replace("FCT-", ""));
                maxNumero = Math.max(maxNumero, n);
            } catch (NumberFormatException ignored) { }
        }
        contadorFactura = maxNumero + 1;
    }

    private List<String> leerLineas(String archivo) {
        Path p = Paths.get(archivo);
        if (!Files.exists(p)) return Collections.emptyList();
        try {
            List<String> lineas = Files.readAllLines(p, StandardCharsets.UTF_8);
            lineas.removeIf(String::isBlank);
            return lineas;
        } catch (IOException e) {
            System.err.println("Error al leer tabla " + archivo + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ── Serializadores a línea de texto (formato de "fila" de la tabla) ───────
    private String lineaDeUsuario(Usuario u) {
        return String.join("|", u.getUsername(), u.getPassword(), u.getNombre(),
                u.getApellido(), u.getEmail(), u.getRol());
    }

    private String lineaDeCliente(Cliente c) {
        return String.join("|", c.getCedula(), c.getNombre(), c.getApellido(),
                c.getTelefono(), c.getEmail(), c.getDireccion());
    }

    private String lineaDeProducto(Producto p) {
        String tipo = "GENERAL";
        String extra = "";
        if (p instanceof ProductoAlimenticio pa) {
            tipo = "ALIMENTICIO";
            extra = pa.getCategoria();
        } else if (p instanceof ProductoElectronico pe) {
            tipo = "ELECTRONICO";
            extra = String.valueOf(pe.getMesesGarantia());
        }
        return String.join("|", tipo, p.getCodigo(), p.getNombre(),
                String.valueOf(p.getPrecio()), String.valueOf(p.getStock()), extra);
    }

    private String lineaDeFactura(Factura f) {
        StringBuilder lineas = new StringBuilder();
        for (LineaFactura l : f.getLineas()) {
            if (lineas.length() > 0) lineas.append(";");
            lineas.append(l.getProducto().getCodigo()).append(":").append(l.getCantidad());
        }
        return String.join("|", f.getNumeroFactura(), f.getCliente().getCedula(),
                f.getVendedor().getUsername(), String.valueOf(f.getFecha().getTime()),
                f.getEstado(), lineas.toString());
    }
}
