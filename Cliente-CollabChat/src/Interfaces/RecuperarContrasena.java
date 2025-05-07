package Interfaces;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicScrollBarUI;
import Packages.PaqueteAutenticarContrasena;
import java.util.Arrays;
import cliente.Cliente;

public class RecuperarContrasena extends JFrame {
    private JTextField campoNombre;
    private JTextField campoEdad;
    private JTextField campoCiudad;
    private JPasswordField campoNuevaContrasena;
    private JPasswordField campoConfirmarContrasena;
    private JButton botonRecuperar;
    private JButton botonCancelar;
    private Image imagenFondo;
    private IniciarSesion padre;
    private Point clickInicial;

    public RecuperarContrasena(IniciarSesion padre) {
        this.padre = padre;
        setTitle("CollabChat - Recuperar Contraseña");
        setSize(1280, 675);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1280, 675, 30, 30));

        try {
            imagenFondo = ImageIO.read(getClass().getResource("/resources/logo.png"));
        } catch (Exception e) {
            imagenFondo = new BufferedImage(1280, 675, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) imagenFondo.getGraphics();
            g2d.setColor(new Color(54, 57, 63));
            g2d.fillRect(0, 0, 1280, 675);
            g2d.dispose();
        }

        inicializarComponentes();
        configurarComponentes();
    }

    public RecuperarContrasena() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private static class BarraDesplazamiento extends BasicScrollBarUI {
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

    private void inicializarComponentes() {
        Font fuenteModerna = new Font("Segoe UI", Font.PLAIN, 18);
        Color fondoInput = new Color(64, 68, 75, 220);
        Color colorTexto = Color.WHITE;
        Color colorMorado = new Color(103, 58, 183);

        // Panel principal con fondo
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setOpaque(false);

        // Barra superior personalizada
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setPreferredSize(new Dimension(getWidth(), 40));
        barraSuperior.setBackground(new Color(30, 30, 30));
        barraSuperior.setOpaque(true);

        JLabel etiquetaTitulo = new JLabel("  RECUPERAR CONTRASEÑA");
        etiquetaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiquetaTitulo.setForeground(Color.WHITE);

        JButton botonCerrar = new JButton("X");
        botonCerrar.setFocusPainted(false);
        botonCerrar.setBorderPainted(false);
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setBackground(new Color(103, 58, 183));
        botonCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonCerrar.setPreferredSize(new Dimension(45, 40));
        botonCerrar.addActionListener(e -> dispose());

        // Movimiento de la ventana al arrastrar
        barraSuperior.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                clickInicial = e.getPoint();
                getComponentAt(clickInicial);
            }
        });

        barraSuperior.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int posX = getLocation().x;
                int posY = getLocation().y;

                int xMovido = e.getX() - clickInicial.x;
                int yMovido = e.getY() - clickInicial.y;

                int X = posX + xMovido;
                int Y = posY + yMovido;

                setLocation(X, Y);
            }
        });

        barraSuperior.add(etiquetaTitulo, BorderLayout.WEST);
        barraSuperior.add(botonCerrar, BorderLayout.EAST);
        panelPrincipal.add(barraSuperior, BorderLayout.NORTH);

        // Panel para el cuadro negro con scroll
        JPanel panelCajaNegra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 30, 30, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.dispose();
            }
        };
        panelCajaNegra.setLayout(new BoxLayout(panelCajaNegra, BoxLayout.Y_AXIS));
        panelCajaNegra.setOpaque(false);
        panelCajaNegra.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 40));

        // Título centrado
        JLabel tituloRecuperacion = new JLabel("RECUPERAR CONTRASEÑA", SwingConstants.CENTER);
        tituloRecuperacion.setFont(new Font("Segoe UI", Font.BOLD, 36));
        tituloRecuperacion.setForeground(colorMorado);
        tituloRecuperacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloRecuperacion.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panelCajaNegra.add(tituloRecuperacion);

        // Panel para campos de texto
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setOpaque(false);
        panelCampos.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadir componentes con espacio
        int espacioExtra = 20;
        
        // Nombre
        agregarCampoFormulario(panelCampos, "Nombre:", campoNombre = new JTextField(), fuenteModerna, fondoInput, colorTexto);
        panelCampos.add(Box.createRigidArea(new Dimension(0, espacioExtra)));
        
        // Edad
        agregarCampoFormulario(panelCampos, "Edad:", campoEdad = new JTextField(), fuenteModerna, fondoInput, colorTexto);
        panelCampos.add(Box.createRigidArea(new Dimension(0, espacioExtra)));
        
        // Ciudad
        agregarCampoFormulario(panelCampos, "Ciudad:", campoCiudad = new JTextField(), fuenteModerna, fondoInput, colorTexto);
        panelCampos.add(Box.createRigidArea(new Dimension(0, espacioExtra)));
        
        // Nueva Contraseña
        agregarCampoFormulario(panelCampos, "Nueva Contraseña:", campoNuevaContrasena = new JPasswordField(), fuenteModerna, fondoInput, colorTexto);
        panelCampos.add(Box.createRigidArea(new Dimension(0, espacioExtra)));
        
        // Confirmar Contraseña
        agregarCampoFormulario(panelCampos, "Confirmar Contraseña:", campoConfirmarContrasena = new JPasswordField(), fuenteModerna, fondoInput, colorTexto);

        panelCajaNegra.add(panelCampos);

        // Panel para botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        botonRecuperar = new BotonRedondo("RECUPERAR CONTRASEÑA", colorMorado, Color.WHITE);
        botonCancelar = new BotonRedondo("CANCELAR", colorMorado, Color.WHITE);

        panelBotones.add(botonRecuperar);
        panelBotones.add(Box.createRigidArea(new Dimension(0, 15)));
        panelBotones.add(botonCancelar);

        panelCajaNegra.add(panelBotones);

        // Configuración del JScrollPane
        JScrollPane panelDesplazamiento = new JScrollPane(panelCajaNegra);
        panelDesplazamiento.setBorder(BorderFactory.createEmptyBorder());
        panelDesplazamiento.setOpaque(false);
        panelDesplazamiento.getViewport().setOpaque(false);
        panelDesplazamiento.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDesplazamiento.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelDesplazamiento.getVerticalScrollBar().setUI(new BarraDesplazamiento());
        panelDesplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        panelDesplazamiento.getVerticalScrollBar().setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        
        // Ajustar tamaño preferido
        panelDesplazamiento.setPreferredSize(new Dimension(520, 550));
        panelDesplazamiento.getViewport().setPreferredSize(new Dimension(500, 550));

        // Centrar el scroll pane en la ventana
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);
        panelCentral.add(panelDesplazamiento);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        setContentPane(panelPrincipal);
    }

    private void agregarCampoFormulario(JPanel panelPadre, String textoEtiqueta, JComponent campo, Font fuente, Color fondo, Color colorTexto) {
        JPanel panelCampo = new JPanel();
        panelCampo.setLayout(new BoxLayout(panelCampo, BoxLayout.Y_AXIS));
        panelCampo.setOpaque(false);
        panelCampo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel etiqueta = new JLabel(textoEtiqueta);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(fuente);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCampo.add(etiqueta);
        panelCampo.add(Box.createRigidArea(new Dimension(0, 5)));

        if (campo instanceof JPasswordField) {
            estilizarCampoContrasena((JPasswordField) campo, fuente, fondo, colorTexto);
        } else {
            estilizarCampoTexto((JTextField) campo, fuente, fondo, colorTexto);
        }

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelCampo.add(campo);
        panelPadre.add(panelCampo);
    }

    private void estilizarCampoTexto(JTextField campo, Font fuente, Color fondo, Color colorTexto) {
        campo.setFont(fuente);
        campo.setForeground(colorTexto);
        campo.setBackground(fondo);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 33, 36)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        campo.setCaretColor(Color.WHITE);
        campo.setPreferredSize(new Dimension(400, 50));
        campo.setMaximumSize(new Dimension(400, 50));
    }

    private void estilizarCampoContrasena(JPasswordField campo, Font fuente, Color fondo, Color colorTexto) {
        estilizarCampoTexto(campo, fuente, fondo, colorTexto);
        campo.setEchoChar('•');
    }

    private void configurarComponentes() {
        botonRecuperar.addActionListener(this::intentarRecuperacion);
        botonCancelar.addActionListener(e -> {
            this.dispose();
            padre.setVisible(true);
        });
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void intentarRecuperacion(ActionEvent evt) {
        String nombre = campoNombre.getText();
        String edad = campoEdad.getText();
        String ciudad = campoCiudad.getText();
        char[] nuevaContrasena = campoNuevaContrasena.getPassword();
        char[] confirmarContrasena = campoConfirmarContrasena.getPassword();

        // Validaciones básicas
        if (nombre.isEmpty() || edad.isEmpty() || ciudad.isEmpty() || 
            nuevaContrasena.length == 0 || confirmarContrasena.length == 0) {
            mostrarError("Todos los campos son obligatorios");
            return;
        }

        if (!new String(nuevaContrasena).equals(new String(confirmarContrasena))) {
            mostrarError("Las contraseñas no coinciden");
            return;
        }

        int valorEdad;
        try {
            valorEdad = Integer.parseInt(edad);
            if (valorEdad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("Edad no válida");
            return;
        }

        // Crear y enviar paquete
        PaqueteAutenticarContrasena paquete = new PaqueteAutenticarContrasena();
        paquete.nombreUsuario = nombre;
        paquete.edad = valorEdad;
        paquete.ciudad = ciudad;
        paquete.nuevaContrasena = new String(nuevaContrasena);

        // Limpiar contraseña en memoria
        Arrays.fill(nuevaContrasena, '0');
        Arrays.fill(confirmarContrasena, '0');

        // Enviar al servidor
        Cliente.cliente.sendTCP(paquete);
    }

    class BotonRedondo extends JButton {
        private Color colorFondo;
        private Color colorTexto;
        
        public BotonRedondo(String texto, Color fondo, Color colorTexto) {
            super(texto);
            this.colorFondo = fondo;
            this.colorTexto = colorTexto;
            setFont(new Font("Segoe UI", Font.BOLD, 20));
            setForeground(colorTexto);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            setPreferredSize(new Dimension(400, 60));
            setMinimumSize(new Dimension(400, 60));
            setMaximumSize(new Dimension(400, 60));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(
                        Math.min(colorFondo.getRed() + 30, 255),
                        Math.min(colorFondo.getGreen() + 30, 255),
                        Math.min(colorFondo.getBlue() + 30, 255)
                    ));
                    repaint();
                }
                
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(colorFondo);
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(colorFondo.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(colorFondo.brighter());
            } else {
                g2.setColor(colorFondo);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g);
        }
    }
}
