package fidecompro.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase Factura.
 * Demuestra: Colecciones (ArrayList), Serialización
 */
public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numeroFactura;
    private Cliente cliente;
    private Usuario vendedor;
    private Date fecha;
    private List<LineaFactura> lineas;
    private String estado; // EMITIDA, ANULADA

    public Factura(String numeroFactura, Cliente cliente, Usuario vendedor) {
        this.numeroFactura = numeroFactura;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.fecha = new Date();
        this.lineas = new ArrayList<>();
        this.estado = "EMITIDA";
    }

    public void agregarLinea(LineaFactura linea) {
        lineas.add(linea);
    }

    public double getSubtotalSinImpuestos() {
        double total = 0;
        for (LineaFactura l : lineas) {
            total += l.getProducto().getPrecio() * l.getCantidad();
        }
        return total;
    }

    public double getTotalImpuestos() {
        double total = 0;
        for (LineaFactura l : lineas) {
            double precioConImp = l.getProducto().calcularPrecioFinal() * l.getCantidad();
            double precioSinImp = l.getProducto().getPrecio() * l.getCantidad();
            total += (precioConImp - precioSinImp);
        }
        return total;
    }

    public double getTotalGeneral() {
        double total = 0;
        for (LineaFactura l : lineas) {
            total += l.getSubtotal();
        }
        return total;
    }

    public String generarTextoFactura() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================\n");
        sb.append("              FIDECOMPRO - FACTURA DE VENTA            \n");
        sb.append("=======================================================\n");
        sb.append(String.format("N° Factura : %s%n", numeroFactura));
        sb.append(String.format("Fecha      : %s%n", sdf.format(fecha)));
        sb.append(String.format("Vendedor   : %s%n", vendedor.getNombreCompleto()));
        sb.append("-------------------------------------------------------\n");
        sb.append("DATOS DEL CLIENTE\n");
        sb.append(String.format("  Cédula   : %s%n", cliente.getCedula()));
        sb.append(String.format("  Nombre   : %s%n", cliente.getNombreCompleto()));
        sb.append(String.format("  Teléfono : %s%n", cliente.getTelefono()));
        sb.append(String.format("  Email    : %s%n", cliente.getEmail()));
        sb.append(String.format("  Dirección: %s%n", cliente.getDireccion()));
        sb.append("-------------------------------------------------------\n");
        sb.append(String.format("%-5s %-25s %8s %12s %12s%n",
                "Cant", "Producto", "Tipo", "P.Unit", "Subtotal"));
        sb.append("-------------------------------------------------------\n");
        for (LineaFactura l : lineas) {
            sb.append(String.format("%-5d %-25s %8s %12.2f %12.2f%n",
                    l.getCantidad(),
                    l.getProducto().getNombre(),
                    l.getProducto().getTipoProducto().substring(0, Math.min(8, l.getProducto().getTipoProducto().length())),
                    l.getProducto().calcularPrecioFinal(),
                    l.getSubtotal()));
        }
        sb.append("-------------------------------------------------------\n");
        sb.append(String.format("%-40s %12.2f%n", "Subtotal sin impuestos (₡):", getSubtotalSinImpuestos()));
        sb.append(String.format("%-40s %12.2f%n", "Total impuestos (₡):", getTotalImpuestos()));
        sb.append(String.format("%-40s %12.2f%n", "TOTAL A PAGAR (₡):", getTotalGeneral()));
        sb.append("=======================================================\n");
        sb.append("         ¡Gracias por su compra en Fidecompro!         \n");
        sb.append("=======================================================\n");
        return sb.toString();
    }

    // Getters
    public String getNumeroFactura() { return numeroFactura; }
    public Cliente getCliente() { return cliente; }
    public Usuario getVendedor() { return vendedor; }
    public Date getFecha() { return fecha; }
    public List<LineaFactura> getLineas() { return lineas; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return numeroFactura + " - " + cliente.getNombreCompleto() +
               " - ₡" + String.format("%.2f", getTotalGeneral());
    }
}
