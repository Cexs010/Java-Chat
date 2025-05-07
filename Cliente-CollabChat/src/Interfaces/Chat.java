package Interfaces;

import Packages.PacketMessage;
import cliente.Cliente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chat extends JFrame {
    private static final Color COLOR_MORADO = new Color(103, 58, 183);
    private static final Color COLOR_SUAVE = new Color(245, 245, 245);
    private static final Color FONDO_INPUT = new Color(64, 68, 75);
    private static final Font FUENTE = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FUENTE_PEQUENA = new Font("Segoe UI", Font.ITALIC, 12);

    private JTextField campoMensaje;
    private JPanel mensajesPanel;
    private JPanel sidebar;
    private final String usuarioActual;
    private final Cliente cliente;
    private final Map<String, Color> coloresUsuarios = new HashMap<>();

    public Chat(String usuario, Cliente cliente) {
        this.usuarioActual = usuario;
        this.cliente = cliente;
        coloresUsuarios.put(usuario, generarColorDesdeNombre(usuario));
        configurarVentana();
        initUI();
    }

    public Chat(String usuario, Object obj) {
        this(usuario, (obj instanceof Cliente) ? (Cliente) obj : null);
    }

    private void configurarVentana() {
        setTitle("Chat Colaborativo - " + usuarioActual);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        crearSidebar();
        crearPanelPrincipal();
    }

    private void crearSidebar() {
        sidebar = new JPanel();
        sidebar.setBackground(COLOR_MORADO);
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel lblUsuarios = new JLabel("Usuarios conectados", SwingConstants.CENTER);
        lblUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUsuarios.setForeground(Color.WHITE);
        lblUsuarios.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsuarios.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(lblUsuarios);

        add(sidebar, BorderLayout.WEST);
    }

    private void crearPanelPrincipal() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_SUAVE);

        mensajesPanel = new JPanel();
        mensajesPanel.setLayout(new BoxLayout(mensajesPanel, BoxLayout.Y_AXIS));
        mensajesPanel.setBackground(COLOR_SUAVE);
        mensajesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(mensajesPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(crearPanelInput(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel crearPanelInput() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(COLOR_SUAVE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        campoMensaje = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };
        campoMensaje.setOpaque(false);
        campoMensaje.setFont(FUENTE);
        campoMensaje.setForeground(Color.WHITE);
        campoMensaje.setCaretColor(Color.WHITE);
        campoMensaje.setBackground(FONDO_INPUT);
        campoMensaje.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        campoMensaje.setPreferredSize(new Dimension(400, 40));

        JButton btnAdjuntar = new JButton();
        btnAdjuntar.setIcon(new ImageIcon(getClass().getResource("/resources/adjuntar.png")));
        btnAdjuntar.setContentAreaFilled(false);
        btnAdjuntar.setBorderPainted(false);
        btnAdjuntar.setFocusPainted(false);

        ImageIcon adjuntarIcon = (ImageIcon) btnAdjuntar.getIcon();
        int ancho = adjuntarIcon.getIconWidth();
        int alto = adjuntarIcon.getIconHeight();

        ImageIcon flechaIcon = new ImageIcon(
            new ImageIcon(getClass().getResource("/resources/flecha.png"))
                .getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH)
        );
        
        JButton btnEnviar = new JButton(flechaIcon);
        btnEnviar.setContentAreaFilled(false);
        btnEnviar.setBorderPainted(false);
        btnEnviar.setFocusPainted(false);
        btnEnviar.addActionListener(e -> enviarMensaje());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);
        botones.add(btnAdjuntar);
        botones.add(btnEnviar);

        inputPanel.add(campoMensaje, BorderLayout.CENTER);
        inputPanel.add(botones, BorderLayout.EAST);

        configurarEventosTeclado();

        return inputPanel;
    }

    private void configurarEventosTeclado() {
        campoMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                    e.consume();
                }
            }
        });
    }

    private JPanel crearBurbujaUsuario(String nombre) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBackground(new Color(120, 90, 200));
    panel.setMaximumSize(new Dimension(180, 40));
    panel.setPreferredSize(new Dimension(180, 40));
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel contenido = new JPanel();
    contenido.setLayout(new BoxLayout(contenido, BoxLayout.X_AXIS));
    contenido.setOpaque(false);
    contenido.setAlignmentY(Component.CENTER_ALIGNMENT);

    JLabel circulo = new JLabel("●");
    circulo.setForeground(new Color(0, 200, 0)); // Verde para conectado
    circulo.setFont(new Font("Segoe UI", Font.BOLD, 14));

    JLabel label = new JLabel(nombre);
    label.setForeground(Color.WHITE);
    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

    contenido.add(circulo);
    contenido.add(label);
    panel.add(contenido);

    JPanel wrapper = new JPanel();
    wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
    wrapper.setOpaque(false);
    wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    wrapper.add(panel);

    return wrapper;
}



    public JPanel crearBurbujaMensaje(String usuario, String texto, boolean esPropio, String horaEnvio) {
        Color colorUsuario = obtenerColorUsuario(usuario);
        
        JLabel lblUsuario = new JLabel(usuario);
        lblUsuario.setFont(FUENTE_PEQUENA);
        lblUsuario.setForeground(Color.GRAY);

        JLabel lblMensaje = new JLabel("<html><div style='width: auto;'>" + texto + "</div></html>");
        lblMensaje.setFont(FUENTE);
        lblMensaje.setForeground(Color.BLACK);
        lblMensaje.setOpaque(true);
        lblMensaje.setBackground(colorUsuario);
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblHora = new JLabel(horaEnvio);
        lblHora.setFont(FUENTE_PEQUENA);
        lblHora.setForeground(Color.GRAY);

        JPanel alineado = new JPanel();
        alineado.setLayout(new BoxLayout(alineado, BoxLayout.Y_AXIS));
        alineado.setOpaque(false);

        if (esPropio) {
            alineado.setAlignmentX(Component.RIGHT_ALIGNMENT);
        } else {
            alineado.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        alineado.add(lblUsuario);
        alineado.add(lblMensaje);
        alineado.add(lblHora);

        JPanel contenedorFinal = new JPanel();
        contenedorFinal.setLayout(new BoxLayout(contenedorFinal, BoxLayout.Y_AXIS));
        contenedorFinal.setOpaque(false);
        contenedorFinal.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        contenedorFinal.add(alineado);

        return contenedorFinal;
    }

    private Color obtenerColorUsuario(String usuario) {
        if (!coloresUsuarios.containsKey(usuario)) {
            coloresUsuarios.put(usuario, generarColorDesdeNombre(usuario));
        }
        return coloresUsuarios.get(usuario);
    }

    private Color generarColorDesdeNombre(String nombre) {
        int hash = nombre.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;
        
        // Asegurar colores visibles (55-255)
        r = 155 + Math.abs(r % 100);
        g = 155 + Math.abs(g % 100);
        b = 155 + Math.abs(b % 100);

        
        return new Color(r, g, b);
    }

    public void recibirMensaje(PacketMessage packet) {
        boolean esPropio = packet.nombreUsuario.equals(usuarioActual);
        SwingUtilities.invokeLater(() -> {
            String horaEnvio = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());
            JPanel burbuja = crearBurbujaMensaje(
                packet.nombreUsuario,
                packet.mensaje,
                esPropio,
                horaEnvio
            );
            mensajesPanel.add(burbuja);
            mensajesPanel.revalidate();
            mensajesPanel.repaint();
        });
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (!texto.isEmpty()) {
            PacketMessage packet = new PacketMessage();
            packet.mensaje = texto;
            packet.nombreUsuario = usuarioActual;

            if (cliente != null && cliente.isConnected()) {
                cliente.sendTCP(packet);
            } else {
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor");
            }
            campoMensaje.setText("");
        }
    }

    public void agregarMensaje(String usuario, String texto, boolean esPropio, String hora) {
        JPanel burbuja = crearBurbujaMensaje(usuario, texto, esPropio, hora);
        mensajesPanel.add(burbuja);
        mensajesPanel.revalidate();
        mensajesPanel.repaint();

        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(
            JScrollPane.class, mensajesPanel);
        if (scrollPane != null) {
            scrollPane.getVerticalScrollBar().setValue(
                scrollPane.getVerticalScrollBar().getMaximum());
        }
    }

    public void agregarUsuarioActivo(String nombre) {
        SwingUtilities.invokeLater(() -> {
            obtenerColorUsuario(nombre);
            sidebar.add(crearBurbujaUsuario(nombre));
            sidebar.revalidate();
            sidebar.repaint();
        });
    }

    public void limpiarUsuarios() {
        SwingUtilities.invokeLater(() -> {
            Component[] comps = sidebar.getComponents();
            for (int i = comps.length - 1; i > 0; i--) {
                sidebar.remove(comps[i]);
            }
            sidebar.revalidate();
            sidebar.repaint();
        });
    }
}