package Interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.imageio.ImageIO;
import Packages.PaqueteAutenticar;
import cliente.Cliente;

class BotonRedondo extends JButton {
    private static final int ANCHO_ARCO = 20;
    private static final int ALTO_ARCO = 20;
    private final Color colorFondo;
    
    public BotonRedondo(String texto, Color fondo, Color textoColor) {
        super(texto);
        this.colorFondo = fondo;
        setFont(new Font("Segoe UI", Font.BOLD, 20));
        setForeground(textoColor);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        setPreferredSize(new Dimension(400, 60));
        setMinimumSize(new Dimension(400, 60));
        setMaximumSize(new Dimension(400, 60));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                setBackground(colorMasClaro(colorFondo, 30));
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                setBackground(colorFondo);
                repaint();
            }
        });
    }
    
    private Color colorMasClaro(Color color, int cantidad) {
        return new Color(
            Math.min(color.getRed() + cantidad, 255),
            Math.min(color.getGreen() + cantidad, 255),
            Math.min(color.getBlue() + cantidad, 255)
        );
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            if (getModel().isPressed()) {
                g2.setColor(colorFondo.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(colorFondo.brighter());
            } else {
                g2.setColor(colorFondo);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ANCHO_ARCO, ALTO_ARCO);
            super.paintComponent(g2);
        } finally {
            g2.dispose();
        }
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // No pintar borde para mejorar rendimiento
    }
}

public class IniciarSesion extends JFrame {
    private static final int ANCHO = 1280;
    private static final int ALTO = 675;
    private static final Color COLOR_MORADO = new Color(103, 58, 183);
    private static final Color FONDO_OSCURO = new Color(30, 30, 30);
    private static final Color FONDO_INPUT = new Color(64, 68, 75, 255);
    private static final Font FUENTE_MODERNA = new Font("Segoe UI", Font.PLAIN, 18);
    
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private BufferedImage imagenFondo;
    private Point clickInicial;

