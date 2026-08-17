package fidecompro.excepciones;

/**
 * Excepción para registros duplicados (cédula, código, usuario).
 */
public class RegistroDuplicadoException extends Exception {
    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
