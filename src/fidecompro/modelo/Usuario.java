package fidecompro.modelo;

import java.io.Serializable;

/**
 * Clase Usuario — hereda de Persona.
 * Demuestra: Herencia, Polimorfismo
 */
public class Usuario extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String rol; // ADMIN, VENDEDOR

    public Usuario(String username, String password, String nombre,
                   String apellido, String email, String rol) {
        super(nombre, apellido, "", email);
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    @Override
    public String getTipoPersona() {
        return "Usuario (" + rol + ")";
    }

    public boolean autenticar(String user, String pass) {
        return this.username.equals(user) && this.password.equals(pass);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
        return username + " [" + rol + "]";
    }
}
