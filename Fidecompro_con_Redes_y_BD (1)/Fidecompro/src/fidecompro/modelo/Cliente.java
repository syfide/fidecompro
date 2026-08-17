package fidecompro.modelo;

import java.io.Serializable;

/**
 * Clase Cliente — hereda de Persona.
 * Demuestra: Herencia, Polimorfismo
 */
public class Cliente extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cedula;
    private String direccion;

    public Cliente(String cedula, String nombre, String apellido,
                   String telefono, String email, String direccion) {
        super(nombre, apellido, telefono, email);
        this.cedula = cedula;
        this.direccion = direccion;
    }

    @Override
    public String getTipoPersona() {
        return "Cliente";
    }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return cedula + " - " + getNombreCompleto();
    }
}
