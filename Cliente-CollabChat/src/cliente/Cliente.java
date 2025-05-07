package cliente;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import Interfaces.*;
import Packages.*;
import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class Cliente extends Listener {
    // Instancia única del cliente
    public static Cliente instancia;

    // Cliente KryoNet
    public static Client cliente;
    
    // Nueva variable de estado de conexión
    private static boolean connected = false;

    // Configuración de conexión
    private static final String IP = "localhost";
    private static final int PUERTO_TCP = 27960;

    // Interfaces gráficas
    private static IniciarSesion ventanaLogin;
    private static Registro ventanaRegistro;
    private static RecuperarContrasena ventanaRecuperacion;
    private static Chat ventanaChat;

    // Usuario actual
    private static String nombreUsuario;
    
    // Variable para manejar el historial
    private static PaqueteHistorial historialPendiente = null;
    // Variable para el manejo de usuarios activos
    private static PaqueteUsuariosActivos usuariosPendiente = null;

    // Constructor
    public Cliente() {
        instancia = this;
    }

    // Método principal
    public static void main(String[] args) {
        try {
            cliente = new Client(8192, 4096);
            registrarPaquetes();

            cliente.start();
            cliente.connect(8000, IP, PUERTO_TCP);
            cliente.addListener(new Cliente());
            connected = true; // Actualizar estado de conexión

            mostrarVentanaLogin();
        } catch (IOException e) {
            connected = false;
            mostrarErrorConexion("Error de conexión: " + e.getMessage());
        }
    }
    
    // Registro de clases que se envían por red
    private static void registrarPaquetes() {
        var kryo = cliente.getKryo();
        kryo.register(PacketMessage.class);
        kryo.register(PaqueteRegistro.class);
        kryo.register(RespuestaRegistro.class);
        kryo.register(PaqueteAutenticar.class);
        kryo.register(PaqueteAutenticarContrasena.class);
        kryo.register(RespuestaAutenticarContrasena.class);
        kryo.register(PaqueteHistorial.class);
        kryo.register(PaqueteUsuariosActivos.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(java.sql.Timestamp.class);
    }

    // Métodos para mostrar interfaces gráficas
    public static void mostrarVentanaLogin() {
        cerrarVentanas();
        SwingUtilities.invokeLater(() -> {
            ventanaLogin = new IniciarSesion();
            ventanaLogin.setVisible(true);
        });
    }

    public static void mostrarVentanaRegistro() {
        cerrarVentanas();
        SwingUtilities.invokeLater(() -> {
            ventanaRegistro = new Registro();
            ventanaRegistro.setVisible(true);
        });
    }

    public static void mostrarVentanaRecuperacion() {
        cerrarVentanas();
        SwingUtilities.invokeLater(() -> {
            ventanaRecuperacion = new RecuperarContrasena();
            ventanaRecuperacion.setVisible(true);
        });
    }

    public static void mostrarVentanaChat(String usuario) {
        cerrarVentanas();
        nombreUsuario = usuario;
        SwingUtilities.invokeLater(() -> {
            ventanaChat = new Chat(usuario, Cliente.instancia);
            ventanaChat.setVisible(true);

            if (historialPendiente != null) {
                Cliente.instancia.mostrarHistorial(historialPendiente);
                historialPendiente = null;
            }
            
            if (usuariosPendiente != null) {
                instancia.mostrarUsuarios(usuariosPendiente);
                usuariosPendiente = null;
            }
        });
    }

    // Cierra todas las ventanas abiertas
    private static void cerrarVentanas() {
        if (ventanaLogin != null) ventanaLogin.dispose();
        if (ventanaRegistro != null) ventanaRegistro.dispose();
        if (ventanaRecuperacion != null) ventanaRecuperacion.dispose();
        if (ventanaChat != null) ventanaChat.dispose();
    }

    // Envía un paquete genérico
    public static void enviarPaquete(Object paquete) {
        if (cliente != null && isConnected()) {
            cliente.sendTCP(paquete);
        }
    }

    // Recepción de datos desde el servidor
    @Override
    public void received(Connection c, Object p) {
        if (p instanceof RespuestaRegistro respuesta) {
            manejarRespuestaRegistro(respuesta);
        } else if (p instanceof PaqueteAutenticar respuesta) {
            manejarRespuestaAutenticar(respuesta);
        } else if (p instanceof RespuestaAutenticarContrasena respuesta) {
            manejarRespuestaRecuperacion(respuesta);
        } else if (p instanceof PacketMessage mensaje) {
            manejarMensajeChat(mensaje);
        } else if (p instanceof PaqueteHistorial historial) {
            manejarHistorial(historial);
        } else if (p instanceof PaqueteUsuariosActivos paquete){
            manejarPaqueteUsuarios(paquete);
        }
    }

    // Maneja la respuesta al registro de usuario
    private void manejarRespuestaRegistro(RespuestaRegistro respuesta) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, respuesta.mensaje);
            if (respuesta.respuesta) mostrarVentanaLogin();
        });
    }

    // Maneja la respuesta al intento de inicio de sesión
    private void manejarRespuestaAutenticar(PaqueteAutenticar respuesta) {
        SwingUtilities.invokeLater(() -> {
            if (respuesta.autenticado) {
                nombreUsuario = respuesta.nombreUsuario;
                mostrarVentanaChat(nombreUsuario);
            } else {
                JOptionPane.showMessageDialog(null, "Error: " + respuesta.mensajeError);
                if (ventanaLogin != null) ventanaLogin.reintentarAutenticacion();
            }
        });
    }
    
    // Maneja el envío del paquete de usuarios activos en el server
    private void manejarPaqueteUsuarios(PaqueteUsuariosActivos paquete){
        System.out.println("[Cliente] Usuarios activos: " + paquete.usuarios.values());
        if (ventanaChat == null){
            usuariosPendiente = paquete;
            return;
        }
        
        mostrarUsuarios(paquete);
    }
    
    private void mostrarUsuarios(PaqueteUsuariosActivos paquete){
        ventanaChat.limpiarUsuarios();
        for (String nombre : paquete.usuarios.values()) {
            ventanaChat.agregarUsuarioActivo(nombre);
        }
    }

    // Maneja la respuesta de recuperación de contraseña
    private void manejarRespuestaRecuperacion(RespuestaAutenticarContrasena respuesta) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, respuesta.mensaje);
            if (respuesta.exito) mostrarVentanaLogin();
        });
    }

    // Maneja los mensajes que llegan al chat
    private void manejarMensajeChat(PacketMessage mensaje) {
        if (ventanaChat != null) {
            ventanaChat.recibirMensaje(mensaje);
        }
    }

    private void manejarHistorial(PaqueteHistorial historial) {
        System.out.println("[Cliente] PaqueteHistorial recibido con " + historial.mensajes.size() + " mensajes.");
        if (ventanaChat == null) {
            historialPendiente = historial;
            return;
        }

        mostrarHistorial(historial);
    }
    
    // Método para mostrar el historial
    private void mostrarHistorial(PaqueteHistorial historial) {
        SwingUtilities.invokeLater(() -> {
            for (Map<String, Object> mensaje : historial.mensajes) {
                String usuario = (String) mensaje.get("nombre");
                String contenido = (String) mensaje.get("contenido");
                java.util.Date fecha = (java.util.Date) mensaje.get("fecha_envio");
                java.sql.Timestamp timestamp = new java.sql.Timestamp(fecha.getTime());
                String hora = (timestamp != null)
                        ? new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(timestamp)
                        : "Fecha no disponible";
                boolean esPropio = usuario.equals(nombreUsuario);
                ventanaChat.agregarMensaje(usuario, contenido, esPropio, hora);
            }
        });
    }

    // Métodos modificados/agregados
    @Override
    public void connected(Connection connection) {
        connected = true;
    }

    @Override
    public void disconnected(Connection connection) {
        connected = false;
    }

    public void sendTCP(PacketMessage packet) {
        if (isConnected()) {
            packet.nombreUsuario = "";
            cliente.sendTCP(packet);
        }
    }

    public static boolean isConnected() {
        return connected && cliente != null && cliente.isConnected();
    }

    // Muestra un error de conexión
    private static void mostrarErrorConexion(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, mensaje);
            System.exit(1);
        });
    }
}