    public IniciarSesion() {
        setTitle("CollabChat - Inicio de Sesión");
        setSize(ANCHO, ALTO);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, ANCHO, ALTO, 30, 30));
        
        cargarImagenFondo();
        inicializarComponentes();
    }

    // Método nuevo añadido para reintentos de autenticación
    public void reintentarAutenticacion() {
        campoContrasena.setText("");
        campoContrasena.requestFocus();
    }

    private void cargarImagenFondo() {
        try {
            imagenFondo = ImageIO.read(getClass().getResource("/resources/logo.png"));
            imagenFondo = escalarImagen(imagenFondo, ANCHO, ALTO);
        } catch (Exception e) {
            e.printStackTrace();
            imagenFondo = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = imagenFondo.createGraphics();
            g2d.setColor(new Color(54, 57, 63));
            g2d.fillRect(0, 0, ANCHO, ALTO);
            g2d.dispose();
        }
    }
    
    private BufferedImage escalarImagen(BufferedImage original, int ancho, int alto) {
        BufferedImage escalada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = escalada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, ancho, alto, null);
        g2d.dispose();
        return escalada;
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, this);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setOpaque(false);

        agregarBarraSuperior(panelPrincipal);
        agregarFormularioLogin(panelPrincipal);
        
        setContentPane(panelPrincipal);
    }

    private void agregarBarraSuperior(JPanel panelPrincipal) {
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setPreferredSize(new Dimension(ANCHO, 40));
        barraSuperior.setBackground(FONDO_OSCURO);
        barraSuperior.setOpaque(true);

        JLabel titulo = new JLabel("  LOGIN");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);

        JButton botonCerrar = crearBotonCerrar();
        configurarListenerArrastre(barraSuperior);

        barraSuperior.add(titulo, BorderLayout.WEST);
        barraSuperior.add(botonCerrar, BorderLayout.EAST);
        panelPrincipal.add(barraSuperior, BorderLayout.NORTH);
    }

    private JButton crearBotonCerrar() {
        JButton botonCerrar = new JButton("X");
        botonCerrar.setFocusPainted(false);
        botonCerrar.setBorderPainted(false);
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setBackground(COLOR_MORADO);
        botonCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonCerrar.setPreferredSize(new Dimension(45, 40));
        botonCerrar.addActionListener(e -> System.exit(0));
        return botonCerrar;
    }

    private void configurarListenerArrastre(JComponent componente) {
        componente.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickInicial = e.getPoint();
            }
        });

        componente.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = getLocation().x + e.getX() - clickInicial.x;
                int y = getLocation().y + e.getY() - clickInicial.y;
                setLocation(x, y);
            }
        });
    }

    private void agregarFormularioLogin(JPanel panelPrincipal) {
        JPanel panelCajaNegra = crearPanelCajaNegra();
        
        JLabel tituloLogin = new JLabel("LOGIN", SwingConstants.CENTER);
        tituloLogin.setFont(new Font("Segoe UI", Font.BOLD, 36));
        tituloLogin.setForeground(COLOR_MORADO);
        tituloLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLogin.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panelCajaNegra.add(tituloLogin);

        panelCajaNegra.add(crearPanelCampos());
        panelCajaNegra.add(crearPanelBotones());

        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);
        panelCentral.add(panelCajaNegra);
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelCajaNegra() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                try {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(30, 30, 30, 240));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                } finally {
                    g2d.dispose();
                }
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(500, 550));
        panel.setMinimumSize(new Dimension(500, 550));
        panel.setMaximumSize(new Dimension(500, 550));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        return panel;
    }

    private JPanel crearPanelCampos() {
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setOpaque(false);
        panelCampos.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        campoUsuario = crearCampoTextoEstilizado();
        panelCampos.add(crearPanelEntrada("Usuario:", campoUsuario));
        panelCampos.add(Box.createRigidArea(new Dimension(0, 20)));

        campoContrasena = crearCampoContrasenaEstilizado();
        panelCampos.add(crearPanelEntrada("Contraseña:", campoContrasena));
        panelCampos.add(Box.createRigidArea(new Dimension(0, 10)));

        panelCampos.add(crearPanelOlvideContrasena());
        
        return panelCampos;
    }

    private JPanel crearPanelEntrada(String textoEtiqueta, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel etiqueta = new JLabel(textoEtiqueta);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(FUENTE_MODERNA);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(campo);
        
        return panel;
    }

    private JTextField crearCampoTextoEstilizado() {
        JTextField campo = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(FONDO_INPUT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        
        campo.setFont(FUENTE_MODERNA);
        campo.setForeground(Color.WHITE);
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 33, 36)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        campo.setCaretColor(Color.WHITE);
        campo.setPreferredSize(new Dimension(400, 50));
        campo.setMaximumSize(new Dimension(400, 50));
        return campo;
    }

    private JPasswordField crearCampoContrasenaEstilizado() {
        JPasswordField campo = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(FONDO_INPUT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        
        campo.setFont(FUENTE_MODERNA);
        campo.setForeground(Color.WHITE);
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 33, 36)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        campo.setCaretColor(Color.WHITE);
        campo.setPreferredSize(new Dimension(400, 50));
        campo.setMaximumSize(new Dimension(400, 50));
        campo.setEchoChar('•');
        return campo;
    }

    private JPanel crearPanelOlvideContrasena() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(400, 30));
        
        JLabel enlace = new JLabel("<html><span style='color:#6495ED;'>¿Olvidaste tu contraseña?</span></html>");
        enlace.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enlace.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                new RecuperarContrasena(IniciarSesion.this).setVisible(true);
            }
            
            @Override
            public void mouseEntered(MouseEvent evt) {
                enlace.setText("<html><span style='color:#6495ED;text-decoration:underline;'>¿Olvidaste tu contraseña?</span></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                enlace.setText("<html><span style='color:#6495ED;'>¿Olvidaste tu contraseña?</span></html>");
            }
        });
        panel.add(enlace);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        BotonRedondo botonLogin = new BotonRedondo("INICIAR SESIÓN", COLOR_MORADO, Color.WHITE);
        botonLogin.addActionListener(this::intentarLogin);
        
        BotonRedondo botonRegistro = new BotonRedondo("REGISTRARSE", COLOR_MORADO, Color.WHITE);
        botonRegistro.addActionListener(e -> mostrarVentanaRegistro(e)); 

        panel.add(botonLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(botonRegistro);

        return panel;
    }

    private void intentarLogin(ActionEvent evt) {
        String usuario = campoUsuario.getText();
        char[] contrasena = campoContrasena.getPassword();
        
        if (usuario.isEmpty() || contrasena.length == 0) {
            mostrarError("Por favor ingrese usuario y contraseña");
            reintentarAutenticacion();
            return;
        }
        
        PaqueteAutenticar paquete = new PaqueteAutenticar();
        paquete.nombreUsuario = usuario;
        paquete.contraseña = new String(contrasena);
        
        Cliente.cliente.sendTCP(paquete);
        
        Arrays.fill(contrasena, '0');
    }

    private void mostrarVentanaRegistro(ActionEvent evt) {
        Registro Registro = new Registro(this);
        Registro.setVisible(true);
        this.setVisible(false);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
            mensaje,
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void limpiarCampos() {
        campoUsuario.setText("");
        campoContrasena.setText("");
        campoUsuario.requestFocus();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IniciarSesion ventana = new IniciarSesion();
            ventana.setVisible(true);
        });
    }
}