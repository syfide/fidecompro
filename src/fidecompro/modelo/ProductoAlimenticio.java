package fidecompro.modelo;

import java.io.Serializable;

/**
 * Producto Alimenticio — tarifa reducida IVA (1%).
 * Demuestra: Herencia, Polimorfismo
 */
public class ProductoAlimenticio extends Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double IVA_REDUCIDO = 0.01;
    private String categoria; // ej: Lácteos, Carnes, Granos

    public ProductoAlimenticio(String codigo, String nombre, double precio,
                                int stock, String categoria) {
        super(codigo, nombre, precio, stock);
        this.categoria = categoria;
    }

    @Override
    public double calcularPrecioFinal() {
        return precio * (1 + IVA_REDUCIDO);
    }

    @Override
    public String getTipoProducto() {
        return "Alimenticio (" + categoria + ")";
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
