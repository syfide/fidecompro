package fidecompro.gui;

import fidecompro.excepciones.RegistroDuplicadoException;
import fidecompro.modelo.Cliente;
import fidecompro.red.AlmacenRemoto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD de Clientes.
 */
public class ClientePanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtCedula, txtNombre, txtApellido, txtTelefono, txtEmail, txtDireccion;
    private JButton btnGuardar, btnLimpiar;

    public ClientePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));
        inicializarUI();
    }

    private void inicializarUI() {
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(245, 248, 255));
        header.setBorder(new EmptyBorder(20, 24, 8, 24));
        JLabel titulo = new JLabel("👥  Gestión de Clientes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(22, 40, 70));
        header.add(titulo);

        // Formulario
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

        txtCedula    = new JTextField(12);
        txtNombre    = new JTextField(15);
        txtApellido  = new JTextField(15);
        txtTelefono  = new JTextField(12);
        txtEmail     = new JTextField(20);
        txtDireccion = new JTextField(30);

        agregarCampo(form, gbc, "Cédula:", txtCedula, 0, 0);
        agregarCampo(form, gbc, "Nombre:", txtNombre, 2, 0);
        agregarCampo(form, gbc, "Apellido:", txtApellido, 4, 0);
        agregarCampo(form, gbc, "Teléfono:", txtTelefono, 0, 1);
        agregarCampo(form, gbc, "Email:", txtEmail, 2, 1);
        agregarCampo(form, gbc, "Dirección:", txtDireccion, 4, 1);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setBackground(Color.WHITE);
        btnLimpiar = crearBoton("Limpiar", new Color(120, 130, 160));
        btnGuardar = crearBoton("Guardar Cliente", new Color(22, 40, 70));
        botones.add(btnLimpiar);
        botones.add(btnGuardar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        form.add(botones, gbc);

        // Tabla
        String[] cols = {"Cédula", "Nombre", "Apellido", "Teléfono", "Email", "Dirección"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(22, 40, 70));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(0, 16, 16, 16));

        // Acciones
        btnGuardar.addActionListener(e -> guardarCliente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(245, 248, 255));
        top.add(header, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refrescar();
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc,
                               String label, JTextField field, int col, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = col; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panel.add(lbl, gbc);
        gbc.gridx = col + 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(field, gbc);
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

    private void guardarCliente() {
        try {
            String cedula = txtCedula.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String tel = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String dir = txtDireccion.getText().trim();

            if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cédula, nombre y apellido son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Si ya existe: actualizar
            if (AlmacenRemoto.getInstance().buscarCliente(cedula) != null) {
                Cliente c = new Cliente(cedula, nombre, apellido, tel, email, dir);
                AlmacenRemoto.getInstance().actualizarCliente(c);
                JOptionPane.showMessageDialog(this, "Cliente actualizado.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                Cliente c = new Cliente(cedula, nombre, apellido, tel, email, dir);
                AlmacenRemoto.getInstance().agregarCliente(c);
                JOptionPane.showMessageDialog(this, "Cliente registrado.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            limpiarFormulario();
            refrescar();
        } catch (RegistroDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtCedula.setText((String) modeloTabla.getValueAt(fila, 0));
        txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        txtApellido.setText((String) modeloTabla.getValueAt(fila, 2));
        txtTelefono.setText((String) modeloTabla.getValueAt(fila, 3));
        txtEmail.setText((String) modeloTabla.getValueAt(fila, 4));
        txtDireccion.setText((String) modeloTabla.getValueAt(fila, 5));
    }

    private void limpiarFormulario() {
        txtCedula.setText(""); txtNombre.setText(""); txtApellido.setText("");
        txtTelefono.setText(""); txtEmail.setText(""); txtDireccion.setText("");
        tabla.clearSelection();
    }

    public void refrescar() {
        modeloTabla.setRowCount(0);
        List<Cliente> lista = AlmacenRemoto.getInstance().getClientes();
        for (Cliente c : lista) {
            modeloTabla.addRow(new Object[]{
                c.getCedula(), c.getNombre(), c.getApellido(),
                c.getTelefono(), c.getEmail(), c.getDireccion()
            });
        }
    }
}
