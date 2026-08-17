package fidecompro.modelo;

import java.io.Serializable;

/**
 * Clase abstracta Producto.
 * Demuestra: Herencia (base para múltiples tipos), Polimorfismo
 */
public abstract class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String codigo;
    protected String nombre;
    protected double precio;
    protected int stock;

    public Producto(String codigo, String nombre, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Método abstracto: polimorfismo — cada tipo calcula su precio final
    public abstract double calcularPrecioFinal();

    // Método abstracto: tipo de producto
    public abstract String getTipoProducto();

    public boolean hayStock(int cantidad) {
        return stock >= cantidad;
    }

    public void reducirStock(int cantidad) {
        this.stock -= cantidad;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - ₡" + String.format("%.2f", calcularPrecioFinal());
    }
}
