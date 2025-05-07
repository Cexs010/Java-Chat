package modelos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AutenticacionUsuario {
    
    /**
     * Método para autenticar un usuario (versión simplificada para el controlador)
     * @param conn Conexión a la base de datos
     * @param nombreUsuario Nombre de usuario
     * @param contraseña Contraseña en texto plano
     * @return true si la autenticación es exitosa, false en caso contrario
     */
    public static boolean autenticar(Connection conn, String nombreUsuario, String contraseña) {
        if (conn == null) {
            System.err.println("Error: Conexión a BD nula");
            return false;
        }

        String sql = "SELECT contraseña FROM usuarios WHERE nombre = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Comparar contraseñas hasheadas
                String hashAlmacenado = rs.getString("contraseña");
                String hashProporcionado = Encriptacion.encriptarSHA256(contraseña);
                return hashAlmacenado.equals(hashProporcionado);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            return false;
        }
    }
}