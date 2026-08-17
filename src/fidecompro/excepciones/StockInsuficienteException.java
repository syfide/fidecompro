package fidecompro.excepciones;

/**
 * Excepción para stock insuficiente.
 * Demuestra: Excepciones personalizadas
 */
public class StockInsuficienteException extends Exception {
    private String codigoProducto;
    private int cantidadSolicitada;
    private int stockDisponible;

    public StockInsuficienteException(String codigoProducto,
                                       int cantidadSolicitada,
                                       int stockDisponible) {
        super("Stock insuficiente para el producto [" + codigoProducto + "]. " +
              "Solicitado: " + cantidadSolicitada + ", Disponible: " + stockDisponible);
        this.codigoProducto = codigoProducto;
        this.cantidadSolicitada = cantidadSolicitada;
        this.stockDisponible = stockDisponible;
    }

    /** Constructor simple usado al reconstruir la excepción desde una
     *  respuesta de red (el servidor ya envía el mensaje formateado). */
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
        this.codigoProducto = null;
        this.cantidadSolicitada = 0;
        this.stockDisponible = 0;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public int getCantidadSolicitada() { return cantidadSolicitada; }
    public int getStockDisponible() { return stockDisponible; }
}
