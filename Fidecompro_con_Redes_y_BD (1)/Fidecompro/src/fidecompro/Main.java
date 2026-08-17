package fidecompro;

import fidecompro.gui.LoginFrame;
import fidecompro.red.AlmacenRemoto;
import fidecompro.red.ServidorFidecompro;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación Fidecompro.
 *
 * Uso:
 *   java -cp out fidecompro.Main servidor [puerto]
 *       Arranca el servidor (base de datos + red). Debe ejecutarse primero.
 *
 *   java -cp out fidecompro.Main [host] [puerto]
 *       Arranca el cliente (interfaz Swing) y lo conecta al servidor
 *       indicado. Si no se especifican, usa localhost y el puerto por defecto.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("servidor")) {
            int puerto = ServidorFidecompro.PUERTO_DEFECTO;
            if (args.length > 1) {
                try { puerto = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
            }
            new ServidorFidecompro(puerto).iniciar();
            return;
        }

        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = ServidorFidecompro.PUERTO_DEFECTO;
        if (args.length > 1) {
            try { puerto = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
        }
        AlmacenRemoto.getInstance().configurar(host, puerto);

        // Ejecutar en el hilo de despacho de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            if (!AlmacenRemoto.getInstance().probarConexion()) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar al servidor en " + host + ":" + puerto +
                        ".\nInicie primero el servidor con:\n  java -cp out fidecompro.Main servidor",
                        "Servidor no disponible", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            new LoginFrame().setVisible(true);
        });
    }
}
