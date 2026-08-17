package fidecompro.gui;

import fidecompro.excepciones.RegistroDuplicadoException;
import fidecompro.modelo.*;
import fidecompro.red.AlmacenRemoto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD de Productos.
 * Demuestra: Polimorfismo (distintos tipos de producto)
 */
public class ProductoPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtCodigo, txtNombre, txtPrecio, txtStock, txtExtra;
    private JComboBox<String> cmbTipo;
    private JLabel lblExtra;
    private JButton btnGuardar, btnLimpiar;

    public ProductoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));
        inicializarUI();
    }

    private void inicializarUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(245, 248, 255));
        header.setBorder(new EmptyBorder(20, 24, 8, 24));
        JLabel titulo = new JLabel("📦  Gestión de Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(22, 40, 70));
        header.add(titulo);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 16, 0, 16),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 220, 240)),
                        new EmptyBorder(16, 16, 16, 16))));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtCodigo = new JTextField(10);
        txtNombre = new JTextField(18);
        txtPrecio = new JTextField(10);
        txtStock  = new JTextField(8);
        txtExtra  = new JTextField(15);

        String[] tipos = {"General (IVA 13%)", "Alimenticio (IVA 1%)", "Electrónico (IVA 15%)"};
        cmbTipo = new JComboBox<>(tipos);
        cmbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbTipo.addActionListener(e -> actualizarLabelExtra());

        lblExtra = new JLabel("N/A:");

        agregarCampo(form, gbc, "Código:", txtCodigo, 0, 0);
        agregarCampo(form, gbc, "Nombre:", txtNombre, 2, 0);
        agregarCampo(form, gbc, "Precio (₡):", txtPrecio, 4, 0);
        agregarCampo(form, gbc, "Stock:", txtStock, 0, 1);

        // Tipo de producto
        gbc.gridwidth = 1; gbc.gridx = 2; gbc.gridy = 1;
        form.add(new JLabel("Tipo:") {{ setFont(new Font("Segoe UI", Font.BOLD, 11)); }}, gbc);
        gbc.gridx = 3; gbc.gridwidth = 2;
        form.add(cmbTipo, gbc);

        // Campo extra según tipo
        gbc.gridwidth = 1; gbc.gridx = 4; gbc.gridy = 1; // queda debajo
        gbc.gridx = 0; gbc.gridy = 2;
        lblExtra.setFont(new Font("Segoe UI", Font.BOLD, 11));
        form.add(lblExtra, gbc);
        gbc.gridx = 1;
        form.add(txtExtra, gbc);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setBackground(Color.WHITE);
        btnLimpiar = crearBoton("Limpiar", new Color(120, 130, 160));
        btnGuardar = crearBoton("Guardar Producto", new Color(40, 160, 100));
        botones.add(btnLimpiar);
        botones.add(btnGuardar);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 6;
        form.add(botones, gbc);

        // Tabla
        String[] cols = {"Código", "Nombre", "Tipo", "Precio Base", "Precio Final", "Stock"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(40, 160, 100));
        tabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(0, 16, 16, 16));

        btnGuardar.addActionListener(e -> guardarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(245, 248, 255));
        top.add(header, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        actualizarLabelExtra();
        refrescar();
    }

    private void actualizarLabelExtra() {
        int idx = cmbTipo.getSelectedIndex();
        if (idx == 1) lblExtra.setText("Categoría:");
        else if (idx == 2) lblExtra.setText("Meses garantía:");
        else { lblExtra.setText("N/A:"); txtExtra.setText(""); txtExtra.setEnabled(false); return; }
        txtExtra.setEnabled(true);
    }

    private void agregarCampo(JPanel p, GridBagConstraints gbc,
                               String lbl, JTextField fld, int col, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = col; gbc.gridy = row;
        JLabel label = new JLabel(lbl);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        p.add(label, gbc);
        gbc.gridx = col + 1;
        fld.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(fld, gbc);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void guardarProducto() {
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String precioStr = txtPrecio.getText().trim();
            String stockStr = txtStock.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);
            int tipo = cmbTipo.getSelectedIndex();

            Producto p;
            if (tipo == 1) {
                String cat = txtExtra.getText().trim().isEmpty() ? "General" : txtExtra.getText().trim();
                p = new ProductoAlimenticio(codigo, nombre, precio, stock, cat);
            } else if (tipo == 2) {
                int meses = txtExtra.getText().trim().isEmpty() ? 12 : Integer.parseInt(txtExtra.getText().trim());
                p = new ProductoElectronico(codigo, nombre, precio, stock, meses);
            } else {
                p = new ProductoGeneral(codigo, nombre, precio, stock);
            }

            if (AlmacenRemoto.getInstance().buscarProducto(codigo) != null) {
                AlmacenRemoto.getInstance().actualizarProducto(p);
                JOptionPane.showMessageDialog(this, "Producto actualizado.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                AlmacenRemoto.getInstance().agregarProducto(p);
                JOptionPane.showMessageDialog(this, "Producto registrado.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            limpiarFormulario();
            refrescar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y stock deben ser numéricos.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (RegistroDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText(""); txtNombre.setText(""); txtPrecio.setText("");
        txtStock.setText(""); txtExtra.setText("");
        tabla.clearSelection();
    }

    public void refrescar() {
        modeloTabla.setRowCount(0);
        List<Producto> lista = AlmacenRemoto.getInstance().getProductos();
        for (Producto p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getCodigo(), p.getNombre(), p.getTipoProducto(),
                String.format("₡%.2f", p.getPrecio()),
                String.format("₡%.2f", p.calcularPrecioFinal()),
                p.getStock()
            });
        }
    }
}
