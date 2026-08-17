package fidecompro.modelo;

import java.io.Serializable;

/**
 * Representa una línea dentro de una factura.
 */
public class LineaFactura implements Serializable {
    private static final long serialVersionUID = 1L;

    private Producto producto;
    private int cantidad;

    public LineaFactura(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return producto.calcularPrecioFinal() * cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
