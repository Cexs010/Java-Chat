package controladores;

import com.esotericsoftware.kryonet.Connection;
import modelos.GuardarMensajes;
import Packages.PacketMessage;
import Servidor.Servidor;

import java.sql.SQLException;

public class ControladorMensajes {

    public static void procesarMensaje(java.sql.Connection conn,
                                       Connection cliente,
                                       PacketMessage packet) {
        try {
            String nombreUsuario = Servidor.getNombreUsuario(cliente);

            if (nombreUsuario == null) {
                System.err.println("Bloqueado mensaje de cliente no autenticado: " 
                                   + cliente.getID());
                return;
            }

            int idUsuario = obtenerIdUsuario(conn, nombreUsuario);
            if (idUsuario == -1) {
                System.err.println("Usuario no encontrado: " + nombreUsuario);
                return;
            }

            GuardarMensajes.guardar(conn, idUsuario, packet.mensaje);
            packet.nombreUsuario = nombreUsuario;

            // Reenvía a todos los clientes autenticados y conectados
            Servidor.getConexionesActivas().stream()
                .filter(con -> con.isConnected() && Servidor.getNombreUsuario(con) != null)
                .forEach(con -> con.sendTCP(packet));

        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
        }
    }

    private static int obtenerIdUsuario(java.sql.Connection conn, String nombreUsuario) throws SQLException {
        String sql = "SELECT id_usuario FROM usuarios WHERE nombre = ?";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_usuario") : -1;
            }
        }
    }
}
