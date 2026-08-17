package fidecompro.red;

import java.io.Serializable;

/**
 * Protocolo de comunicación entre el cliente (interfaz Swing) y el
 * servidor Fidecompro. Los objetos Solicitud/Respuesta viajan serializados
 * a través del socket usando ObjectOutputStream/ObjectInputStream.
 */
public class Protocolo {

    public enum Operacion {
        AUTENTICAR,
        LISTAR_CLIENTES, AGREGAR_CLIENTE, ACTUALIZAR_CLIENTE, BUSCAR_CLIENTE,
        LISTAR_PRODUCTOS, AGREGAR_PRODUCTO, ACTUALIZAR_PRODUCTO,
        LISTAR_FACTURAS, AGREGAR_FACTURA, GENERAR_NUMERO_FACTURA
    }

    /** Mensaje que el cliente envía al servidor. */
    public static class Solicitud implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Operacion operacion;
        private final Object[] datos;

        public Solicitud(Operacion operacion, Object... datos) {
            this.operacion = operacion;
            this.datos = datos;
        }

        public Operacion getOperacion() { return operacion; }
        public Object[] getDatos() { return datos; }
    }

    /** Mensaje que el servidor responde al cliente. */
    public static class Respuesta implements Serializable {
        private static final long serialVersionUID = 1L;
        private final boolean exito;
        private final Object resultado;
        private final String mensajeError;

        private Respuesta(boolean exito, Object resultado, String mensajeError) {
            this.exito = exito;
            this.resultado = resultado;
            this.mensajeError = mensajeError;
        }

        public static Respuesta ok(Object resultado) {
            return new Respuesta(true, resultado, null);
        }

        public static Respuesta error(String mensaje) {
            return new Respuesta(false, null, mensaje);
        }

        public boolean isExito() { return exito; }
        public Object getResultado() { return resultado; }
        public String getMensajeError() { return mensajeError; }
    }
}
