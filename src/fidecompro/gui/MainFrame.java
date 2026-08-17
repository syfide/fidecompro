package fidecompro.gui;

import fidecompro.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ventana principal con menú de navegación lateral.
 * Demuestra: GUI Swing, CardLayout
 */
public class MainFrame extends JFrame {

    private Usuario usuarioActual;
    private JPanel panelContenido;
    private CardLayout cardLayout;

    // Paneles de cada sección
    private ClientePanel panelClientes;
    private ProductoPanel panelProductos;
    private FacturaPanel panelFacturas;
    private DashboardPanel panelDashboard;

    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        setTitle("Fidecompro — " + usuario.getNombreCompleto() + " [" + usuario.getRol() + "]");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inicializarUI();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout());

        // ── Barra lateral ────────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(22, 40, 70));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Logo / título
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(new Color(15, 28, 55));
        logoPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        JLabel lblLogo = new JLabel("🏪 FIDECOMPRO");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLogo.setForeground(Color.WHITE);
        logoPanel.add(lblLogo);

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(10));

        // Botones de navegación
        sidebar.add(crearBotonNav("🏠  Inicio", "dashboard"));
        sidebar.add(crearBotonNav("👥  Clientes", "clientes"));
        sidebar.add(crearBotonNav("📦  Productos", "productos"));
        sidebar.add(crearBotonNav("🧾  Facturas", "facturas"));
        sidebar.add(Box.createVerticalGlue());

        // Info usuario
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(15, 28, 55));
        userPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblUser = new JLabel("👤 " + usuarioActual.getNombreCompleto());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUser.setForeground(new Color(180, 210, 255));
        JLabel lblRol = new JLabel(usuarioActual.getRol());
        lblRol.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblRol.setForeground(new Color(130, 160, 210));
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSalir.setBackground(new Color(180, 50, 50));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        userPanel.add(lblUser);
        userPanel.add(Box.createVerticalStrut(2));
        userPanel.add(lblRol);
        userPanel.add(Box.createVerticalStrut(8));
        userPanel.add(btnSalir);
        sidebar.add(userPanel);

        // ── Contenido ────────────────────────────────────────────────────────
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(new Color(245, 248, 255));

        panelDashboard = new DashboardPanel();
        panelClientes = new ClientePanel();
        panelProductos = new ProductoPanel();
        panelFacturas = new FacturaPanel(usuarioActual);

        panelContenido.add(panelDashboard, "dashboard");
        panelContenido.add(panelClientes, "clientes");
        panelContenido.add(panelProductos, "productos");
        panelContenido.add(panelFacturas, "facturas");

        add(sidebar, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        cardLayout.show(panelContenido, "dashboard");
    }

    private JButton crearBotonNav(String texto, String card) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(200, 220, 255));
        btn.setBackground(new Color(22, 40, 70));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(40, 65, 110));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(22, 40, 70));
            }
        });
        btn.addActionListener(e -> {
            cardLayout.show(panelContenido, card);
            if ("clientes".equals(card)) panelClientes.refrescar();
            if ("productos".equals(card)) panelProductos.refrescar();
            if ("facturas".equals(card)) panelFacturas.refrescar();
            if ("dashboard".equals(card)) panelDashboard.refrescar();
        });
        return btn;
    }
}
