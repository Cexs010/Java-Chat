package Servidor;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import controladores.*;
import modelos.conexionBD;
import Packages.*;
import java.io.IOException;
import java.util.Map.Entry;

public class Servidor extends Listener {
    
    private static final int PUERTO_TCP = 27960;
    private static Server server;
    
    private static final ArrayList<Connection> conexionesActivas = new ArrayList<>();
    
    //Conexiones de usuarios
    private static final HashMap<Connection, String> conexionesUsuarios = new HashMap<>();
    
    private final ControladorRegistro controladorRegistro = new ControladorRegistro();
    private final ControladorAutenticacion controladorAutenticacion = new ControladorAutenticacion();
    private final ControladorRecuperacionContrasena controladorRecuperacion = new ControladorRecuperacionContrasena();

    public static void main(String[] args) {
        try {
            server = new Server() {
                @Override
                protected Connection newConnection() {
                    return new ConexionCliente();
                }
            };
            
            registrarPaquetes();
            server.bind(PUERTO_TCP);
            server.start();
            server.addListener(new Servidor());
            
            System.out.println("Servidor iniciado en puerto " + PUERTO_TCP);
            
        } catch (IOException e) {
            System.err.println("Error al iniciar servidor: " + e.getMessage());
        }
    }
    
    private static void registrarPaquetes() {
        server.getKryo().register(PacketMessage.class);
        server.getKryo().register(PaqueteRegistro.class);
        server.getKryo().register(RespuestaRegistro.class);
        server.getKryo().register(PaqueteAutenticar.class);
        server.getKryo().register(PaqueteAutenticarContrasena.class);
        server.getKryo().register(RespuestaAutenticarContrasena.class);
        server.getKryo().register(PaqueteHistorial.class);
        server.getKryo().register(PaqueteUsuariosActivos.class);
        server.getKryo().register(java.util.ArrayList.class);
        server.getKryo().register(java.util.HashMap.class);
        server.getKryo().register(java.sql.Timestamp.class);
    }

    @Override
    public void connected(Connection connection) {
        conexionesActivas.add(connection);
        System.out.println("Nueva conexion: " + connection.getID());
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PacketMessage) {
            PacketMessage msg = (PacketMessage) object;
            try (java.sql.Connection conn = new conexionBD().conectarBD()) {
                ControladorMensajes.procesarMensaje(conn, connection, msg);
            } catch (SQLException e) {
                System.err.println("Error al procesar mensaje: " + e.getMessage());
            }
        } 
        else if (object instanceof PaqueteRegistro) {
            procesarRegistro(connection, (PaqueteRegistro) object);
        } 
        else if (object instanceof PaqueteAutenticar) {
            procesarAutenticacion(connection, (PaqueteAutenticar) object);
        } 
        else if (object instanceof PaqueteAutenticarContrasena) {
            procesarRecuperacionContrasena(connection, (PaqueteAutenticarContrasena) object);
        }
        
    }
    
    private void procesarRegistro(Connection c, PaqueteRegistro paquete) {
        try (java.sql.Connection conn = new conexionBD().conectarBD()) {
            controladorRegistro.procesarRegistro(conn, c, paquete);
        } catch (SQLException e) {
            System.err.println("Error en registro: " + e.getMessage());
        }
    }
    
    private void procesarAutenticacion(Connection c, PaqueteAutenticar paquete) {
    try (java.sql.Connection conn = new conexionBD().conectarBD()) {
        controladorAutenticacion.procesarAutenticacion(conn, c, paquete);
        
        if (paquete.autenticado) {
            conexionesUsuarios.put(c, paquete.nombreUsuario);
            System.out.println("Autenticacion exitosa: " + paquete.nombreUsuario 
                             + " (Conexion ID: " + c.getID() + ")");
            
            // Una vez autenticado correctamente, enviar el historial
            ControladorRecuperarHistorial.procesarHistorial(conn, c);
            
            //Mandamos lista de usuarios
            broadcastUsuariosActivos();
            
        } else {
            System.out.println("Intento de autenticacion fallido: " 
                             + paquete.nombreUsuario + " - " + paquete.mensajeError);
        }
    } catch (SQLException e) {
        System.err.println("Error en autenticacion: " + e.getMessage());
    }
}
    
    private void procesarRecuperacionContrasena(Connection c, PaqueteAutenticarContrasena paquete) {
        try (java.sql.Connection conn = new conexionBD().conectarBD()) {
            controladorRecuperacion.procesarRecuperacion(conn, c, paquete);
        } catch (SQLException e) {
            System.err.println("Error en recuperacion: " + e.getMessage());
        }
    }

    @Override
    public void disconnected(Connection c) {
        String usuario = conexionesUsuarios.get(c);
        conexionesActivas.remove(c);
        conexionesUsuarios.remove(c);
        System.out.println("Desconexion: " + c.getID() 
                         + (usuario != null ? " (Usuario: " + usuario + ")" : ""));
        
        //Actualizamos la lista de usuarios activos
        broadcastUsuariosActivos();
    }
    
    public static ArrayList<Connection> getConexionesActivas() {
        synchronized (conexionesActivas) {
            return new ArrayList<>(conexionesActivas);
        }
    }
    
    public static String getNombreUsuario(Connection c) {
        String usuario = conexionesUsuarios.get(c);
        System.out.println("[DEBUG] Consulta usuario para conexion " + c.getID() + ": " 
                          + (usuario != null ? usuario : "NO AUTENTICADO"));
        return usuario;
    }
    
    /** Recolecta la lista actual de usuarios y la envía a todos los clientes */
    private void broadcastUsuariosActivos() {
        // Construir HashMap<ID de conexión, nombre>
        HashMap<Integer, String> mapEnvio = new HashMap<>();
        for (Entry<Connection, String> entry : conexionesUsuarios.entrySet()) {
            mapEnvio.put(entry.getKey().getID(), entry.getValue());
        }

        PaqueteUsuariosActivos paquete = new PaqueteUsuariosActivos(mapEnvio);

        // Enviar a todos
        for (Connection cc : getConexionesActivas()) {
            cc.sendTCP(paquete);
        }
    }
    
    private static class ConexionCliente extends Connection {}
}