package Interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicScrollBarUI;
import Packages.PaqueteRegistro;
import cliente.Cliente;
import java.util.Arrays;

public class Registro extends JFrame {
    private static final int ANCHO = 1280;
    private static final int ALTO = 675;
    private static final Color COLOR_MORADO = new Color(103, 58, 183);
    private static final Color FONDO_OSCURO = new Color(30, 30, 30);
    private static final Color FONDO_INPUT = new Color(64, 68, 75, 255); // Cambiado a 255 para opacidad completa
    private static final Font FUENTE_MODERNA = new Font("Segoe UI", Font.PLAIN, 18);
    
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JTextField campoEdad;
    private JTextField campoCiudad;
    private final IniciarSesion padre;
    private BufferedImage imagenFondo;
    private Point clickInicial;

    public Registro(IniciarSesion padre) {
        this.padre = padre;
        configurarMarco();
        cargarImagenFondo();
        inicializarComponentes();
    }

    public Registro() {
        this(null);
    }

    private void configurarMarco() {
        setTitle("CollabChat - Registro");
        setSize(ANCHO, ALTO);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, ANCHO, ALTO, 30, 30));
    }

    private void cargarImagenFondo() {
        try {
            imagenFondo = ImageIO.read(getClass().getResource("/resources/logo.png"));
            imagenFondo = escalarImagen(imagenFondo, ANCHO, ALTO);
        } catch (Exception e) {
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
        JPanel panelPrincipal = crearPanelPrincipal();
        agregarBarraSuperior(panelPrincipal);
        agregarFormularioRegistro(panelPrincipal);
        setContentPane(panelPrincipal);
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, this);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private void agregarBarraSuperior(JPanel panelPrincipal) {
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setPreferredSize(new Dimension(ANCHO, 40));
        barraSuperior.setBackground(FONDO_OSCURO);
        
        JLabel titulo = new JLabel("  REGISTRO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);

        JButton botonCerrar = crearBotonCerrar();
        configurarListenerArrastre(barraSuperior);

        barraSuperior.add(titulo, BorderLayout.WEST);
        barraSuperior.add(botonCerrar, BorderLayout.EAST);
        panelPrincipal.add(barraSuperior, BorderLayout.NORTH);
    }

    private JButton crearBotonCerrar() {
        JButton boton = new JButton("X");
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_MORADO);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setPreferredSize(new Dimension(45, 40));
        boton.addActionListener(e -> dispose());
        return boton;
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

    private void agregarFormularioRegistro(JPanel panelPrincipal) {
        JPanel panelFormulario = crearPanelFormulario();
        JScrollPane panelDesplazamiento = crearPanelDesplazamiento(panelFormulario);
        
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);
        panelCentral.add(panelDesplazamiento);
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
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
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 40));

        agregarTitulo(panel);
        agregarCamposFormulario(panel);
        agregarBotones(panel);

        return panel;
    }

    private void agregarTitulo(JPanel panel) {
        JLabel titulo = new JLabel("REGISTRO", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titulo.setForeground(COLOR_MORADO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panel.add(titulo);
    }

    private void agregarCamposFormulario(JPanel panel) {
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setOpaque(false);
        panelCampos.setAlignmentX(Component.CENTER_ALIGNMENT);

        int espacioExtra = 30;

        this.campoUsuario = crearCampoTextoEstilizado();
        agregarCampoFormulario(panelCampos, "Usuario:", this.campoUsuario);
        panelCampos.add(Box.createRigidArea(new Dimension(0, espacioExtra)));

        this.campoContrasena = crearCampoContrasenaEstilizado();
        agregarCampoFormulario(panelCampos, "Contraseña:", this.campoContrasena);
        panelCampos.add(Box.createRigidArea(new Dimension(0, espacioExtra)));

        this.campoEdad = crearCampoTextoEstilizado();
        agregarCampoFormulario(panelCampos, "Edad:", this.campoEdad);
        panelCampos.add(Box.createRigidArea(new Dimension(0, espacioExtra)));

        this.campoCiudad = crearCampoTextoEstilizado();
        agregarCampoFormulario(panelCampos, "Ciudad de Nacimiento:", this.campoCiudad);

        panel.add(panelCampos);
    }

    private void agregarCampoFormulario(JPanel panelPadre, String textoEtiqueta, JComponent campo) {
        JPanel panelCampo = new JPanel();
        panelCampo.setLayout(new BoxLayout(panelCampo, BoxLayout.Y_AXIS));
        panelCampo.setOpaque(false);
        panelCampo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel etiqueta = new JLabel(textoEtiqueta);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(FUENTE_MODERNA);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCampo.add(etiqueta);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 5)));

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCampo.add(campo);
        panelPadre.add(panelCampo);
    }

    private JTextField crearCampoTextoEstilizado() {
        JTextField campo = new JTextField() {
            @Override
            public void paint(Graphics g) {
                // Primero pintamos el fondo
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Luego pintamos el texto y borde
                super.paint(g);
            }
        };
        campo.setOpaque(false);
        campo.setFont(FUENTE_MODERNA);
        campo.setForeground(Color.WHITE);
        campo.setBackground(FONDO_INPUT);
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
            public void paint(Graphics g) {
                // Primero pintamos el fondo
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Luego pintamos el texto y borde
                super.paint(g);
            }
        };
        campo.setOpaque(false);
        campo.setFont(FUENTE_MODERNA);
        campo.setForeground(Color.WHITE);
        campo.setBackground(FONDO_INPUT);
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

    private void agregarBotones(JPanel panel) {
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        BotonRedondo botonRegistro = new BotonRedondo("REGISTRARSE", COLOR_MORADO, Color.WHITE);
        botonRegistro.addActionListener(e -> registrarUsuario());

        BotonRedondo botonVolver = new BotonRedondo("VOLVER AL LOGIN", COLOR_MORADO, Color.WHITE);
        botonVolver.addActionListener(e -> volverALogin());

        panelBotones.add(botonRegistro);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 15)));
        panelBotones.add(botonVolver);

        panel.add(panelBotones);
    }

    private JScrollPane crearPanelDesplazamiento(JPanel vista) {
        JScrollPane panelDesplazamiento = new JScrollPane(vista);
        panelDesplazamiento.setBorder(BorderFactory.createEmptyBorder());
        panelDesplazamiento.setOpaque(false);
        panelDesplazamiento.getViewport().setOpaque(false);
        panelDesplazamiento.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDesplazamiento.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelDesplazamiento.getVerticalScrollBar().setUI(new BarraDesplazamientoPersonalizadaUI());
        panelDesplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        panelDesplazamiento.setPreferredSize(new Dimension(520, 550));
        panelDesplazamiento.getViewport().setPreferredSize(new Dimension(500, 550));
        return panelDesplazamiento;
    }

    private void registrarUsuario() {
        String usuario = campoUsuario.getText().trim();
        char[] contrasena = campoContrasena.getPassword();
        String textoEdad = campoEdad.getText().trim();
        String ciudad = campoCiudad.getText().trim();
        
        // Validaciones
        if (usuario.isEmpty() || contrasena.length == 0 || textoEdad.isEmpty() || ciudad.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        
        int edad;
        try {
            edad = Integer.parseInt(textoEdad);
            if (edad <= 0) {
                mostrarError("La edad debe ser un número positivo.");
                return;
            }
        } catch (NumberFormatException ex) {
            mostrarError("Edad no válida. Debe ser un número.");
            return;
        }

        // Crear y enviar paquete
        PaqueteRegistro paquete = new PaqueteRegistro();
        paquete.nombreUsuario = usuario;
        paquete.contraseña = new String(contrasena);
        paquete.edad = edad;
        paquete.ciudadNacimiento = ciudad;

        // Enviar al servidor
        Cliente.cliente.sendTCP(paquete);
        
        // Limpiar contraseña en memoria
        Arrays.fill(contrasena, '0');
        
         // Cerrar la ventana de registro
    dispose();
    
    // Asegúrate de que la ventana de login se muestre después
    if (padre != null) {
        padre.setVisible(true);  // Hacer visible la ventana de login (si el objeto padre existe)
    }
    }

    private void volverALogin() {
        padre.setVisible(true);
        dispose();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class BarraDesplazamientoPersonalizadaUI extends BasicScrollBarUI {
        private final Dimension d = new Dimension();
        
        @Override 
        protected void configureScrollBarColors() {
            thumbColor = Color.WHITE;
            trackColor = new Color(30, 30, 30, 240);
        }
        
        @Override 
        protected JButton createDecreaseButton(int orientation) {
            return crearBotonCero();
        }
        
        @Override 
        protected JButton createIncreaseButton(int orientation) {
            return crearBotonCero();
        }
        
        private JButton crearBotonCero() {
            JButton boton = new JButton();
            boton.setPreferredSize(d);
            boton.setMinimumSize(d);
            boton.setMaximumSize(d);
            return boton;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D)g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x+2, thumbBounds.y, thumbBounds.width-4, thumbBounds.height, 10, 10);
            } finally {
                g2.dispose();
            }
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
        
        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(8, 50);
        }
    }

    class BotonRedondo extends JButton {
        private static final int ANCHO_ARCO = 20;
        private static final int ALTO_ARCO = 20;
        private final Color colorFondo;
        
        public BotonRedondo(String texto, Color fondo, Color colorTexto) {
            super(texto);
            this.colorFondo = fondo;
            setFont(new Font("Segoe UI", Font.BOLD, 20));
            setForeground(colorTexto);
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
    }
}