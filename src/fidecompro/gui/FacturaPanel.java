package fidecompro.gui;

import fidecompro.excepciones.StockInsuficienteException;
import fidecompro.modelo.*;
import fidecompro.red.AlmacenRemoto;
import fidecompro.util.FacturaExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de creación y listado de facturas.
 * Demuestra: Excepciones (StockInsuficiente), Colecciones, Archivos
 */
public class FacturaPanel extends JPanel {

    private Usuario usuarioActual;

    // Sección nueva factura
    private JComboBox<Cliente> cmbCliente;
    private JComboBox<Producto> cmbProducto;
    private JTextField txtCantidad;
    private JButton btnAgregar, btnGenerarFactura, btnLimpiarFactura;
    private DefaultTableModel modeloLineas;
    private JLabel lblTotal;
    private List<LineaFactura> lineasActuales = new ArrayList<>();

    // Historial
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    public FacturaPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));
        inicializarUI();
    }

    private void inicializarUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(245, 248, 255));
        header.setBorder(new EmptyBorder(20, 24, 8, 24));
        JLabel titulo = new JLabel("🧾  Facturación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(22, 40, 70));
        header.add(titulo);

        // División: izquierda=nueva factura, derecha=historial
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(560);
        split.setBorder(null);

        // ── Panel nueva factura ───────────────────────────────────────────
        JPanel panelNueva = new JPanel(new BorderLayout());
        panelNueva.setBackground(new Color(245, 248, 255));
        panelNueva.setBorder(new EmptyBorder(0, 12, 12, 6));

        JLabel lblNueva = new JLabel("Nueva Factura");
        lblNueva.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNueva.setForeground(new Color(22, 40, 70));
        lblNueva.setBorder(new EmptyBorder(0, 0, 8, 0));

        // Cliente
        JPanel pCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pCliente.setBackground(Color.WHITE);
        pCliente.setBorder(BorderFactory.createTitledBorder("Seleccionar Cliente"));
        cmbCliente = new JComboBox<>();
        cmbCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbCliente.setPreferredSize(new Dimension(350, 28));
        pCliente.add(cmbCliente);

        // Agregar producto
        JPanel pAgregar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pAgregar.setBackground(Color.WHITE);
        pAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        cmbProducto = new JComboBox<>();
        cmbProducto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cmbProducto.setPreferredSize(new Dimension(260, 28));
        txtCantidad = new JTextField("1", 4);
        btnAgregar = new JButton("+ Agregar");
        btnAgregar.setBackground(new Color(40, 130, 200));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pAgregar.add(new JLabel("Producto:"));
        pAgregar.add(cmbProducto);
        pAgregar.add(new JLabel("Cant:"));
        pAgregar.add(txtCantidad);
        pAgregar.add(btnAgregar);

        // Tabla de líneas
        String[] colsLineas = {"Código", "Producto", "Tipo", "Cant.", "P.Unit.", "Subtotal"};
        modeloLineas = new DefaultTableModel(colsLineas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaLineas = new JTable(modeloLineas);
        tablaLineas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaLineas.setRowHeight(24);
        tablaLineas.getTableHeader().setBackground(new Color(22, 40, 70));
        tablaLineas.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollLineas = new JScrollPane(tablaLineas);
        scrollLineas.setPreferredSize(new Dimension(0, 200));

        // Total
        JPanel pTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pTotal.setBackground(new Color(245, 248, 255));
        lblTotal = new JLabel("TOTAL: ₡0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(22, 40, 70));
        pTotal.add(lblTotal);

        // Botones factura
        JPanel pBotonesFactura = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pBotonesFactura.setBackground(new Color(245, 248, 255));
        btnLimpiarFactura = new JButton("Cancelar");
        btnLimpiarFactura.setBackground(new Color(180, 60, 60));
        btnLimpiarFactura.setForeground(Color.WHITE);
        btnLimpiarFactura.setFocusPainted(false);
        btnGenerarFactura = new JButton("✅  Generar Factura");
        btnGenerarFactura.setBackground(new Color(22, 140, 70));
        btnGenerarFactura.setForeground(Color.WHITE);
        btnGenerarFactura.setFocusPainted(false);
        btnGenerarFactura.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pBotonesFactura.add(btnLimpiarFactura);
        pBotonesFactura.add(btnGenerarFactura);

        JPanel infoNueva = new JPanel(new BorderLayout());
        infoNueva.setBackground(new Color(245, 248, 255));
        infoNueva.add(lblNueva, BorderLayout.NORTH);
        infoNueva.add(pCliente, BorderLayout.CENTER);

        JPanel topNueva = new JPanel(new BorderLayout());
        topNueva.setBackground(new Color(245, 248, 255));
        topNueva.add(infoNueva, BorderLayout.NORTH);
        topNueva.add(pAgregar, BorderLayout.SOUTH);

        JPanel botNueva = new JPanel(new BorderLayout());
        botNueva.setBackground(new Color(245, 248, 255));
        botNueva.add(pTotal, BorderLayout.NORTH);
        botNueva.add(pBotonesFactura, BorderLayout.SOUTH);

        panelNueva.add(topNueva, BorderLayout.NORTH);
        panelNueva.add(scrollLineas, BorderLayout.CENTER);
        panelNueva.add(botNueva, BorderLayout.SOUTH);

        // ── Panel historial ───────────────────────────────────────────────
        JPanel panelHist = new JPanel(new BorderLayout());
        panelHist.setBackground(new Color(245, 248, 255));
        panelHist.setBorder(new EmptyBorder(0, 6, 12, 12));

        JLabel lblHist = new JLabel("Historial de Facturas");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHist.setForeground(new Color(22, 40, 70));
        lblHist.setBorder(new EmptyBorder(0, 0, 8, 0));

        String[] colsHist = {"N° Factura", "Cliente", "Vendedor", "Total", "Estado"};
        modeloHistorial = new DefaultTableModel(colsHist, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaHistorial.setRowHeight(24);
        tablaHistorial.getTableHeader().setBackground(new Color(200, 130, 30));
        tablaHistorial.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollHist = new JScrollPane(tablaHistorial);

        JButton btnImprimir = new JButton("📄  Exportar seleccionada");
        btnImprimir.setBackground(new Color(22, 40, 70));
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.setFocusPainted(false);
        btnImprimir.addActionListener(e -> exportarFacturaSeleccionada());

        panelHist.add(lblHist, BorderLayout.NORTH);
        panelHist.add(scrollHist, BorderLayout.CENTER);
        JPanel pBtnHist = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtnHist.setBackground(new Color(245, 248, 255));
        pBtnHist.add(btnImprimir);
        panelHist.add(pBtnHist, BorderLayout.SOUTH);

        split.setLeftComponent(panelNueva);
        split.setRightComponent(panelHist);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(245, 248, 255));
        top.add(header, BorderLayout.NORTH);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // Acciones
        btnAgregar.addActionListener(e -> agregarLinea());
        btnGenerarFactura.addActionListener(e -> generarFactura());
        btnLimpiarFactura.addActionListener(e -> limpiarFactura());

        refrescar();
    }

    private void agregarLinea() {
        Producto p = (Producto) cmbProducto.getSelectedItem();
        if (p == null) return;
        try {
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            if (cant <= 0) throw new NumberFormatException();

            // Validar stock (excepción personalizada)
            if (!p.hayStock(cant)) {
                throw new StockInsuficienteException(p.getCodigo(), cant, p.getStock());
            }

            LineaFactura linea = new LineaFactura(p, cant);
            lineasActuales.add(linea);
            modeloLineas.addRow(new Object[]{
                p.getCodigo(), p.getNombre(), p.getTipoProducto(),
                cant,
                String.format("₡%.2f", p.calcularPrecioFinal()),
                String.format("₡%.2f", linea.getSubtotal())
            });
            actualizarTotal();

        } catch (StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida (número entero > 0).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTotal() {
        double total = lineasActuales.stream().mapToDouble(LineaFactura::getSubtotal).sum();
        lblTotal.setText(String.format("TOTAL: ₡%,.2f", total));
    }

    private void generarFactura() {
        if (cmbCliente.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (lineasActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        String numero = AlmacenRemoto.getInstance().generarNumeroFactura();
        Factura factura = new Factura(numero, cliente, usuarioActual);

        // Agregar líneas (el servidor valida stock y lo descuenta de forma atómica)
        for (LineaFactura l : lineasActuales) {
            factura.agregarLinea(l);
        }

        try {
            AlmacenRemoto.getInstance().agregarFactura(factura);
        } catch (fidecompro.excepciones.StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Exportar a archivo (multihilo)
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                File dir = new File("facturas");
                if (!dir.exists()) dir.mkdirs();
                return FacturaExporter.exportar(factura, "facturas");
            }

            @Override
            protected void done() {
                try {
                    String archivo = get();
                    JOptionPane.showMessageDialog(FacturaPanel.this,
                            "✅ Factura " + numero + " generada.\nArchivo: " + archivo,
                            "Factura emitida", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FacturaPanel.this,
                            "Factura guardada, pero no se pudo exportar: " + ex.getMessage(),
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
                limpiarFactura();
                refrescar();
            }
        };
        worker.execute();
    }

    private void exportarFacturaSeleccionada() {
        int fila = tablaHistorial.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura del historial.", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String numFactura = (String) modeloHistorial.getValueAt(fila, 0);
        Factura f = AlmacenRemoto.getInstance().getFacturas().stream()
                .filter(fc -> fc.getNumeroFactura().equals(numFactura))
                .findFirst().orElse(null);
        if (f == null) return;

        try {
            File dir = new File("facturas");
            if (!dir.exists()) dir.mkdirs();
            String archivo = FacturaExporter.exportar(f, "facturas");
            JOptionPane.showMessageDialog(this, "Factura exportada:\n" + archivo,
                    "Exportado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFactura() {
        lineasActuales.clear();
        modeloLineas.setRowCount(0);
        lblTotal.setText("TOTAL: ₡0.00");
        cmbCliente.setSelectedIndex(0);
    }

    public void refrescar() {
        // Recargar combos
        cmbCliente.removeAllItems();
        for (Cliente c : AlmacenRemoto.getInstance().getClientes()) cmbCliente.addItem(c);

        cmbProducto.removeAllItems();
        for (Producto p : AlmacenRemoto.getInstance().getProductos()) cmbProducto.addItem(p);

        // Recargar historial
        modeloHistorial.setRowCount(0);
        for (Factura f : AlmacenRemoto.getInstance().getFacturas()) {
            modeloHistorial.addRow(new Object[]{
                f.getNumeroFactura(),
                f.getCliente().getNombreCompleto(),
                f.getVendedor().getNombreCompleto(),
                String.format("₡%,.2f", f.getTotalGeneral()),
                f.getEstado()
            });
        }
    }
}
