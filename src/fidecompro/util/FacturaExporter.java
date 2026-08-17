package fidecompro.util;

import fidecompro.modelo.Factura;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Exporta facturas a archivo de texto.
 */
public class FacturaExporter {

    public static String exportar(Factura factura, String directorio) throws IOException {
        String nombreArchivo = directorio + "/" + factura.getNumeroFactura() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.print(factura.generarTextoFactura());
        }
        return nombreArchivo;
    }
}
