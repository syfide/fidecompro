package fidecompro.excepciones;

/**
 * Excepción para fallos de autenticación.
 */
public class AutenticacionException extends Exception {
    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
