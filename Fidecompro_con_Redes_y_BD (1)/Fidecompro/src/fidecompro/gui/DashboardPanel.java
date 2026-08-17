package fidecompro.gui;

import fidecompro.red.AlmacenRemoto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel de dashboard con estadísticas rápidas.
 */
public class DashboardPanel extends JPanel {

    private JLabel lblClientes, lblProductos, lblFacturas, lblVentas;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));
        inicializarUI();
    }

    private void inicializarUI() {
        // Título
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(245, 248, 255));
        header.setBorder(new EmptyBorder(24, 24, 8, 24));
        JLabel titulo = new JLabel("🏠  Panel Principal");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(new Color(22, 40, 70));
        header.add(titulo);

        // Tarjetas
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setBackground(new Color(245, 248, 255));
        cards.setBorder(new EmptyBorder(16, 24, 24, 24));

        lblClientes  = new JLabel("0");
        lblProductos = new JLabel("0");
        lblFacturas  = new JLabel("0");
        lblVentas    = new JLabel("₡0");

        cards.add(crearTarjeta("👥 Clientes", lblClientes,  new Color(52, 120, 200)));
        cards.add(crearTarjeta("📦 Productos", lblProductos, new Color(40, 160, 100)));
        cards.add(crearTarjeta("🧾 Facturas",  lblFacturas,  new Color(200, 130, 30)));
        cards.add(crearTarjeta("💰 Total Ventas", lblVentas, new Color(150, 50, 160)));

        JLabel info = new JLabel(
            "<html><div style='padding:16px;color:#555;font-size:12px'>" +
            "<b>Bienvenido a Fidecompro</b><br><br>" +
            "Use el menú lateral para gestionar clientes, productos y generar facturas.<br><br>" +
            "Las facturas generadas se guardan automáticamente en la carpeta <b>facturas/</b>." +
            "</div></html>");
        info.setBorder(new EmptyBorder(0, 24, 0, 24));

        add(header, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(info, BorderLayout.SOUTH);

        refrescar();
    }

    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTit.setForeground(new Color(220, 235, 255));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(Color.WHITE);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    public void refrescar() {
        AlmacenRemoto a = AlmacenRemoto.getInstance();
        lblClientes.setText(String.valueOf(a.getClientes().size()));
        lblProductos.setText(String.valueOf(a.getProductos().size()));
        lblFacturas.setText(String.valueOf(a.getFacturas().size()));
        double total = a.getFacturas().stream().mapToDouble(f -> f.getTotalGeneral()).sum();
        lblVentas.setText(String.format("₡%,.0f", total));
    }
}
