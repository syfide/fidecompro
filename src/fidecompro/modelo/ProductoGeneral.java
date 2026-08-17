package fidecompro.modelo;

import java.io.Serializable;

/**
 * Producto General — aplica IVA estándar (13%).
 * Demuestra: Herencia, Polimorfismo
 */
public class ProductoGeneral extends Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double IVA = 0.13;

    public ProductoGeneral(String codigo, String nombre, double precio, int stock) {
        super(codigo, nombre, precio, stock);
    }

    @Override
    public double calcularPrecioFinal() {
        return precio * (1 + IVA);
    }

    @Override
    public String getTipoProducto() {
        return "General";
    }
}
