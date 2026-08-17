package fidecompro.modelo;

import java.io.Serializable;

/**
 * Producto Electrónico — IVA 13% + cargo por garantía (2%).
 * Demuestra: Herencia, Polimorfismo
 */
public class ProductoElectronico extends Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final double IVA = 0.13;
    private static final double CARGO_GARANTIA = 0.02;
    private int mesesGarantia;

    public ProductoElectronico(String codigo, String nombre, double precio,
                                int stock, int mesesGarantia) {
        super(codigo, nombre, precio, stock);
        this.mesesGarantia = mesesGarantia;
    }

    @Override
    public double calcularPrecioFinal() {
        return precio * (1 + IVA + CARGO_GARANTIA);
    }

    @Override
    public String getTipoProducto() {
        return "Electrónico (" + mesesGarantia + " meses garantía)";
    }

    public int getMesesGarantia() { return mesesGarantia; }
    public void setMesesGarantia(int mesesGarantia) { this.mesesGarantia = mesesGarantia; }
}
