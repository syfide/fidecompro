package fidecompro.modelo;

import java.io.Serializable;

/**
 * Clase abstracta base para todas las personas del sistema.
 * Demuestra: Herencia (base), Serialización
 */
public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected String apellido;
    protected String telefono;
    protected String email;

    public Persona(String nombre, String apellido, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    // Método abstracto: polimorfismo
    public abstract String getTipoPersona();

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
