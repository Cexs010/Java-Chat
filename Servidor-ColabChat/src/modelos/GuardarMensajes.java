package modelos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class GuardarMensajes {
    
    /**
     * Guarda un mensaje en la base de datos
     * @param conn Conexión a la base de datos
     * @param idUsuario ID del usuario que envía el mensaje
     * @param contenido Contenido del mensaje
     * @return true si se guardó correctamente, false en caso contrario
     */
    public static boolean guardar(Connection conn, int idUsuario, String contenido) {
        if (conn == null) {
            System.err.println("Error: Conexión a BD nula");
            return false;
        }

        String sql = "INSERT INTO mensajes (id_usuario, contenido, fecha_envio) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setString(2, contenido);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al guardar mensaje: " + e.getMessage());
            return false;
        }
    }
}
