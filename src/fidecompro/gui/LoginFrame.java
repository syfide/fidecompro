package fidecompro.gui;

import fidecompro.excepciones.AutenticacionException;
import fidecompro.modelo.Usuario;
import fidecompro.red.AlmacenRemoto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de inicio de sesión.
 * Demuestra: GUI (Swing), Excepciones, Multihilos (SwingWorker)
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JLabel lblEstado;

    public LoginFrame() {
        setTitle("Fidecompro - Iniciar Sesión");
        setSize(420, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        inicializarUI();
    }

    private void inicializarUI() {
        // Panel principal con fondo oscuro
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(22, 40, 70));

        // Header
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelHeader.setBackground(new Color(22, 40, 70));
        panelHeader.setBorder(new EmptyBorder(30, 0, 10, 0));
        JLabel lblTitulo = new JLabel("🏪  FIDECOMPRO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblSubtitulo = new JLabel("Sistema de Facturación");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(160, 190, 230));
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(new Color(22, 40, 70));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblSubtitulo);
        panelHeader.add(textos);

        // Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(240, 245, 255));
        panelForm.setBorder(new EmptyBorder(24, 36, 24, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblU = new JLabel("Usuario:");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelForm.add(lblU, gbc);

        gbc.gridy = 1;
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        panelForm.add(txtUsuario, gbc);

        // Password
        gbc.gridy = 2;
        JLabel lblP = new JLabel("Contraseña:");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelForm.add(lblP, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        panelForm.add(txtPassword, gbc);

        // Estado
        gbc.gridy = 4;
        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblEstado.setForeground(new Color(200, 50, 50));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        panelForm.add(lblEstado, gbc);

        // Botón
        gbc.gridy = 5;
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIngresar.setBackground(new Color(22, 40, 70));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelForm.add(btnIngresar, gbc);

        // Hint
        gbc.gridy = 6;
        JLabel lblHint = new JLabel("Default: admin / admin123");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblHint.setForeground(new Color(130, 130, 160));
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        panelForm.add(lblHint, gbc);

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        panelPrincipal.add(panelForm, BorderLayout.CENTER);
        add(panelPrincipal);

        // Acciones
        btnIngresar.addActionListener(e -> autenticar());
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) autenticar();
            }
        });
    }

    private void autenticar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Ingrese usuario y contraseña.");
            return;
        }

        btnIngresar.setEnabled(false);
        lblEstado.setText("Verificando...");
        lblEstado.setForeground(new Color(30, 100, 200));

        // Multihilo con SwingWorker para no bloquear la UI
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                Thread.sleep(400); // simula latencia
                return AlmacenRemoto.getInstance().autenticar(user, pass);
            }

            @Override
            protected void done() {
                try {
                    Usuario u = get();
                    dispose();
                    new MainFrame(u).setVisible(true);
                } catch (Exception ex) {
                    lblEstado.setForeground(new Color(200, 50, 50));
                    lblEstado.setText("Usuario o contraseña incorrectos.");
                    btnIngresar.setEnabled(true);
                    txtPassword.setText("");
                }
            }
        };
        worker.execute();
    }
}
