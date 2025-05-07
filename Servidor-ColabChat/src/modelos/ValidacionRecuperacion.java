package modelos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidacionRecuperacion {
    
    /**
     * Método para validar datos de recuperación de cuenta
     * @param conn Conexión activa a la base de datos
     * @param nombre Nombre de usuario a verificar
     * @param edad Edad registrada del usuario
     * @param ciudad Ciudad de nacimiento del usuario
     * @return true si los datos coinciden, false si no coinciden o hay error
     */
    public static boolean validarDatosRecuperacion(Connection conn, String nombre, int edad, String ciudad) {
        if (conn == null) {
            System.out.println("Error: No hay conexión a la base de datos");
            return false;
        }
        
        try {
            String sql = "SELECT COUNT(*) as coincidencias " +
                       "FROM usuarios u " +
                       "JOIN datos_personales d ON u.id_usuario = d.id_usuario " +
                       "WHERE u.nombre = ? AND d.edad = ? AND d.ciudad_nacimiento = ?";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setInt(2, edad);
            ps.setString(3, ciudad);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                boolean datosCorrectos = rs.getInt("coincidencias") > 0;
                if (datosCorrectos) {
                    System.out.println("Validación exitosa para el usuario: " + nombre);
                } else {
                    System.out.println("Datos incorrectos para el usuario: " + nombre);
                }
                return datosCorrectos;
            }
            return false;
            
        } catch (SQLException e) {
            System.out.println("Error al validar datos de recuperación: " + e.getMessage());
            return false;
        }
    }
}
