package controladores;

import com.esotericsoftware.kryonet.Connection;
import modelos.ValidacionRecuperacion;
import modelos.Encriptacion;
import Packages.PaqueteAutenticarContrasena;
import Packages.RespuestaAutenticarContrasena;

public class ControladorRecuperacionContrasena {
    public static void procesarRecuperacion(java.sql.Connection conn, Connection cliente, PaqueteAutenticarContrasena paquete) {
        RespuestaAutenticarContrasena respuesta = new RespuestaAutenticarContrasena();
        
        // Validar datos
        boolean datosCorrectos = ValidacionRecuperacion.validarDatosRecuperacion(
            conn, paquete.nombreUsuario, paquete.edad, paquete.ciudad);
        
        if (datosCorrectos) {
            // Actualizar contraseña
            String nuevaContrasenaHash = Encriptacion.encriptarSHA256(paquete.nuevaContrasena);
            boolean actualizado = actualizarContrasena(conn, paquete.nombreUsuario, nuevaContrasenaHash);
            
            respuesta.exito = actualizado;
            respuesta.mensaje = actualizado ? "Contraseña actualizada exitosamente" : "Error al actualizar contraseña";
        } else {
            respuesta.exito = false;
            respuesta.mensaje = "Datos de recuperación incorrectos";
        }
        
        cliente.sendTCP(respuesta);
    }
    
    private static boolean actualizarContrasena(java.sql.Connection conn, String nombreUsuario, String nuevaContrasenaHash) {
        String sql = "UPDATE usuarios SET contraseña = ? WHERE nombre = ?";
        
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevaContrasenaHash);
            pstmt.setString(2, nombreUsuario);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }
}