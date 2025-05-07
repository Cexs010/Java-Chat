package modelos;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class RegistrarUsuario {

    // Método principal para registrar al usuario
    public static boolean registrar(Connection conn, String nombre, String contraseña, int edad, String ciudadNacimiento) {

        if (conn == null) {//En caso de que la conexión no funcione
            System.out.println("No se pudo establecer conexión con la base de datos.");
            return false;
        }

        try {
            // 1. Encriptar la contraseña
            String contraseñaEncriptada = encriptarSHA256(contraseña);

            // 2. Insertar en la tabla usuarios
            String sqlUsuarios = "INSERT INTO usuarios (nombre, contraseña, fecha_registro) VALUES (?, ?, NOW())";
            PreparedStatement psUsuarios = conn.prepareStatement(sqlUsuarios, Statement.RETURN_GENERATED_KEYS);
            psUsuarios.setString(1, nombre);
            psUsuarios.setString(2, contraseñaEncriptada);
            psUsuarios.executeUpdate();

            // 3. Obtener el ID generado automáticamente
            ResultSet rs = psUsuarios.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);

                // 4. Insertar en datos_personales
                String sqlDatos = "INSERT INTO datos_personales (id_usuario, edad, ciudad_nacimiento) VALUES (?, ?, ?)";
                PreparedStatement psDatos = conn.prepareStatement(sqlDatos);
                psDatos.setInt(1, idUsuario);
                psDatos.setInt(2, edad);
                psDatos.setString(3, ciudadNacimiento);
                psDatos.executeUpdate();

                System.out.println("Usuario registrado exitosamente.");
                return true;
            } else {
                System.out.println("No se pudo obtener el ID del nuevo usuario.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario.");
            e.printStackTrace();
            return false;
        }
    }

    // Método para encriptar contraseña usando SHA-256
    private static String encriptarSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes());

            // Convertimos bytes a hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña.", e);
        }
    }
}
