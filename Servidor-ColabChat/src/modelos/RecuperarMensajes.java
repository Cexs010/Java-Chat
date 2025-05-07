package modelos;

import java.sql.*;
import java.util.*;

public class RecuperarMensajes {

    public List<Map<String, Object>> obtenerMensajesUltimos30Dias(Connection conn) {
        List<Map<String, Object>> mensajes = new ArrayList<>();
        String sql = """
            SELECT u.nombre, m.contenido, m.fecha_envio
            FROM mensajes m
            INNER JOIN usuarios u ON m.id_usuario = u.id_usuario
            WHERE m.fecha_envio >= NOW() - INTERVAL 30 DAY
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("nombre", rs.getString("nombre"));
                fila.put("contenido", rs.getString("contenido"));
                fila.put("fecha_envio", rs.getTimestamp("fecha_envio"));
                mensajes.add(fila);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mensajes;
    }
}